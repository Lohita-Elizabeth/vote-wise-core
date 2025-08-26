package onlinevotingsystem.repository;

import onlinevotingsystem.entity.Voter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VoterRepository extends JpaRepository<Voter, Long> {
    Optional<Voter> findByAuth_Id(Long authId);
    Optional<Voter> findByVoterIdNumber(String voterIdNumber);
    boolean existsByAuth_Id(Long authId);
}
