package com.vortex.handbrake.encoder;

import java.io.File;
import java.math.BigDecimal;
import java.util.function.Consumer;

public interface EncoderStrategy {

    File encode(File srcFile, Consumer<String> logConsumer, Consumer<BigDecimal> progressConsumer);

    void terminate();
}
