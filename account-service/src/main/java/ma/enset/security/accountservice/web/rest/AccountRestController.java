package ma.enset.security.accountservice.web.rest;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import ma.enset.security.accountservice.domain.Account;
import ma.enset.security.accountservice.domain.AccountStatus;
import ma.enset.security.accountservice.service.AccountService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountRestController {

    public record CreateAccountRequest(@NotBlank String customerReference,
                                       @NotBlank String currency,
                                       @PositiveOrZero double balance) {}

    private final AccountService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Account create(@Valid @RequestBody CreateAccountRequest r) {
        return service.create(r.customerReference(), r.currency(), r.balance());
    }

    @GetMapping
    public List<Account> findAll() { return service.findAll(); }

    @GetMapping("/{id}")
    public Account findById(@PathVariable UUID id) { return service.findById(id); }

    @PatchMapping("/{id}/status")
    public Account updateStatus(@PathVariable UUID id, @RequestParam AccountStatus status) {
        return service.updateStatus(id, status);
    }

    @PostMapping("/{id}/debit")
    public Account debit(@PathVariable UUID id, @RequestParam double amount) {
        return service.debit(id, amount);
    }

    @PostMapping("/{id}/credit")
    public Account credit(@PathVariable UUID id, @RequestParam double amount) {
        return service.credit(id, amount);
    }
}