package br.com.passe.club.controller;

import br.com.passe.club.dto.ClubCreateRequest;
import br.com.passe.club.dto.ClubDetailResponse;
import br.com.passe.club.dto.ClubResponse;
import br.com.passe.club.service.ClubService;
import br.com.passe.market.dto.TransactionFilter;
import br.com.passe.market.dto.TransactionListResponse;
import br.com.passe.market.service.TransactionQueryService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/clubs")
@RequiredArgsConstructor
public class ClubController {

    private final ClubService clubService;
    private final TransactionQueryService transactionQueryService;

    @PostMapping
    public ResponseEntity<ClubResponse> create(@Valid @RequestBody ClubCreateRequest request) {
        ClubResponse created = clubService.create(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequestUri()
                .path("/{id}").buildAndExpand(created.id()).toUri();
        return ResponseEntity.created(location).body(created);
    }

    @GetMapping
    public List<ClubResponse> list(@RequestParam(required = false) String sort) {
        return clubService.list(sort);
    }

    @GetMapping("/{id}")
    public ClubDetailResponse findById(@PathVariable String id) {
        return clubService.findById(id);
    }

    @GetMapping("/{id}/transactions")
    public TransactionListResponse transactions(@PathVariable String id,
                                                @RequestParam Map<String, String> params) {
        // o filtro é lido antes de consultar o clube: formato inválido (400) vem antes de inexistente (404)
        TransactionFilter filter = TransactionFilter.from(params);
        return transactionQueryService.list(id, filter);
    }
}