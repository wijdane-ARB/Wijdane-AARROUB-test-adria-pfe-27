package ma.enset.security.transactionservice.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class TransactionEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void onTransactionCreated(TransactionCreatedEvent event) {
        try {
            // cle du message = accountId (ordre garanti par compte)
            kafkaTemplate.send(KafkaTopicConfig.TOPIC, event.accountId().toString(), event)
                    .whenComplete((result, ex) -> {
                        if (ex != null) log.error("Echec publication Kafka pour {}", event.transactionId(), ex);
                        else log.info("Evenement publie : topic={} partition={} offset={}",
                                KafkaTopicConfig.TOPIC, result.getRecordMetadata().partition(),
                                result.getRecordMetadata().offset());
                    });
        } catch (Exception e) {
            log.error("Kafka indisponible, evenement non publie pour {}", event.transactionId(), e);
        }
    }
}
