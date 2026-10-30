package com.adria.bank.account.mcp;

import com.adria.bank.account.domain.Account;
import com.adria.bank.account.domain.AccountStatus;
import com.adria.bank.account.service.AccountService;
import lombok.RequiredArgsConstructor;
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

    @Tool(description = "Retourne un compte bancaire (solde, statut, devise) a partir de son identifiant UUID")
    public Account getAccount(@ToolParam(description = "UUID du compte") String accountId) {
        return service.findById(UUID.fromString(accountId));
    }

    @Tool(description = "Cree un nouveau compte bancaire actif")
    public Account createAccount(@ToolParam(description = "Reference client") String customerReference,
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
