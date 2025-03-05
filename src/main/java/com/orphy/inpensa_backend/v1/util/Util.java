package com.orphy.inpensa_backend.v1.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.function.Supplier;

public class Util {

    private final static Logger LOGGER = LoggerFactory.getLogger(Util.class);
    public static <T> Optional<T> tryOrElseWithOpt(Supplier<T> fn) {
        try {
            return Optional.of(fn.get());
        } catch (Exception e) {
            LOGGER.warn("Error thrown and caught using try or else.", e);
            return Optional.empty();
        }
    }

    public static <T> T tryOrElse(Supplier<T> fn, Supplier<? extends RuntimeException> exSupplier) {
        try {
            return fn.get();
        } catch (Exception e ) {
            LOGGER.warn("Error thrown and caught using try or else.", e);
            throw exSupplier.get();
        }
    }

    public static void tryOrElse(Runnable runnable, Supplier<? extends RuntimeException> exSupplier) {
        try {
            runnable.run();
        } catch (Exception e ) {
            LOGGER.warn("Error thrown and caught using try or else.", e);
            throw exSupplier.get();
        }
    }
}
