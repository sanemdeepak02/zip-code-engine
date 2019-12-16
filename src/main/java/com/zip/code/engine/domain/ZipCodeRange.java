package com.zip.code.engine.domain;

import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Value;

/**
 * Created by sanemdeepak on 12/15/19.
 */
@Value
@Builder(builderClassName = "ZipCodeRangeBuilder", toBuilder = true)
public class ZipCodeRange {
    private final int start;
    private final int end;

    @JsonPOJOBuilder(withPrefix = "")
    public static class ZipCodeRangeBuilder {

    }
}
