package br.com.passe.shared.util;

import br.com.passe.shared.exception.BadRequestException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.regex.Pattern;

public final class BrasiliaDateTime {

    private static final Pattern INPUT = Pattern.compile("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}-03:00");
    private static final DateTimeFormatter FORMAT =
            DateTimeFormatter.ofPattern("uuuu-MM-dd'T'HH:mm'-03:00'").withResolverStyle(ResolverStyle.STRICT);

    private BrasiliaDateTime() {
    }

    public static LocalDateTime parse(String field, String raw) {
        try {
            if (INPUT.matcher(raw).matches()) {
                return LocalDateTime.parse(raw, FORMAT);
            }
        } catch (DateTimeParseException ex) {
        }
        throw new BadRequestException(field + ": expected format yyyy-MM-ddTHH:mm-03:00 with a valid date.");
    }
}
