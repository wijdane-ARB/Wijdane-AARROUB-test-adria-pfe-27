package ma.enset.security.transactionservice.client;

import feign.FeignException;
import ma.enset.security.transactionservice.service.BusinessRuleException;
import ma.enset.security.transactionservice.service.NotFoundException;
import ma.enset.security.transactionservice.service.ServiceUnavailableException;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class AccountClientFallbackFactory implements FallbackFactory<AccountClient> {

    @Override
    public AccountClient create(Throwable cause) {
        return new AccountClient() {
            @Override public AccountDto getAccount(UUID id) { throw translate(cause); }
            @Override public AccountDto debit(UUID id, double amount) { throw translate(cause); }
            @Override public AccountDto credit(UUID id, double amount) { throw translate(cause); }
        };
    }

    private RuntimeException translate(Throwable cause) {
        if (cause instanceof FeignException.NotFound) return new NotFoundException("Compte introuvable");
        if (cause instanceof FeignException.BadRequest)
            return new BusinessRuleException("Refuse par account-service : " + cause.getMessage());
        return new ServiceUnavailableException(
                "account-service indisponible (circuit breaker / timeout) : " + cause.getClass().getSimpleName());
    }
}
