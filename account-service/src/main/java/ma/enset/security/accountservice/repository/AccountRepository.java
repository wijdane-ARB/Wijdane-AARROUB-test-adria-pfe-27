package ma.enset.security.accountservice.repository;

import ma.enset.security.accountservice.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AccountRepository extends JpaRepository<Account, UUID> {
}
