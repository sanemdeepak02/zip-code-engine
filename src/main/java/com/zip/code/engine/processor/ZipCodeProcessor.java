package com.zip.code.engine.processor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zip.code.engine.domain.ZipCodeMessage;
import com.zip.code.engine.producer.Producer;
import com.zip.code.engine.transformer.Transformer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;

/**
 * Created by sanemdeepak on 12/15/19.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ZipCodeProcessor {

    private final Producer<Optional<byte[]>, byte[]> kafkaProducer;
    private final Transformer<ZipCodeMessage, ZipCodeMessage> zipCodeMessageTransformer;
    private final ObjectMapper objectMapper;

    public void process(ZipCodeMessage zipCodeMessage) {
        if (Objects.nonNull(zipCodeMessage)) {
            ZipCodeMessage transformedMessage = zipCodeMessageTransformer.transform(zipCodeMessage);
            this.toBytes(transformedMessage).ifPresent(kafkaProducer::produce);
        } else {
            log.debug("zipCodeMessage must not be null, ignoring processing");
        }
    }

    private Optional<byte[]> toBytes(ZipCodeMessage zipCodeMessage) {
        try {
            return Optional.ofNullable(this.objectMapper.writer().writeValueAsBytes(zipCodeMessage));
        } catch (Exception e) {
            log.error("ERROR converting zipCodeMessage to bytes, message: {}", zipCodeMessage, e);
            return Optional.empty();
        }
    }
}
