package br.com.passe.player.dto;

import br.com.passe.player.enums.PlayerCondition;
import br.com.passe.player.enums.Position;
import br.com.passe.shared.exception.BadRequestException;
import br.com.passe.shared.util.QueryParams;
import br.com.passe.shared.util.TextNormalizer;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public record PlayerListQuery(
        String search,
        Position position,
        PlayerCondition condition,
        Boolean free,
        Integer clubId,
        Integer minAge,
        Integer maxAge,
        BigDecimal minValue,
        BigDecimal maxValue,
        List<String> sort,
        int page,
        int limit) {

    private static final Set<String> SORTS =
            Set.of("name", "value_asc", "value_desc", "age_asc", "age_desc", "position");
    private static final Comparator<PlayerResponse> BY_NAME =
            Comparator.comparing((PlayerResponse p) -> TextNormalizer.fold(p.name()));

    public PlayerListQuery {
        require(minAge == null || maxAge == null || minAge <= maxAge, "min_age cannot be greater than max_age.");
        require(minValue == null || maxValue == null || minValue.compareTo(maxValue) <= 0,
                "min_value cannot be greater than max_value.");
        require(!Boolean.TRUE.equals(free) || clubId == null, "free=true cannot be combined with club_id.");
    }

    public static PlayerListQuery from(Map<String, String> params) {
        return new PlayerListQuery(
                search(params.get("search")),
                QueryParams.enumValue(params, "position", Position.class),
                QueryParams.enumValue(params, "condition", PlayerCondition.class),
                QueryParams.bool(params, "free"),
                QueryParams.integer(params, "club_id", 1, Integer.MAX_VALUE),
                QueryParams.integer(params, "min_age", 16, 45),
                QueryParams.integer(params, "max_age", 16, 45),
                QueryParams.decimal(params, "min_value", 550),
                QueryParams.decimal(params, "max_value", 550),
                sort(params.get("sort")),
                Objects.requireNonNullElse(QueryParams.integer(params, "page", 1, Integer.MAX_VALUE), 1),
                Objects.requireNonNullElse(QueryParams.integer(params, "limit", 1, 50), 10));
    }

    public boolean matches(PlayerResponse player) {
        return (search == null || TextNormalizer.fold(player.name()).contains(TextNormalizer.fold(search)))
                && (position == null || player.position() == position)
                && (condition == null || player.condition() == condition)
                && (free == null || free == (player.clubId() == null))
                && (clubId == null || clubId.equals(player.clubId()))
                && (minAge == null || player.age() >= minAge)
                && (maxAge == null || player.age() <= maxAge)
                && (minValue == null || player.marketValue().compareTo(minValue) >= 0)
                && (maxValue == null || player.marketValue().compareTo(maxValue) <= 0);
    }

    public Comparator<PlayerResponse> comparator() {
        return sort.stream()
                .map(PlayerListQuery::comparatorFor)
                .reduce((first, second) -> first.thenComparing(second))
                .orElseThrow()
                .thenComparing(BY_NAME)
                .thenComparing(PlayerResponse::id);
    }

    private static Comparator<PlayerResponse> comparatorFor(String sort) {
        return switch (sort) {
            case "value_asc" -> Comparator.comparing(PlayerResponse::marketValue);
            case "value_desc" -> Comparator.comparing(PlayerResponse::marketValue).reversed();
            case "age_asc" -> Comparator.comparing(PlayerResponse::age);
            case "age_desc" -> Comparator.comparing(PlayerResponse::age).reversed();
            case "position" -> Comparator.comparingInt(p -> p.position().ordinal());
            default -> BY_NAME;
        };
    }

    private static String search(String raw) {
        if (raw == null) {
            return null;
        }
        String search = TextNormalizer.clean(raw);
        int length = search.codePointCount(0, search.length());
        require(!TextNormalizer.hasControlChars(raw) && length >= 2 && length <= 100,
                "search: must have between 2 and 100 characters, without control characters.");
        return search;
    }

    private static List<String> sort(String raw) {
        if (raw == null) {
            return List.of("name");
        }
        List<String> sorts = List.of(raw.split(",", -1));
        boolean valid = sorts.size() <= 2
                && SORTS.containsAll(sorts)
                && (sorts.size() == 1 || !field(sorts.get(0)).equals(field(sorts.get(1))));
        require(valid, "sort: invalid value.");
        return sorts;
    }

    private static String field(String sort) {
        return sort.split("_")[0];
    }

    private static void require(boolean valid, String message) {
        if (!valid) {
            throw new BadRequestException(message);
        }
    }
}
