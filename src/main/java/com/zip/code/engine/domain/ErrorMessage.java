package com.zip.code.engine.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

/**
 * Created by sanemdeepak on 12/15/19.
 */
@Value
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ErrorMessage {
    private byte[] failedMessage;
    private String cause;
}
