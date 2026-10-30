package ma.enset.security.transactionservice.messaging;

import ma.enset.security.transactionservice.domain.Transaction;

import java.math.BigDecimal;
import java.util.UUID;

public record TransactionCreatedEvent(UUID eventId, String eventType, UUID transactionId, UUID accountId,
                                      BigDecimal amount, String currency, String occurredAt) {

    public static TransactionCreatedEvent from(Transaction t) {
        return new TransactionCreatedEvent(UUID.randomUUID(), "TransactionCreated", t.getId(), t.getAccountId(),
                t.getAmount(), t.getCurrency(), t.getCreatedAt().toString());
    }
}

