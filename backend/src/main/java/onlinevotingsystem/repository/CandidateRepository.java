package onlinevotingsystem.repository;

import onlinevotingsystem.entity.Candidate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CandidateRepository extends JpaRepository<Candidate, Long> {
    Optional<Candidate> findByAuth_Id(Long authId);
    boolean existsByAuth_Id(Long authId);
    boolean existsByAuth_IdAndElection_IdNot(Long authId, Long electionId);
    List<Candidate> findByElection_Id(Long electionId);
}
