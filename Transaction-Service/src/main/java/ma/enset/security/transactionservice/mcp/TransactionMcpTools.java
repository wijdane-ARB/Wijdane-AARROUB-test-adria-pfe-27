package ma.enset.security.transactionservice.mcp;

import com.adria.bank.transaction.domain.Transaction;
import com.adria.bank.transaction.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TransactionMcpTools {

    private final TransactionService service;

    @Tool(description = "Liste les transactions d'un compte (ou toutes si accountId est vide)")
    public List<Transaction> listTransactions(@ToolParam(description = "UUID du compte, optionnel", required = false) String accountId) {
        return service.findAll(accountId == null || accountId.isBlank() ? null : UUID.fromString(accountId));
    }

    @Tool(description = "Enregistre une transaction (debit) sur un compte. Le montant doit etre positif, <= plafond, compte actif, meme devise que le compte")
    public Transaction createTransaction(@ToolParam(description = "UUID du compte") String accountId,
                                         @ToolParam(description = "Montant strictement positif") double amount,
                                         @ToolParam(description = "Devise, ex: EUR") String currency,
                                         @ToolParam(description = "Categorie du marchand", required = false) String merchantCategory,
                                         @ToolParam(description = "Pays", required = false) String country) {
        return service.create(UUID.fromString(accountId), BigDecimal.valueOf(amount), currency, merchantCategory, country);
    }

    @Tool(description = "Effectue un virement d'un compte source vers un compte cible")
    public Transaction transfer(@ToolParam(description = "UUID du compte source") String sourceAccountId,
                                @ToolParam(description = "UUID du compte cible") String targetAccountId,
                                @ToolParam(description = "Montant strictement positif") double amount,
                                @ToolParam(description = "Pays", required = false) String country) {
        return service.transfer(UUID.fromString(sourceAccountId), UUID.fromString(targetAccountId),
                BigDecimal.valueOf(amount), country);
    }
}
