package br.com.passe.club.enums;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum Formation {
    THREE_ONE("3-1", 3, 0, 1),
    TWO_TWO("2-2", 2, 0, 2),
    TWO_ONE_ONE("2-1-1", 2, 1, 1),
    ONE_TWO_ONE("1-2-1", 1, 2, 1);

    private final String code;
    private final int defenders;
    private final int midfielders;
    private final int forwards;

    Formation(String code, int defenders, int midfielders, int forwards) {
        this.code = code;
        this.defenders = defenders;
        this.midfielders = midfielders;
        this.forwards = forwards;
    }

    public static Formation fromCode(String code) {
        return Arrays.stream(values())
                .filter(formation -> formation.code.equals(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid formation: " + code));
    }
}
