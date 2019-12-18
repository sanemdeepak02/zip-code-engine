package com.zip.code.engine.transformer;

import com.zip.code.engine.domain.ZipCodeMessage;
import com.zip.code.engine.domain.ZipCodeRange;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by sanemdeepak on 12/17/19.
 */
@RunWith(JUnit4.class)
public class ZipCodeTransformerTest {
    Transformer<ZipCodeMessage, ZipCodeMessage> zipCodeMessageTransformer;

    @Before
    public void setup() {
        this.zipCodeMessageTransformer = new ZipCodeTransformer();
    }

    @Test
    public void transformHappyPathOnlyOneRange() {
        ZipCodeMessage zipCodeMessage = ZipCodeMessage
                .builder()
                .zipCodeRanges(Arrays.asList(
                        ZipCodeRange
                                .builder()
                                .start(1)
                                .end(2)
                                .build()))
                .build();
        ZipCodeMessage transform = this.zipCodeMessageTransformer.transform(zipCodeMessage);

        assertNotNull(transform);
        assertEquals(transform.getZipCodeRanges(), zipCodeMessage.getZipCodeRanges());
    }

    @Test(expected = IllegalArgumentException.class)
    public void transformNull() {
        this.zipCodeMessageTransformer.transform(null);
    }

    @Test
    public void transformHappyPathMultipleOverlappingRanges() {
        ZipCodeMessage zipCodeMessage = ZipCodeMessage
                .builder()
                .zipCodeRanges(Arrays.asList(
                        ZipCodeRange
                                .builder()
                                .start(1)
                                .end(9)
                                .build(),
                        ZipCodeRange
                                .builder()
                                .start(3)
                                .end(12)
                                .build()))
                .build();

        ZipCodeRange expected = ZipCodeRange.builder().start(1).end(12).build();

        ZipCodeMessage transform = this.zipCodeMessageTransformer.transform(zipCodeMessage);

        assertNotNull(transform);
        assertNotEquals(zipCodeMessage.getZipCodeRanges(), transform.getZipCodeRanges());
        assertEquals(1, transform.getZipCodeRanges().size());
        assertTrue(transform.getZipCodeRanges().contains(expected));
    }

    @Test
    public void transformHappyPathMultipleOverlappingAndNonOverlappingRanges() {
        ZipCodeMessage zipCodeMessage = ZipCodeMessage
                .builder()
                .zipCodeRanges(Arrays.asList(
                        ZipCodeRange
                                .builder()
                                .start(94133)
                                .end(94133)
                                .build(),
                        ZipCodeRange
                                .builder()
                                .start(94200)
                                .end(94299)
                                .build(),
                        ZipCodeRange
                                .builder()
                                .start(94226)
                                .end(94399)
                                .build()))
                .build();

        List<ZipCodeRange> expected = Arrays.asList(
                ZipCodeRange
                        .builder()
                        .start(94133)
                        .end(94133)
                        .build(),
                ZipCodeRange
                        .builder()
                        .start(94200)
                        .end(94399)
                        .build());

        ZipCodeMessage transform = this.zipCodeMessageTransformer.transform(zipCodeMessage);

        assertNotNull(transform);
        assertNotEquals(zipCodeMessage.getZipCodeRanges(), transform.getZipCodeRanges());
        assertEquals(2, transform.getZipCodeRanges().size());
        assertTrue(transform.getZipCodeRanges().containsAll(expected));
    }
}
