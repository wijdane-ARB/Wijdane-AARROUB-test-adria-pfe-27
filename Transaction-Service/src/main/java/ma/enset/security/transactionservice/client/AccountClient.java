package ma.enset.security.transactionservice.client;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@FeignClient(name = "ACCOUNT-SERVICE", path = "/api/accounts", fallbackFactory = AccountClientFallbackFactory.class)
public interface AccountClient {

    @GetMapping("/{id}")
    AccountDto getAccount(@PathVariable("id") UUID id);

    @PostMapping("/{id}/debit")
    AccountDto debit(@PathVariable("id") UUID id, @RequestParam("amount") double amount);

    @PostMapping("/{id}/credit")
    AccountDto credit(@PathVariable("id") UUID id, @RequestParam("amount") double amount);
}
