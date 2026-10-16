package br.com.passe.club.controller;

import br.com.passe.club.dto.ClubCreateRequest;
import br.com.passe.club.dto.ClubDetailResponse;
import br.com.passe.club.dto.ClubResponse;
import br.com.passe.club.service.ClubService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/clubs")
@RequiredArgsConstructor
public class ClubController {

    private final ClubService clubService;

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
}