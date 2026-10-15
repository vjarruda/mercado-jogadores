package br.com.passe.shared.util;

import br.com.passe.shared.exception.NotFoundException;

import java.util.regex.Pattern;

public final class IdParser {

    private static final Pattern POSITIVE_INTEGER = Pattern.compile("[1-9][0-9]{0,9}");

    private IdParser() {
    }

    public static int parseOrNotFound(String rawId, String resourceName) {
        if (rawId == null || !POSITIVE_INTEGER.matcher(rawId).matches()) {
            throw notFound(resourceName);
        }
        long value = Long.parseLong(rawId);
        if (value > Integer.MAX_VALUE) {
            throw notFound(resourceName);
        }
        return (int) value;
    }

    private static NotFoundException notFound(String resourceName) {
        return new NotFoundException(resourceName + " not found.");
    }
}