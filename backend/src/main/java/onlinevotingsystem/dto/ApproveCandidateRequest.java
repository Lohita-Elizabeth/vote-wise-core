package onlinevotingsystem.dto;

import jakarta.validation.constraints.NotNull;

public class ApproveCandidateRequest {
    @NotNull
    private Boolean approved;

    public Boolean getApproved() { return approved; }
    public void setApproved(Boolean approved) { this.approved = approved; }
}
