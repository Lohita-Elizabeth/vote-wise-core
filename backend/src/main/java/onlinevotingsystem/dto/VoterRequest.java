package onlinevotingsystem.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class VoterRequest {
    @NotNull
    private Long authId;
    @NotBlank
    private String name;
    @NotNull
    private LocalDate dob;
    private String address;
    @NotBlank
    private String voterIdNumber;
    private Boolean eligible = true;

    public Long getAuthId() { return authId; }
    public void setAuthId(Long authId) { this.authId = authId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public LocalDate getDob() { return dob; }
    public void setDob(LocalDate dob) { this.dob = dob; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getVoterIdNumber() { return voterIdNumber; }
    public void setVoterIdNumber(String voterIdNumber) { this.voterIdNumber = voterIdNumber; }
    public Boolean getEligible() { return eligible; }
    public void setEligible(Boolean eligible) { this.eligible = eligible; }
}
