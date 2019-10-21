package com.vortex.handbrake.domain;

import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class OperationResult<S, E> {
    private final S success;
    private final E error;

    public OperationResult(S success, E error) {
        this.success = success;
        this.error = error;
    }
}
