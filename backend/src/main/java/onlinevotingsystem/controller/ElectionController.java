package onlinevotingsystem.controller;

import jakarta.validation.Valid;
import onlinevotingsystem.dto.ElectionRequest;
import onlinevotingsystem.dto.ElectionSummaryDto;
import onlinevotingsystem.entity.Election;
import onlinevotingsystem.repository.AuthRepository;
import onlinevotingsystem.service.ElectionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/elections")
public class ElectionController {
    private final ElectionService electionService;
    private final AuthRepository authRepository;

    public ElectionController(ElectionService electionService, AuthRepository authRepository) {
        this.electionService = electionService;
        this.authRepository = authRepository;
    }

    @PostMapping
    public ResponseEntity<Election> create(@Valid @RequestBody ElectionRequest req) {
        return new ResponseEntity<>(electionService.create(req, currentUserId()), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public Election get(@PathVariable Long id) { return electionService.get(id); }

    @GetMapping
    public List<Election> list() { return electionService.list(); }

    @PutMapping("/{id}")
    public Election update(@PathVariable Long id, @Valid @RequestBody ElectionRequest req) {
        return electionService.update(id, req, currentUserId());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) { electionService.delete(id, currentUserId()); }

    @PatchMapping("/{id}/status")
    public Election refreshStatus(@PathVariable Long id) { return electionService.refreshStatus(id, currentUserId()); }

    @GetMapping("/{id}/summary")
    public ElectionSummaryDto summary(@PathVariable Long id) { return electionService.summary(id); }

    private Long currentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null) return null;
        return authRepository.findByUsername(auth.getName()).map(a -> a.getId()).orElse(null);
    }
}
