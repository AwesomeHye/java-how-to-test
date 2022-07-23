package hyein.dev.javatest.domain;

import hyein.dev.javatest.study.StudyStatus;

import java.time.LocalDateTime;

public class Study {
    private StudyStatus studyStatus = StudyStatus.DRAFT;
    private int limit = 0;
    private String name;
    private LocalDateTime openedDateTime;
    private Member owner;

    public Study() {
    }

    public LocalDateTime getOpenedDateTime() {
        return openedDateTime;
    }

    public void setOpenedDateTime(LocalDateTime openedDateTime) {
        this.openedDateTime = openedDateTime;
    }

    public Study(int limit, String name) {
        this.limit = limit;
        this.name = name;
    }

    public Study(int limit) {
        if(limit < 0) {
            throw new IllegalArgumentException("최소 인원은 0보다 커야 한다.");
        }

        this.limit = limit;
    }

    public StudyStatus getStatus() {
        return studyStatus;
    }

    public int getLimit() {
        return limit;
    }

    public void setOwner(Member member) {
        this.owner = member;
    }

    public Member getOwner() {
        return owner;
    }

    public void open() {
        this.studyStatus = StudyStatus.OPENED;
        this.openedDateTime = LocalDateTime.now();
    }
}
