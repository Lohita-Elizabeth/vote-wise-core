package onlinevotingsystem.controller;

import jakarta.validation.Valid;
import onlinevotingsystem.dto.VoteRequest;
import onlinevotingsystem.entity.Vote;
import onlinevotingsystem.repository.AuthRepository;
import onlinevotingsystem.service.VoteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/votes")
public class VoteController {
    private final VoteService voteService;
    private final AuthRepository authRepository;

    public VoteController(VoteService voteService, AuthRepository authRepository) {
        this.voteService = voteService;
        this.authRepository = authRepository;
    }

    @PostMapping
    public ResponseEntity<Vote> cast(@Valid @RequestBody VoteRequest req) {
        return new ResponseEntity<>(voteService.castVote(req, currentUserId()), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public Vote get(@PathVariable Long id) { return voteService.get(id); }

    @GetMapping("/voter/{voterId}")
    public List<Vote> byVoter(@PathVariable Long voterId) { return voteService.byVoter(voterId); }

    @GetMapping("/election/{electionId}")
    public List<Vote> byElection(@PathVariable Long electionId) { return voteService.byElection(electionId); }

    @GetMapping("/candidate/{candidateId}")
    public List<Vote> byCandidate(@PathVariable Long candidateId) { return voteService.byCandidate(candidateId); }

    @GetMapping("/count/candidate/{candidateId}")
    public long countForCandidate(@PathVariable Long candidateId) { return voteService.countByCandidate(candidateId); }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) { voteService.delete(id, currentUserId()); }

    private Long currentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null) return null;
        return authRepository.findByUsername(auth.getName()).map(a -> a.getId()).orElse(null);
    }
}
