package onlinevotingsystem.controller;

import jakarta.validation.Valid;
import onlinevotingsystem.dto.CandidateRequest;
import onlinevotingsystem.entity.Candidate;
import onlinevotingsystem.repository.AuthRepository;
import onlinevotingsystem.service.CandidateService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/candidates")
public class CandidateController {
    private final CandidateService candidateService;
    private final AuthRepository authRepository;

    public CandidateController(CandidateService candidateService, AuthRepository authRepository) {
        this.candidateService = candidateService;
        this.authRepository = authRepository;
    }

    @PostMapping
    public ResponseEntity<Candidate> register(@Valid @RequestBody CandidateRequest req) {
        return new ResponseEntity<>(candidateService.register(req, currentUserId()), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public Candidate get(@PathVariable Long id) { return candidateService.get(id); }

    @GetMapping
    public List<Candidate> list() { return candidateService.list(); }

    @GetMapping("/election/{electionId}")
    public List<Candidate> byElection(@PathVariable Long electionId) { return candidateService.listByElection(electionId); }

    @PutMapping("/{id}")
    public Candidate update(@PathVariable Long id, @Valid @RequestBody CandidateRequest req) {
        return candidateService.update(id, req, currentUserId());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) { candidateService.delete(id, currentUserId()); }

    private Long currentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null) return null;
        return authRepository.findByUsername(auth.getName()).map(a -> a.getId()).orElse(null);
    }
}
