package onlinevotingsystem.service;

import onlinevotingsystem.audit.AuditService;
import onlinevotingsystem.dto.VoterRequest;
import onlinevotingsystem.entity.Auth;
import onlinevotingsystem.entity.Voter;
import onlinevotingsystem.exception.BadRequestException;
import onlinevotingsystem.exception.ResourceNotFoundException;
import onlinevotingsystem.repository.AuthRepository;
import onlinevotingsystem.repository.VoterRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

@Service
public class VoterService {
    private final VoterRepository voterRepository;
    private final AuthRepository authRepository;
    private final AuditService auditService;

    public VoterService(VoterRepository voterRepository, AuthRepository authRepository, AuditService auditService) {
        this.voterRepository = voterRepository;
        this.authRepository = authRepository;
        this.auditService = auditService;
    }

    @Transactional
    public Voter create(VoterRequest req, Long actingUserId) {
        Auth auth = authRepository.findById(req.getAuthId())
                .orElseThrow(() -> new ResourceNotFoundException("Auth not found"));
        if (!isAdult(req.getDob())) throw new BadRequestException("Voter must be at least 18 years old");
        Voter voter = new Voter();
        voter.setAuth(auth);
        voter.setName(req.getName());
        voter.setDob(req.getDob());
        voter.setAddress(req.getAddress());
        voter.setVoterIdNumber(req.getVoterIdNumber());
        voter.setEligible(req.getEligible() != null ? req.getEligible() : true);
        Voter saved = voterRepository.save(voter);
        auditService.record(actingUserId, "CREATE_VOTER", "Voter", saved.getId(), null);
        return saved;
    }

    public Voter get(Long id) {
        return voterRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Voter not found"));
    }

    public List<Voter> list() { return voterRepository.findAll(); }

    @Transactional
    public Voter update(Long id, VoterRequest req, Long actingUserId) {
        Voter v = get(id);
        if (!isAdult(req.getDob())) throw new BadRequestException("Voter must be at least 18 years old");
        v.setName(req.getName());
        v.setDob(req.getDob());
        v.setAddress(req.getAddress());
        v.setVoterIdNumber(req.getVoterIdNumber());
        v.setEligible(req.getEligible() != null ? req.getEligible() : v.isEligible());
        Voter saved = voterRepository.save(v);
        auditService.record(actingUserId, "UPDATE_VOTER", "Voter", saved.getId(), null);
        return saved;
    }

    @Transactional
    public void delete(Long id, Long actingUserId) {
        voterRepository.deleteById(id);
        auditService.record(actingUserId, "DELETE_VOTER", "Voter", id, null);
    }

    private boolean isAdult(LocalDate dob) {
        return Period.between(dob, LocalDate.now()).getYears() >= 18;
    }
}
