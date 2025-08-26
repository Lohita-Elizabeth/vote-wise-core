package onlinevotingsystem.repository;

import onlinevotingsystem.entity.Vote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VoteRepository extends JpaRepository<Vote, Long> {
    boolean existsByVoter_IdAndElection_Id(Long voterId, Long electionId);
    long countByCandidate_Id(Long candidateId);
    List<Vote> findByVoter_Id(Long voterId);
    List<Vote> findByElection_Id(Long electionId);
    List<Vote> findByCandidate_Id(Long candidateId);
}
