package com.zip.code.engine.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zip.code.engine.domain.ErrorMessage;
import com.zip.code.engine.domain.ZipCodeMessage;
import com.zip.code.engine.processor.ZipCodeProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;

/**
 * Created by sanemdeepak on 12/15/19.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ZipCodeListener {

    private final ObjectMapper objectMapper;
    private final ZipCodeProcessor zipCodeProcessor;
    private final ErrorHandler errorHandler;


    @KafkaListener(topics = "${kafka.consumer.topic.raw.zip-code}", groupId = "${kafka.consumer.topic.raw.group-id}")
    public void listen(byte[] bytes) {
        try {
            if (Objects.nonNull(bytes)) {
                this.toZipCodeMessage(bytes)
                        .ifPresent(zipCodeProcessor::process);
            }
        } catch (Exception exp) {
            this.errorHandler.handleError(
                    ErrorMessage
                            .builder()
                            .failedMessage(bytes)
                            .cause(ExceptionUtils.getStackTrace(exp))
                            .build());

        }
    }

    private Optional<ZipCodeMessage> toZipCodeMessage(byte[] bytes) {
        try {
            return Optional.of(this.objectMapper.readValue(bytes, ZipCodeMessage.class));
        } catch (Exception e) {
            log.error("ERROR converting byes to ZipCodeMessage, from: {}", new String(bytes), e);
            return Optional.empty();
        }
    }
}
