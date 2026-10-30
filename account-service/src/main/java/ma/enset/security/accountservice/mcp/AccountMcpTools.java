package ma.enset.security.accountservice.mcp;

import lombok.RequiredArgsConstructor;
import ma.enset.security.accountservice.domain.Account;
import ma.enset.security.accountservice.domain.AccountStatus;
import ma.enset.security.accountservice.service.AccountService;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AccountMcpTools {

    private final AccountService service;

    @Tool(description = "Liste tous les comptes bancaires")
    public List<Account> listAccounts() { return service.findAll(); }

    @Tool(description = "Retourne un compte bancaire (solde, statut, devise) à partir de son identifiant UUID")
    public Account getAccount(@ToolParam(description = "UUID du compte") String accountId) {
        return service.findById(UUID.fromString(accountId));
    }

    @Tool(description = "Crée un nouveau compte bancaire actif")
    public Account createAccount(@ToolParam(description = "Référence client") String customerReference,
                                 @ToolParam(description = "Devise ISO, ex: EUR, MAD") String currency,
                                 @ToolParam(description = "Solde initial") double balance) {
        return service.create(customerReference, currency, balance);
    }

    @Tool(description = "Active ou bloque un compte (status = ACTIVE ou BLOCKED)")
    public Account updateAccountStatus(@ToolParam(description = "UUID du compte") String accountId,
                                       @ToolParam(description = "ACTIVE ou BLOCKED") String status) {
        return service.updateStatus(UUID.fromString(accountId), AccountStatus.valueOf(status.toUpperCase()));
    }
}