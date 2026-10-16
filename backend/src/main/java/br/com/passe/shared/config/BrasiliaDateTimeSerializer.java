package br.com.passe.shared.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class BrasiliaDateTimeSerializer extends JsonSerializer<LocalDateTime> {

    private static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'-03:00'");

    @Override
    public void serialize(LocalDateTime value, JsonGenerator generator, SerializerProvider provider)
            throws IOException {
        generator.writeString(FORMAT.format(value));
    }
}