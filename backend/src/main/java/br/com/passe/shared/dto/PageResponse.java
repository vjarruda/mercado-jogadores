package br.com.passe.shared.dto;

import java.util.List;

public record PageResponse<T>(List<T> data, Meta meta) {

    public record Meta(int page, int limit, int totalItems, int totalPages) {
    }

    public static <T> PageResponse<T> of(List<T> all, int page, int limit) {
        int from = (int) Math.min((long) (page - 1) * limit, all.size());
        int to = Math.min(from + limit, all.size());
        int totalPages = (all.size() + limit - 1) / limit;
        return new PageResponse<>(all.subList(from, to), new Meta(page, limit, all.size(), totalPages));
    }
}