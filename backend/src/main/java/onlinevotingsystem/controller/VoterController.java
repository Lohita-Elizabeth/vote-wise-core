package onlinevotingsystem.controller;

import jakarta.validation.Valid;
import onlinevotingsystem.dto.VoterRequest;
import onlinevotingsystem.entity.Voter;
import onlinevotingsystem.repository.AuthRepository;
import onlinevotingsystem.service.VoterService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/voters")
public class VoterController {
    private final VoterService voterService;
    private final AuthRepository authRepository;

    public VoterController(VoterService voterService, AuthRepository authRepository) {
        this.voterService = voterService;
        this.authRepository = authRepository;
    }

    @PostMapping
    public ResponseEntity<Voter> create(@Valid @RequestBody VoterRequest req) {
        return new ResponseEntity<>(voterService.create(req, currentUserId()), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public Voter get(@PathVariable Long id) { return voterService.get(id); }

    @GetMapping
    public List<Voter> list() { return voterService.list(); }

    @PutMapping("/{id}")
    public Voter update(@PathVariable Long id, @Valid @RequestBody VoterRequest req) {
        return voterService.update(id, req, currentUserId());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) { voterService.delete(id, currentUserId()); }

    private Long currentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null) return null;
        return authRepository.findByUsername(auth.getName()).map(a -> a.getId()).orElse(null);
    }
}
