package onlinevotingsystem.dto;

public class ElectionSummaryDto {
    private Long electionId;
    private String title;
    private long totalVotes;

    public ElectionSummaryDto() {}
    public ElectionSummaryDto(Long electionId, String title, long totalVotes) {
        this.electionId = electionId;
        this.title = title;
        this.totalVotes = totalVotes;
    }

    public Long getElectionId() { return electionId; }
    public void setElectionId(Long electionId) { this.electionId = electionId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public long getTotalVotes() { return totalVotes; }
    public void setTotalVotes(long totalVotes) { this.totalVotes = totalVotes; }
}
