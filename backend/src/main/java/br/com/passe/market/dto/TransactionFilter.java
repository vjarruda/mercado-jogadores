package br.com.passe.market.dto;

import br.com.passe.market.entity.MarketTransaction;
import br.com.passe.market.enums.TransactionType;
import br.com.passe.shared.exception.BadRequestException;
import br.com.passe.shared.util.QueryParams;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

public record TransactionFilter(Set<TransactionType> types, Integer playerId) {

    public static TransactionFilter from(Map<String, String> params) {
        return new TransactionFilter(
                types(params.get("type")),
                QueryParams.integer(params, "player_id", 1, Integer.MAX_VALUE));
    }

    //aceita os dois tipos.
    public boolean matches(MarketTransaction transaction) {
        boolean typeMatches = types == null || types.contains(transaction.getType());
        boolean playerMatches = playerId == null || playerId.equals(transaction.getPlayer().getId());
        return typeMatches && playerMatches;
    }

    private static Set<TransactionType> types(String raw) {
        if (raw == null) {
            return null;
        }
        List<String> tokens = Arrays.asList(raw.split(",", -1));
        try {
            List<TransactionType> types = tokens.stream().map(TransactionType::valueOf).toList();
            if (Set.copyOf(types).size() != types.size()) {
                throw new BadRequestException("type: values cannot be repeated.");
            }
            return Set.copyOf(types);
        } catch (IllegalArgumentException ex) {
            throw new BadRequestException("type: invalid value.");
        }
    }
}
