package com.zip.code.engine.producer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.Objects;
import java.util.Optional;

/**
 * Created by sanemdeepak on 12/15/19.
 */

@Slf4j
@Component
public class KafkaProducer implements Producer<Optional<byte[]>, byte[]> {

    private final KafkaTemplate<byte[], byte[]> kafkaTemplate;
    private final String destinationTopic;

    public KafkaProducer(KafkaTemplate<byte[], byte[]> kafkaTemplate,
                         @Value("${kafka.producer.topic}") String destinationTopic) {
        this.kafkaTemplate = kafkaTemplate;
        this.destinationTopic = destinationTopic;
    }

    @Override
    public Optional<byte[]> produce(byte[] message) {
        return this.produce(message, this.destinationTopic);
    }

    @Override
    public Optional<byte[]> produce(byte[] message, String destination) {
        if (Objects.isNull(message)) {
            log.debug("Message to produce cannot be null, ignoring message");
            return Optional.empty();
        }
        try {
            this.kafkaTemplate
                    .send(this.destinationTopic, message)
                    .addCallback(new ListenableFutureCallback<SendResult<byte[], byte[]>>() {
                        @Override
                        public void onFailure(Throwable ex) {
                            log.error("ERROR while sending message to: {}, message: {}", destinationTopic, new String(message), ex);
                            throw new RuntimeException(ex);
                        }

                        @Override
                        public void onSuccess(SendResult<byte[], byte[]> result) {
                            log.info("Successfully sent message to: {}, message: {}", destinationTopic, new String(message));
                        }
                    });

            return Optional.of(message);

        } catch (Exception exp) {
            return Optional.empty();
        }
    }
}
