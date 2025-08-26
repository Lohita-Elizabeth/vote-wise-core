package onlinevotingsystem.service;

import onlinevotingsystem.audit.AuditService;
import onlinevotingsystem.dto.VoteRequest;
import onlinevotingsystem.entity.*;
import onlinevotingsystem.exception.BadRequestException;
import onlinevotingsystem.exception.ConflictException;
import onlinevotingsystem.exception.ResourceNotFoundException;
import onlinevotingsystem.repository.CandidateRepository;
import onlinevotingsystem.repository.ElectionRepository;
import onlinevotingsystem.repository.VoteRepository;
import onlinevotingsystem.repository.VoterRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class VoteService {
    private final VoteRepository voteRepository;
    private final VoterRepository voterRepository;
    private final CandidateRepository candidateRepository;
    private final ElectionRepository electionRepository;
    private final AuditService auditService;

    public VoteService(VoteRepository voteRepository,
                       VoterRepository voterRepository,
                       CandidateRepository candidateRepository,
                       ElectionRepository electionRepository,
                       AuditService auditService) {
        this.voteRepository = voteRepository;
        this.voterRepository = voterRepository;
        this.candidateRepository = candidateRepository;
        this.electionRepository = electionRepository;
        this.auditService = auditService;
    }

    @Transactional
    public Vote castVote(VoteRequest req, Long actingUserId) {
        Voter voter = voterRepository.findById(req.getVoterId())
                .orElseThrow(() -> new ResourceNotFoundException("Voter not found"));
        if (!voter.isEligible()) throw new BadRequestException("Voter is not eligible to vote");

        Election election = electionRepository.findById(req.getElectionId())
                .orElseThrow(() -> new ResourceNotFoundException("Election not found"));
        if (election.getStatus() != ElectionStatus.ONGOING) {
            throw new BadRequestException("Election is not currently ongoing");
        }

        Candidate candidate = candidateRepository.findById(req.getCandidateId())
                .orElseThrow(() -> new ResourceNotFoundException("Candidate not found"));
        if (!candidate.getElection().getId().equals(election.getId())) {
            throw new BadRequestException("Candidate does not belong to the specified election");
        }

        // Ensure one vote per voter per election
        if (voteRepository.existsByVoter_IdAndElection_Id(voter.getId(), election.getId())) {
            throw new ConflictException("Voter has already cast a vote for this election");
        }

        Vote vote = new Vote();
        vote.setVoter(voter);
        vote.setCandidate(candidate);
        vote.setElection(election);
        Vote saved = voteRepository.save(vote);

        // Optional: mark voter as hasVoted (global flag)
        voter.setHasVoted(true);
        voterRepository.save(voter);

        auditService.record(actingUserId, "CAST_VOTE", "Vote", saved.getId(), "election=" + election.getId());
        return saved;
    }

    public Vote get(Long id) {
        return voteRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Vote not found"));
    }

    public List<Vote> byVoter(Long voterId) { return voteRepository.findByVoter_Id(voterId); }

    public List<Vote> byElection(Long electionId) { return voteRepository.findByElection_Id(electionId); }

    public List<Vote> byCandidate(Long candidateId) { return voteRepository.findByCandidate_Id(candidateId); }

    public long countByCandidate(Long candidateId) { return voteRepository.countByCandidate_Id(candidateId); }

    @Transactional
    public void delete(Long id, Long actingUserId) {
        voteRepository.deleteById(id);
        auditService.record(actingUserId, "DELETE_VOTE", "Vote", id, null);
    }
}
