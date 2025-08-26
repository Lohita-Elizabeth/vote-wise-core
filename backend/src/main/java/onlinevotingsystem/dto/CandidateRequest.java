package onlinevotingsystem.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CandidateRequest {
    @NotNull
    private Long authId;
    @NotNull
    private Long electionId;
    @NotBlank
    private String name;
    private String party;
    private String manifesto;

    public Long getAuthId() { return authId; }
    public void setAuthId(Long authId) { this.authId = authId; }
    public Long getElectionId() { return electionId; }
    public void setElectionId(Long electionId) { this.electionId = electionId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getParty() { return party; }
    public void setParty(String party) { this.party = party; }
    public String getManifesto() { return manifesto; }
    public void setManifesto(String manifesto) { this.manifesto = manifesto; }
}
