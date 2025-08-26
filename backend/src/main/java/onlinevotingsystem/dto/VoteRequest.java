package onlinevotingsystem.dto;

import jakarta.validation.constraints.NotNull;

public class VoteRequest {
    @NotNull
    private Long voterId;
    @NotNull
    private Long candidateId;
    @NotNull
    private Long electionId;

    public Long getVoterId() { return voterId; }
    public void setVoterId(Long voterId) { this.voterId = voterId; }
    public Long getCandidateId() { return candidateId; }
    public void setCandidateId(Long candidateId) { this.candidateId = candidateId; }
    public Long getElectionId() { return electionId; }
    public void setElectionId(Long electionId) { this.electionId = electionId; }
}
