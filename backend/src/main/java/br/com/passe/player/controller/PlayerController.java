package br.com.passe.player.controller;

import br.com.passe.player.dto.ConditionRequest;
import br.com.passe.player.dto.PlayerCreateRequest;
import br.com.passe.player.dto.PlayerResponse;
import br.com.passe.player.dto.PlayerUpdateRequest;
import br.com.passe.player.service.PlayerService;
import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/players")
@RequiredArgsConstructor
public class PlayerController {

    private final PlayerService playerService;

    @PostMapping
    public ResponseEntity<PlayerResponse> create(@Valid @RequestBody PlayerCreateRequest request) {
        PlayerResponse created = playerService.create(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequestUri()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @GetMapping("/{id}")
    public PlayerResponse findById(@PathVariable String id) {
        return playerService.findById(id);
    }

    @PutMapping("/{id}")
    public PlayerResponse update(@PathVariable String id, @Valid @RequestBody PlayerUpdateRequest request) {
        return playerService.update(id, request);
    }

    @PatchMapping("/{id}/condition")
    public PlayerResponse updateCondition(@PathVariable String id, @Valid @RequestBody ConditionRequest request) {
        return playerService.updateCondition(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        playerService.delete(id);
        return ResponseEntity.noContent().build();
    }
}