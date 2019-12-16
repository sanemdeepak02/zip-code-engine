package com.zip.code.engine.domain;

import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Value;

import java.util.List;

/**
 * Created by sanemdeepak on 12/15/19.
 */
@Value
@Builder(builderClassName = "ZipCodeMessageBuilder", toBuilder = true)
public class ZipCodeMessage {
    private final List<ZipCodeRange> zipCodeRanges;

    @JsonPOJOBuilder(withPrefix = "")
    public static class ZipCodeMessageBuilder {

    }
}
