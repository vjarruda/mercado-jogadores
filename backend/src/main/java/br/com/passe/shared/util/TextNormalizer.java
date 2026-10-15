package br.com.passe.shared.util;

import java.text.Normalizer;
import java.util.regex.Pattern;

public final class TextNormalizer {

    private static final Pattern CONTROL_CHARS = Pattern.compile("\\p{Cntrl}");
    private static final Pattern REPEATED_SPACES = Pattern.compile(" {2,}");
    private static final Pattern ACCENT_MARKS = Pattern.compile("\\p{Mn}+");

    private TextNormalizer() {
    }

    public static boolean hasControlChars(String text) {
        return text != null && CONTROL_CHARS.matcher(text).find();
    }

    // trata espaços repetidos
    public static String clean(String text) {
        if (text == null) {
            return null;
        }
        return REPEATED_SPACES.matcher(text.strip()).replaceAll(" ");
    }

    //lower case, sem acentos, usado pra buscar porr ordem alfabetica

    public static String fold(String text) {
        String decomposed = Normalizer.normalize(text, Normalizer.Form.NFD);
        return ACCENT_MARKS.matcher(decomposed).replaceAll("").toLowerCase();
    }
}
