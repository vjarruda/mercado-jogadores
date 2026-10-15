package br.com.passe.player.service;

import br.com.passe.player.enums.Position;
import br.com.passe.player.model.Attributes;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.Map;

@Component
public class OverallCalculator {
    private static final Map<Position, int[]> WEIGHTS = new EnumMap<>(Position.class);

    static {
        WEIGHTS.put(Position.GOL, new int[] {10, 0, 20, 0, 40, 30});
        WEIGHTS.put(Position.ZAG, new int[] {10, 0, 15, 0, 45, 30});
        WEIGHTS.put(Position.LAT, new int[] {25, 0, 20, 15, 25, 15});
        WEIGHTS.put(Position.MEI, new int[] {10, 15, 35, 25, 5, 10});
        WEIGHTS.put(Position.ATA, new int[] {20, 40, 10, 20, 0, 10});
    }

    public int calculate(Position position, Attributes attributes) {
        int[] weights = WEIGHTS.get(position);
        int weightedSum = attributes.pace() * weights[0]
                + attributes.shooting() * weights[1]
                + attributes.passing() * weights[2]
                + attributes.dribbling() * weights[3]
                + attributes.defending() * weights[4]
                + attributes.physical() * weights[5];
        // arredonda pra cima
        return (weightedSum + 50) / 100;
    }
}