package ma.enset.security.transactionservice.web.rest;


import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import ma.enset.security.transactionservice.domain.Transaction;
import ma.enset.security.transactionservice.service.TransactionService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionRestController {

    public record CreateTransactionRequest(@NotNull UUID accountId, @NotNull @Positive BigDecimal amount,
                                           @NotBlank String currency, String merchantCategory, String country) {
    }

    public record TransferRequest(@NotNull UUID sourceAccountId, @NotNull UUID targetAccountId,
                                  @NotNull @Positive BigDecimal amount, String country) {
    }

    private final TransactionService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Transaction create(@Valid @RequestBody CreateTransactionRequest r) {
        return service.create(r.accountId(), r.amount(), r.currency(), r.merchantCategory(), r.country());
    }

    @PostMapping("/transfer")
    @ResponseStatus(HttpStatus.CREATED)
    public Transaction transfer(@Valid @RequestBody TransferRequest r) {
        return service.transfer(r.sourceAccountId(), r.targetAccountId(), r.amount(), r.country());
    }

    @GetMapping
    public List<Transaction> findAll(@RequestParam(required = false) UUID accountId) {
        return service.findAll(accountId);
    }

    @GetMapping("/{id}")
    public Transaction findById(@PathVariable UUID id) {
        return service.findById(id);
    }
}
