package ma.enset.security.accountservice.service;


import lombok.RequiredArgsConstructor;
import ma.enset.security.accountservice.domain.Account;
import ma.enset.security.accountservice.domain.AccountStatus;
import ma.enset.security.accountservice.repository.AccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository repository;

    public Account create(String customerReference, String currency, double balance) {
        if (balance < 0) throw new BusinessRuleException("Le solde initial ne peut pas etre negatif");
        return repository.save(Account.builder()
                .customerReference(customerReference)
                .status(AccountStatus.ACTIVE)
                .currency(currency.toUpperCase())
                .balance(round(balance))
                .build());
    }

    public List<Account> findAll() {
        return repository.findAll();
    }

    public Account findById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Compte introuvable : " + id));
    }

    @Transactional
    public Account updateStatus(UUID id, AccountStatus status) {
        Account account = findById(id);
        account.setStatus(status);
        return account;
    }

    @Transactional
    public Account debit(UUID id, double amount) {
        checkPositive(amount);
        Account account = activeAccount(id);
        if (account.getBalance() < amount) throw new BusinessRuleException("Solde insuffisant");
        account.setBalance(round(account.getBalance() - amount));
        return account;
    }

    @Transactional
    public Account credit(UUID id, double amount) {
        checkPositive(amount);
        Account account = activeAccount(id);
        account.setBalance(round(account.getBalance() + amount));
        return account;
    }

    private Account activeAccount(UUID id) {
        Account account = findById(id);
        if (account.getStatus() != AccountStatus.ACTIVE) throw new BusinessRuleException("Compte bloque");
        return account;
    }

    private void checkPositive(double amount) {
        if (amount <= 0) throw new BusinessRuleException("Le montant doit etre strictement positif");
    }

    private double round(double v) {
        return Math.round(v * 100.0) / 100.0;
    }
}
