package com.zip.code.engine.producer;

import com.github.javafaker.Faker;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.util.concurrent.ListenableFuture;

import java.util.Optional;

import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.*;

/**
 * Created by sanemdeepak on 12/17/19.
 */
@RunWith(MockitoJUnitRunner.class)
public class KafkaProducerTest {

    private KafkaTemplate<byte[], byte[]> kafkaTemplate;
    private String destinationTopic;

    private Faker faker;

    private Producer<Optional<byte[]>, byte[]> kafkaProducer;

    @Before
    public void setup() {
        this.faker = new Faker();
        this.destinationTopic = faker.funnyName().name();
        this.kafkaTemplate = mock(KafkaTemplate.class);

        this.kafkaProducer = new KafkaProducer(this.kafkaTemplate, this.destinationTopic);
    }

    @Test
    public void produceHappyPath() {
        byte[] data = "data".getBytes();
        when(kafkaTemplate.send(this.destinationTopic, data)).thenReturn(mock(ListenableFuture.class));

        kafkaProducer.produce(data);

        verify(kafkaTemplate).send(this.destinationTopic, data);
    }

    @Test
    public void produceFailure() {
        byte[] data = "data".getBytes();
        when(kafkaTemplate.send(this.destinationTopic, data)).thenThrow(new RuntimeException());

        Optional<byte[]> optional = kafkaProducer.produce(data);

        assertFalse(optional.isPresent());
    }
    @Test
    public void produceCalledWithNull() {
        Optional<byte[]> optional = kafkaProducer.produce(null);

        assertFalse(optional.isPresent());
    }
}
