package com.zip.code.engine.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import com.zip.code.engine.domain.ErrorMessage;
import com.zip.code.engine.listener.ErrorHandler;
import com.zip.code.engine.producer.Producer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ErrorHandlerTest {

    private Producer<Optional<byte[]>, byte[]> producerMock;
    private ObjectMapper objectMapperMock;
    private String errorTopic;

    private Faker faker = new Faker();

    private ErrorHandler errorHandler;

    @Before
    public void setUp() {

        this.producerMock = mock(Producer.class);
        this.objectMapperMock = mock(ObjectMapper.class);
        this.errorTopic = this.faker.funnyName().name();

        this.errorHandler = new ErrorHandler(
                this.producerMock,
                this.objectMapperMock,
                this.errorTopic);
    }

    @Test
    public void handleErrorHappyPath() throws JsonProcessingException {
        String failedMessage = "message";
        byte[] failedMessageBytes = failedMessage.getBytes();
        ErrorMessage errorMessage = ErrorMessage
                .builder()
                .cause("cause")
                .failedMessage(failedMessageBytes)
                .build();

        byte[] errorMessageBytes = getBytes(errorHandler);
        when(this.objectMapperMock.writeValueAsBytes(errorMessage)).thenReturn(errorMessageBytes);
        when(producerMock.produce(errorMessageBytes, this.errorTopic)).thenReturn(Optional.of(errorMessageBytes));

        errorHandler.handleError(errorMessage);

        verify(objectMapperMock).writeValueAsBytes(errorMessage);
        verify(producerMock).produce(errorMessageBytes, this.errorTopic);
    }

    @Test
    public void handleErrorObjectMapperThrowsExceptin() throws JsonProcessingException {
        String failedMessage = "message";
        byte[] failedMessageBytes = failedMessage.getBytes();
        ErrorMessage errorMessage = ErrorMessage
                .builder()
                .cause("cause")
                .failedMessage(failedMessageBytes)
                .build();

        byte[] errorMessageBytes = getBytes(errorHandler);
        when(this.objectMapperMock.writeValueAsBytes(errorMessage)).thenThrow(new RuntimeException());

        errorHandler.handleError(errorMessage);

        verify(objectMapperMock).writeValueAsBytes(errorMessage);
        verify(producerMock, never()).produce(errorMessageBytes, this.errorTopic);
    }

    private byte[] getBytes(Object o) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsBytes(o);
    }
}