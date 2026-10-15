package br.com.passe.shared.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.math.BigDecimal;

public class StrictIntegerDeserializer extends JsonDeserializer<Integer> {

    @Override
    public Integer deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        if (parser.currentToken() == JsonToken.VALUE_NUMBER_INT) {
            return parser.getIntValue();
        }
        if (parser.currentToken() == JsonToken.VALUE_NUMBER_FLOAT) {
            BigDecimal decimal = parser.getDecimalValue();
            try {
                return decimal.intValueExact();
            } catch (ArithmeticException ex) {
                return (Integer) context.handleUnexpectedToken(Integer.class, parser);
            }
        }
        return (Integer) context.handleUnexpectedToken(Integer.class, parser);
    }
}
