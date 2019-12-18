package com.zip.code.engine.processor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zip.code.engine.domain.ZipCodeMessage;
import com.zip.code.engine.domain.ZipCodeRange;
import com.zip.code.engine.producer.Producer;
import com.zip.code.engine.transformer.Transformer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.Mockito.*;

/**
 * Created by sanemdeepak on 12/17/19.
 */
@RunWith(MockitoJUnitRunner.class)
public class ZipCodeProcessorTest {

    private Producer<Optional<byte[]>, byte[]> kafkaProducer;
    private Transformer<ZipCodeMessage, ZipCodeMessage> zipCodeMessageTransformer;
    private ObjectMapper objectMapper;

    private ZipCodeProcessor zipCodeProcessor;

    @Before
    public void setup() {
        this.kafkaProducer = mock(Producer.class);
        this.objectMapper = mock(ObjectMapper.class);
        this.zipCodeMessageTransformer = mock(Transformer.class);

        this.zipCodeProcessor = new ZipCodeProcessor(
                this.kafkaProducer,
                this.zipCodeMessageTransformer,
                this.objectMapper);
    }

    @Test
    public void processHappyPath() throws Exception {
        ZipCodeMessage zipCodeMessage = ZipCodeMessage
                .builder()
                .zipCodeRanges(Arrays.asList(ZipCodeRange
                        .builder()
                        .start(1)
                        .end(2)
                        .build()))
                .build();
        byte[] messageAsBytes = getZipCodeMessageAsBytes(zipCodeMessage);

        when(this.objectMapper.writeValueAsBytes(zipCodeMessage)).thenReturn(messageAsBytes);
        when(this.zipCodeMessageTransformer.transform(zipCodeMessage)).thenReturn(zipCodeMessage);
        when(this.kafkaProducer.produce(messageAsBytes)).thenReturn(Optional.of(messageAsBytes));

        this.zipCodeProcessor.process(zipCodeMessage);

        verify(this.objectMapper).writeValueAsBytes(zipCodeMessage);
        verify(this.zipCodeMessageTransformer).transform(zipCodeMessage);
        verify(this.kafkaProducer).produce(messageAsBytes);
    }

    @Test
    public void processCalledWithNull() throws Exception {

        this.zipCodeProcessor.process(null);

        verify(this.objectMapper, never()).writeValueAsBytes(any());
        verify(this.zipCodeMessageTransformer,never()).transform(any());
        verify(this.kafkaProducer,never()).produce(any());
    }

    @Test
    public void processObjectMapperThrowsException() throws Exception {
        ZipCodeMessage zipCodeMessage = ZipCodeMessage
                .builder()
                .zipCodeRanges(Arrays.asList(ZipCodeRange
                        .builder()
                        .start(1)
                        .end(2)
                        .build()))
                .build();
        byte[] messageAsBytes = getZipCodeMessageAsBytes(zipCodeMessage);

        when(this.objectMapper.writeValueAsBytes(zipCodeMessage)).thenThrow(new RuntimeException());
        when(this.zipCodeMessageTransformer.transform(zipCodeMessage)).thenReturn(zipCodeMessage);

        this.zipCodeProcessor.process(zipCodeMessage);

        verify(this.zipCodeMessageTransformer).transform(zipCodeMessage);
        verify(this.objectMapper).writeValueAsBytes(zipCodeMessage);
        verify(this.kafkaProducer, never()).produce(messageAsBytes);
    }

    @Test(expected = RuntimeException.class)
    public void processKafkaProducerThrowsException() throws Exception {
        ZipCodeMessage zipCodeMessage = ZipCodeMessage
                .builder()
                .zipCodeRanges(Arrays.asList(ZipCodeRange
                        .builder()
                        .start(1)
                        .end(2)
                        .build()))
                .build();
        byte[] messageAsBytes = getZipCodeMessageAsBytes(zipCodeMessage);

        when(this.objectMapper.writeValueAsBytes(zipCodeMessage)).thenReturn(messageAsBytes);
        when(this.zipCodeMessageTransformer.transform(zipCodeMessage)).thenReturn(zipCodeMessage);
        when(this.kafkaProducer.produce(messageAsBytes)).thenThrow(new RuntimeException());

        this.zipCodeProcessor.process(zipCodeMessage);

        verify(this.objectMapper).writeValueAsBytes(zipCodeMessage);
        verify(this.zipCodeMessageTransformer).transform(zipCodeMessage);
        verify(this.kafkaProducer).produce(messageAsBytes);
    }
    @Test(expected = RuntimeException.class)
    public void processTransformerThrowsException() throws Exception {
        ZipCodeMessage zipCodeMessage = ZipCodeMessage
                .builder()
                .zipCodeRanges(Arrays.asList(ZipCodeRange
                        .builder()
                        .start(1)
                        .end(2)
                        .build()))
                .build();
        byte[] messageAsBytes = getZipCodeMessageAsBytes(zipCodeMessage);


        when(this.zipCodeMessageTransformer.transform(zipCodeMessage)).thenThrow(new RuntimeException());

        this.zipCodeProcessor.process(zipCodeMessage);

        verify(this.objectMapper, never()).writeValueAsBytes(zipCodeMessage);
        verify(this.zipCodeMessageTransformer).transform(zipCodeMessage);
        verify(this.kafkaProducer, never()).produce(messageAsBytes);
    }

    private byte[] getZipCodeMessageAsBytes(ZipCodeMessage zipCodeMessage) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsBytes(zipCodeMessage);
    }
}
