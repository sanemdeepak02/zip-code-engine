package com.zip.code.engine.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zip.code.engine.domain.ErrorMessage;
import com.zip.code.engine.producer.Producer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Created by sanemdeepak on 12/15/19.
 */
@Component
@Slf4j
public class ErrorHandler {

    private final Producer<Optional<byte[]>, byte[]> kafkaProducer;
    private final ObjectMapper objectMapper;
    private final String errorTopic;

    public ErrorHandler(Producer<Optional<byte[]>, byte[]> kafkaProducer,
                        ObjectMapper objectMapper,
                        @Value("${kafka.producer.error-topic}") String errorTopic) {
        this.kafkaProducer = kafkaProducer;
        this.objectMapper = objectMapper;
        this.errorTopic = errorTopic;
    }

    public void handleError(ErrorMessage errorMessage) {
        this.toBytes(errorMessage).ifPresent(val -> this.kafkaProducer.produce(val, this.errorTopic));
    }

    private Optional<byte[]> toBytes(Object o) {
        try {
            return Optional.of(this.objectMapper.writer().writeValueAsBytes(o));
        } catch (Exception e) {
            log.error("ERROR converting object to bytes, message: {}", o, e);
            return Optional.empty();
        }
    }
}
