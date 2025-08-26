package onlinevotingsystem.service;

import onlinevotingsystem.audit.AuditService;
import onlinevotingsystem.dto.ElectionRequest;
import onlinevotingsystem.dto.ElectionSummaryDto;
import onlinevotingsystem.entity.Election;
import onlinevotingsystem.entity.ElectionStatus;
import onlinevotingsystem.exception.BadRequestException;
import onlinevotingsystem.exception.ResourceNotFoundException;
import onlinevotingsystem.repository.ElectionRepository;
import onlinevotingsystem.repository.VoteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
public class ElectionService {
    private final ElectionRepository electionRepository;
    private final VoteRepository voteRepository;
    private final AuditService auditService;

    public ElectionService(ElectionRepository electionRepository, VoteRepository voteRepository, AuditService auditService) {
        this.electionRepository = electionRepository;
        this.voteRepository = voteRepository;
        this.auditService = auditService;
    }

    @Transactional
    public Election create(ElectionRequest req, Long actingUserId) {
        if (req.getEndDate().isBefore(req.getStartDate())) {
            throw new BadRequestException("endDate must be after startDate");
        }
        Election e = new Election();
        e.setTitle(req.getTitle());
        e.setDescription(req.getDescription());
        e.setStartDate(req.getStartDate());
        e.setEndDate(req.getEndDate());
        e.setStatus(computeStatus(Instant.now(), req.getStartDate(), req.getEndDate()));
        Election saved = electionRepository.save(e);
        auditService.record(actingUserId, "CREATE_ELECTION", "Election", saved.getId(), null);
        return saved;
    }

    public Election get(Long id) {
        return electionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Election not found"));
    }

    public List<Election> list() {
        return electionRepository.findAll();
    }

    @Transactional
    public Election update(Long id, ElectionRequest req, Long actingUserId) {
        Election e = get(id);
        if (req.getEndDate().isBefore(req.getStartDate())) {
            throw new BadRequestException("endDate must be after startDate");
        }
        e.setTitle(req.getTitle());
        e.setDescription(req.getDescription());
        e.setStartDate(req.getStartDate());
        e.setEndDate(req.getEndDate());
        e.setStatus(computeStatus(Instant.now(), req.getStartDate(), req.getEndDate()));
        Election saved = electionRepository.save(e);
        auditService.record(actingUserId, "UPDATE_ELECTION", "Election", saved.getId(), null);
        return saved;
    }

    @Transactional
    public void delete(Long id, Long actingUserId) {
        electionRepository.deleteById(id);
        auditService.record(actingUserId, "DELETE_ELECTION", "Election", id, null);
    }

    @Transactional
    public Election refreshStatus(Long id, Long actingUserId) {
        Election e = get(id);
        e.setStatus(computeStatus(Instant.now(), e.getStartDate(), e.getEndDate()));
        Election saved = electionRepository.save(e);
        auditService.record(actingUserId, "REFRESH_ELECTION_STATUS", "Election", saved.getId(), saved.getStatus().name());
        return saved;
    }

    public ElectionSummaryDto summary(Long id) {
        Election e = get(id);
        long totalVotes = voteRepository.findByElection_Id(id).size();
        return new ElectionSummaryDto(e.getId(), e.getTitle(), totalVotes);
    }

    private ElectionStatus computeStatus(Instant now, Instant start, Instant end) {
        if (now.isBefore(start)) return ElectionStatus.UPCOMING;
        if (now.isAfter(end)) return ElectionStatus.COMPLETED;
        return ElectionStatus.ONGOING;
    }
}
