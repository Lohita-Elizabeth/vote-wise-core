package onlinevotingsystem.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

@Entity
@Table(name = "voters", uniqueConstraints = {
        @UniqueConstraint(name = "uk_voter_voterIdNumber", columnNames = "voterIdNumber"),
        @UniqueConstraint(name = "uk_voter_auth", columnNames = "auth_id")
})
public class Voter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false)
    @JoinColumn(name = "auth_id", nullable = false)
    private Auth auth;

    @NotBlank
    private String name;

    @NotNull
    private LocalDate dob;

    private String address;

    @NotBlank
    private String voterIdNumber;

    private boolean eligible = true;

    private boolean hasVoted = false;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Auth getAuth() { return auth; }
    public void setAuth(Auth auth) { this.auth = auth; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public LocalDate getDob() { return dob; }
    public void setDob(LocalDate dob) { this.dob = dob; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getVoterIdNumber() { return voterIdNumber; }
    public void setVoterIdNumber(String voterIdNumber) { this.voterIdNumber = voterIdNumber; }
    public boolean isEligible() { return eligible; }
    public void setEligible(boolean eligible) { this.eligible = eligible; }
    public boolean isHasVoted() { return hasVoted; }
    public void setHasVoted(boolean hasVoted) { this.hasVoted = hasVoted; }
}
