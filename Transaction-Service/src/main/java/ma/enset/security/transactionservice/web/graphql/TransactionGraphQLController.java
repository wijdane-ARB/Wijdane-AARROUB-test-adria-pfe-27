package ma.enset.security.transactionservice.web.graphql;

import lombok.RequiredArgsConstructor;
import ma.enset.security.transactionservice.domain.Transaction;
import ma.enset.security.transactionservice.service.TransactionService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class TransactionGraphQLController {

    public record CreateTransactionInput(UUID accountId, BigDecimal amount, String currency,
                                         String merchantCategory, String country) {
    }

    public record TransferInput(UUID sourceAccountId, UUID targetAccountId, BigDecimal amount, String country) {
    }

    private final TransactionService service;

    @QueryMapping
    public List<Transaction> transactions(@Argument UUID accountId) {
        return service.findAll(accountId);
    }

    @QueryMapping
    public Transaction transactionById(@Argument UUID id) {
        return service.findById(id);
    }

    @MutationMapping
    public Transaction createTransaction(@Argument CreateTransactionInput input) {
        return service.create(input.accountId(), input.amount(), input.currency(),
                input.merchantCategory(), input.country());
    }

    @MutationMapping
    public Transaction transfer(@Argument TransferInput input) {
        return service.transfer(input.sourceAccountId(), input.targetAccountId(), input.amount(), input.country());
    }
}
