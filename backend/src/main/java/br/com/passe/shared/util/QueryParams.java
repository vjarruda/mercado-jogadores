package br.com.passe.shared.util;

import br.com.passe.shared.exception.BadRequestException;
import java.math.BigDecimal;
import java.util.Map;
import java.util.regex.Pattern;

public final class QueryParams {

    private static final Pattern INTEGER = Pattern.compile("[1-9][0-9]{0,9}");
    private static final Pattern DECIMAL = Pattern.compile("(0|[1-9][0-9]*)(\\.[0-9]{1,2})?");

    private QueryParams() {
    }

    public static Integer integer(Map<String, String> params, String name, int min, int max) {
        String raw = params.get(name);
        if (raw == null) {
            return null;
        }
        if (!INTEGER.matcher(raw).matches()) {
            throw invalid(name);
        }
        long value = Long.parseLong(raw);
        if (value < min || value > max) {
            throw invalid(name);
        }
        return (int) value;
    }

    public static BigDecimal decimal(Map<String, String> params, String name, int max) {
        String raw = params.get(name);
        if (raw == null) {
            return null;
        }
        if (!DECIMAL.matcher(raw).matches() || new BigDecimal(raw).compareTo(BigDecimal.valueOf(max)) > 0) {
            throw invalid(name);
        }
        return new BigDecimal(raw);
    }

    public static <E extends Enum<E>> E enumValue(Map<String, String> params, String name, Class<E> type) {
        String raw = params.get(name);
        if (raw == null) {
            return null;
        }
        try {
            return Enum.valueOf(type, raw);
        } catch (IllegalArgumentException ex) {
            throw invalid(name);
        }
    }

    public static Boolean bool(Map<String, String> params, String name) {
        String raw = params.get(name);
        if (raw == null) {
            return null;
        }
        if (!raw.equals("true") && !raw.equals("false")) {
            throw invalid(name);
        }
        return Boolean.valueOf(raw);
    }

    private static BadRequestException invalid(String name) {
        return new BadRequestException(name + ": invalid value.");
    }
}