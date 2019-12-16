package com.zip.code.engine.configuration;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by sanemdeepak on 12/15/19.
 */
@Configuration
public class KafkaProducerConfig {
    private final String bootstrapServers;
    private final String producerClientId;

    public KafkaProducerConfig(@Value("${kafka.producer.bootstrap-servers}") String bootstrapServers,
                               @Value("${kafka.producer.client-id}") String producerClientId) {
        this.bootstrapServers = bootstrapServers;
        this.producerClientId = producerClientId;
    }

    @Bean
    public KafkaTemplate<byte[], byte[]> kafkaTemplate() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, this.bootstrapServers);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, ByteArraySerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, ByteArraySerializer.class);

        return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(configProps));
    }
}
