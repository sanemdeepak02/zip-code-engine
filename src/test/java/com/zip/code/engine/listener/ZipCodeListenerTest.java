package com.zip.code.engine.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zip.code.engine.domain.ErrorMessage;
import com.zip.code.engine.domain.ZipCodeMessage;
import com.zip.code.engine.domain.ZipCodeRange;
import com.zip.code.engine.processor.ZipCodeProcessor;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;

import static org.mockito.Mockito.*;

/**
 * Created by sanemdeepak on 12/17/19.
 */
@RunWith(MockitoJUnitRunner.class)
public class ZipCodeListenerTest {

    private ObjectMapper objectMapperMock;
    private ZipCodeProcessor zipCodeProcessorMock;
    private ErrorHandler errorHandlerMock;

    private ZipCodeListener zipCodeListener;


    @Before
    public void setup() {
        this.objectMapperMock = mock(ObjectMapper.class);
        this.zipCodeProcessorMock = mock(ZipCodeProcessor.class);
        this.errorHandlerMock = mock(ErrorHandler.class);

        this.zipCodeListener = new ZipCodeListener(
                this.objectMapperMock,
                this.zipCodeProcessorMock,
                this.errorHandlerMock);
    }

    @Test
    public void listenHappyPath() throws Exception {
        ZipCodeMessage zipCodeMessage = ZipCodeMessage
                .builder()
                .zipCodeRanges(Arrays.asList(ZipCodeRange
                        .builder()
                        .start(1)
                        .end(2)
                        .build()))
                .build();

        byte[] validMessageAsBytes = getZipCodeMessageAsBytes(zipCodeMessage);

        when(this.objectMapperMock.readValue(validMessageAsBytes, ZipCodeMessage.class)).thenReturn(zipCodeMessage);
        doNothing().when(this.zipCodeProcessorMock).process(zipCodeMessage);

        this.zipCodeListener.listen(validMessageAsBytes);

        verify(this.objectMapperMock).readValue(validMessageAsBytes, ZipCodeMessage.class);
        verify(this.zipCodeProcessorMock).process(zipCodeMessage);
    }

    @Test
    public void listenCalledWithNull() throws Exception {


        this.zipCodeListener.listen(null);

        verifyZeroInteractions(this.objectMapperMock);
        verifyZeroInteractions(this.zipCodeProcessorMock);
    }

    @Test
    public void listenObjectMapperThrowsException() throws Exception {
        ZipCodeMessage zipCodeMessage = ZipCodeMessage
                .builder()
                .zipCodeRanges(Arrays.asList(ZipCodeRange
                        .builder()
                        .start(1)
                        .end(2)
                        .build()))
                .build();

        byte[] validMessageAsBytes = getZipCodeMessageAsBytes(zipCodeMessage);

        when(this.objectMapperMock.readValue(validMessageAsBytes, ZipCodeMessage.class)).thenThrow(new RuntimeException());

        this.zipCodeListener.listen(validMessageAsBytes);

        verify(this.objectMapperMock).readValue(validMessageAsBytes, ZipCodeMessage.class);
        verify(this.zipCodeProcessorMock, never()).process(zipCodeMessage);
    }

    @Test
    public void listenProcessorThrowsException() throws Exception {
        ZipCodeMessage zipCodeMessage = ZipCodeMessage
                .builder()
                .zipCodeRanges(Arrays.asList(ZipCodeRange
                        .builder()
                        .start(1)
                        .end(2)
                        .build()))
                .build();

        byte[] validMessageAsBytes = getZipCodeMessageAsBytes(zipCodeMessage);

        when(this.objectMapperMock.readValue(validMessageAsBytes, ZipCodeMessage.class)).thenReturn(zipCodeMessage);
        doThrow(new RuntimeException()).when(this.zipCodeProcessorMock).process(zipCodeMessage);

        this.zipCodeListener.listen(validMessageAsBytes);

        verify(this.objectMapperMock).readValue(validMessageAsBytes, ZipCodeMessage.class);
        verify(this.zipCodeProcessorMock).process(zipCodeMessage);
        verify(this.errorHandlerMock).handleError(any(ErrorMessage.class));
    }

    private byte[] getZipCodeMessageAsBytes(ZipCodeMessage zipCodeMessage) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsBytes(zipCodeMessage);
    }
}
