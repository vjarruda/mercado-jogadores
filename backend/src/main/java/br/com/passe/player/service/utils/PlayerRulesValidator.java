package br.com.passe.player.service.utils;

import br.com.passe.player.dto.PlayerCreateRequest;
import br.com.passe.shared.exception.BadRequestException;
import br.com.passe.shared.util.TextNormalizer;
import org.springframework.stereotype.Component;

import java.util.stream.IntStream;

@Component
public class PlayerRulesValidator {

    private static final int MIN_NAME_LENGTH = 3;
    private static final int MAX_NAME_LENGTH = 100;
    private static final int MAX_ATTRIBUTE_SUM = 480;
    private static final int VETERAN_AGILITY_AGE = 35;
    private static final int VETERAN_AGILITY_LIMIT = 85;
    private static final int POTENTIAL_CAP_AGE = 33;

    // valida o nome e retorna ja formatado
    public String validateName(String rawName) {
        if (TextNormalizer.hasControlChars(rawName)) {
            throw new BadRequestException("name: control characters are not allowed.");
        }
        String name = TextNormalizer.clean(rawName);
        if (name.length() < MIN_NAME_LENGTH || name.length() > MAX_NAME_LENGTH) {
            throw new BadRequestException("name: must have between 3 and 100 characters.");
        }
        if (name.codePoints().anyMatch(Character::isDigit)) {
            throw new BadRequestException("name: digits are not allowed.");
        }
        if (countWords(name) < 2) {
            throw new BadRequestException("name: must have at least two words.");
        }
        return name;
    }

    public void validateAgility(int agility, int age) {
        if (age >= VETERAN_AGILITY_AGE && agility > VETERAN_AGILITY_LIMIT) {
            throw new BadRequestException("agility: must be at most 85 for players aged 35 or older.");
        }
    }

    public void validateRegistration(PlayerCreateRequest request) {
        validateAgility(request.agility(), request.age());

        int highest = highestAttribute(request);
        int sum = request.pace() + request.shooting() + request.passing()
                + request.dribbling() + request.defending() + request.physical();

        if (sum > MAX_ATTRIBUTE_SUM) {
            throw new BadRequestException("The sum of the six attributes must be at most 480.");
        }
        if (request.potential() < highest) {
            throw new BadRequestException("potential: must be at least the highest attribute.");
        }
        if (request.age() >= POTENTIAL_CAP_AGE && request.potential() != highest) {
            throw new BadRequestException("potential: must equal the highest attribute for players aged 33 or older.");
        }
    }

    private int highestAttribute(PlayerCreateRequest request) {
        return IntStream.of(request.pace(), request.shooting(), request.passing(),
                request.dribbling(), request.defending(), request.physical()).max().orElse(0);
    }

    private long countWords(String name) {
        return java.util.Arrays.stream(name.split(" "))
                .filter(word -> word.codePoints().anyMatch(Character::isLetter))
                .count();
    }
}