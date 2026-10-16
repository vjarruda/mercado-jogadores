package br.com.passe.club.service;

import br.com.passe.shared.exception.BadRequestException;
import br.com.passe.shared.util.TextNormalizer;
import java.util.regex.Pattern;
import org.springframework.stereotype.Component;

@Component
public class ClubRules {

    private static final Pattern NAME_FORMAT = Pattern.compile("[\\p{L} .-]+");
    private static final Pattern ACRONYM_FORMAT = Pattern.compile("[A-Z]{3}");

    // retorna o nome ja limpo
    public String validateName(String raw) {
        require(!TextNormalizer.hasControlChars(raw), "name: control characters are not allowed.");
        String name = TextNormalizer.clean(raw);
        int words = name.split(" ").length;
        require(name.length() >= 3 && name.length() <= 60, "name: must have between 3 and 60 characters.");
        require(NAME_FORMAT.matcher(name).matches(), "name: only letters, spaces, hyphens and periods.");
        require(words >= 2 && words <= 5, "name: must have between 2 and 5 words.");
        return name;
    }

    public void validateAcronym(String acronym, String cleanName) {
        require(ACRONYM_FORMAT.matcher(acronym).matches(), "acronym: must have exactly 3 letters from A to Z.");
        require(acronym.chars().distinct().count() > 1, "acronym: the three letters cannot be all equal.");

        String letters = TextNormalizer.fold(cleanName).toUpperCase();
        require(letters.charAt(0) == acronym.charAt(0), "acronym: must start with the first letter of the name.");
        require(appearsInOrder(letters, acronym), "acronym: letters must appear in the name, in order.");
    }

    private boolean appearsInOrder(String text, String acronym) {
        int position = 0;
        for (char letter : acronym.toCharArray()) {
            position = text.indexOf(letter, position);
            if (position < 0) {
                return false;
            }
            position++;
        }
        return true;
    }

    private void require(boolean valid, String message) {
        if (!valid) {
            throw new BadRequestException(message);
        }
    }
}