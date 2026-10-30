package ma.enset.security.transactionservice.service;

import com.adria.bank.transaction.client.AccountClient;
import com.adria.bank.transaction.client.AccountDto;
import com.adria.bank.transaction.domain.Transaction;
import com.adria.bank.transaction.messaging.TransactionCreatedEvent;
import com.adria.bank.transaction.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

/** Couche service unique, appelee par REST, GraphQL et MCP. */
@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository repository;
    private final AccountClient accounts;
    private final ApplicationEventPublisher events;

    // partage via config-service : bank.transaction.max-amount=1000
    @Value("${bank.transaction.max-amount:1000}")
    private BigDecimal maxAmount;

    @Transactional
    public Transaction create(UUID accountId, BigDecimal amount, String currency,
                              String merchantCategory, String country) {
        checkAmount(amount);

        AccountDto account = accounts.getAccount(accountId);          // existence + consultation du solde
        if (!"ACTIVE".equals(account.status())) throw new BusinessRuleException("Le compte n'est pas actif");
        if (!account.currency().equalsIgnoreCase(currency))
            throw new BusinessRuleException("Devise de la transaction (" + currency
                    + ") differente de celle du compte (" + account.currency() + ")");
        if (account.balance() < amount.doubleValue()) throw new BusinessRuleException("Solde insuffisant");

        Transaction saved = repository.save(Transaction.builder()
                .accountId(accountId)
                .amount(amount)
                .currency(currency.toUpperCase())
                .merchantCategory(merchantCategory)
                .country(country)
                .createdAt(Instant.now())
                .build());

        accounts.debit(accountId, amount.doubleValue());               // mise a jour du solde apres enregistrement
        events.publishEvent(TransactionCreatedEvent.from(saved));      // envoye a Kafka apres commit
        return saved;
    }

    /** Virement : debite le compte source (transaction TRANSFER) puis credite le compte cible. */
    @Transactional
    public Transaction transfer(UUID sourceId, UUID targetId, BigDecimal amount, String country) {
        if (sourceId.equals(targetId)) throw new BusinessRuleException("Comptes source et cible identiques");
        checkAmount(amount);

        AccountDto source = accounts.getAccount(sourceId);
        AccountDto target = accounts.getAccount(targetId);
        if (!"ACTIVE".equals(target.status())) throw new BusinessRuleException("Le compte cible n'est pas actif");
        if (!source.currency().equalsIgnoreCase(target.currency()))
            throw new BusinessRuleException("Les deux comptes doivent avoir la meme devise");

        Transaction tx = create(sourceId, amount, source.currency(), "TRANSFER", country);
        try {
            accounts.credit(targetId, amount.doubleValue());
        } catch (RuntimeException e) {
            accounts.credit(sourceId, amount.doubleValue());           // compensation du debit
            throw e;                                                   // rollback de la transaction locale
        }
        return tx;
    }

    public List<Transaction> findAll(UUID accountId) {
        return accountId == null ? repository.findAll()
                : repository.findByAccountIdOrderByCreatedAtDesc(accountId);
    }

    public Transaction findById(UUID id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException("Transaction introuvable : " + id));
    }

    private void checkAmount(BigDecimal amount) {
        if (amount == null || amount.signum() <= 0)
            throw new BusinessRuleException("Le montant doit etre strictement positif");
        if (amount.compareTo(maxAmount) > 0)
            throw new BusinessRuleException("Le montant depasse le plafond autorise (" + maxAmount + ")");
    }
}
