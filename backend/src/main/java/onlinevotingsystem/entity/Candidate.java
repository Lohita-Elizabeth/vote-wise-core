package onlinevotingsystem.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "candidates", uniqueConstraints = {
        @UniqueConstraint(name = "uk_candidate_auth", columnNames = "auth_id")
})
public class Candidate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false)
    @JoinColumn(name = "auth_id", nullable = false)
    private Auth auth;

    @ManyToOne(optional = false)
    @JoinColumn(name = "election_id", nullable = false)
    private Election election;

    @NotBlank
    private String name;

    private String party;

    @Column(length = 4000)
    private String manifesto;

    @NotNull
    private Boolean approved = Boolean.FALSE;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Auth getAuth() { return auth; }
    public void setAuth(Auth auth) { this.auth = auth; }
    public Election getElection() { return election; }
    public void setElection(Election election) { this.election = election; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getParty() { return party; }
    public void setParty(String party) { this.party = party; }
    public String getManifesto() { return manifesto; }
    public void setManifesto(String manifesto) { this.manifesto = manifesto; }
    public Boolean getApproved() { return approved; }
    public void setApproved(Boolean approved) { this.approved = approved; }
}
