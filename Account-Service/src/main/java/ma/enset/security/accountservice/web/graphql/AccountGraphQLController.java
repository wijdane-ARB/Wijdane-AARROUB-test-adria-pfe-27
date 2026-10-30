package ma.enset.security.accountservice.web.graphql;

import lombok.RequiredArgsConstructor;
import ma.enset.security.accountservice.domain.Account;
import ma.enset.security.accountservice.domain.AccountStatus;
import ma.enset.security.accountservice.service.AccountService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class AccountGraphQLController {

    public record CreateAccountInput(String customerReference, String currency, double balance) {}

    private final AccountService service;

    @QueryMapping
    public List<Account> accounts() { return service.findAll(); }

    @QueryMapping
    public Account accountById(@Argument UUID id) { return service.findById(id); }

    @MutationMapping
    public Account createAccount(@Argument CreateAccountInput input) {
        return service.create(input.customerReference(), input.currency(), input.balance());
    }

    @MutationMapping
    public Account updateAccountStatus(@Argument UUID id, @Argument AccountStatus status) {
        return service.updateStatus(id, status);
    }
}
