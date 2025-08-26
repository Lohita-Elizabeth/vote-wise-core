package onlinevotingsystem.service;

import onlinevotingsystem.audit.AuditService;
import onlinevotingsystem.dto.CandidateRequest;
import onlinevotingsystem.entity.Auth;
import onlinevotingsystem.entity.Candidate;
import onlinevotingsystem.entity.Election;
import onlinevotingsystem.exception.BadRequestException;
import onlinevotingsystem.exception.ConflictException;
import onlinevotingsystem.exception.ResourceNotFoundException;
import onlinevotingsystem.repository.AuthRepository;
import onlinevotingsystem.repository.CandidateRepository;
import onlinevotingsystem.repository.ElectionRepository;
import onlinevotingsystem.repository.VoterRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CandidateService {
    private final CandidateRepository candidateRepository;
    private final ElectionRepository electionRepository;
    private final AuthRepository authRepository;
    private final VoterRepository voterRepository;
    private final AuditService auditService;

    public CandidateService(CandidateRepository candidateRepository,
                            ElectionRepository electionRepository,
                            AuthRepository authRepository,
                            VoterRepository voterRepository,
                            AuditService auditService) {
        this.candidateRepository = candidateRepository;
        this.electionRepository = electionRepository;
        this.authRepository = authRepository;
        this.voterRepository = voterRepository;
        this.auditService = auditService;
    }

    @Transactional
    public Candidate register(CandidateRequest req, Long actingUserId) {
        Auth auth = authRepository.findById(req.getAuthId())
                .orElseThrow(() -> new ResourceNotFoundException("Auth not found"));
        Election election = electionRepository.findById(req.getElectionId())
                .orElseThrow(() -> new ResourceNotFoundException("Election not found"));

        // Must also be a voter
        if (!voterRepository.existsByAuth_Id(auth.getId())) {
            throw new BadRequestException("Candidate must be a registered voter");
        }

        // Cannot contest multiple elections
        if (candidateRepository.existsByAuth_Id(auth.getId())) {
            throw new ConflictException("User is already registered as a candidate");
        }

        Candidate c = new Candidate();
        c.setAuth(auth);
        c.setElection(election);
        c.setName(req.getName());
        c.setParty(req.getParty());
        c.setManifesto(req.getManifesto());
        Candidate saved = candidateRepository.save(c);
        auditService.record(actingUserId, "REGISTER_CANDIDATE", "Candidate", saved.getId(), null);
        return saved;
    }

    public Candidate get(Long id) {
        return candidateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Candidate not found"));
    }

    public List<Candidate> list() { return candidateRepository.findAll(); }

    public List<Candidate> listByElection(Long electionId) { return candidateRepository.findByElection_Id(electionId); }

    @Transactional
    public Candidate update(Long id, CandidateRequest req, Long actingUserId) {
        Candidate c = get(id);

        // If election is changed, ensure not contesting multiple elections
        if (!c.getElection().getId().equals(req.getElectionId())) {
            if (candidateRepository.existsByAuth_IdAndElection_IdNot(c.getAuth().getId(), c.getElection().getId())) {
                throw new ConflictException("Candidate cannot contest multiple elections");
            }
            Election newElection = electionRepository.findById(req.getElectionId())
                    .orElseThrow(() -> new ResourceNotFoundException("Election not found"));
            c.setElection(newElection);
        }

        c.setName(req.getName());
        c.setParty(req.getParty());
        c.setManifesto(req.getManifesto());
        Candidate saved = candidateRepository.save(c);
        auditService.record(actingUserId, "UPDATE_CANDIDATE", "Candidate", saved.getId(), null);
        return saved;
    }

    @Transactional
    public void delete(Long id, Long actingUserId) {
        candidateRepository.deleteById(id);
        auditService.record(actingUserId, "DELETE_CANDIDATE", "Candidate", id, null);
    }
}
