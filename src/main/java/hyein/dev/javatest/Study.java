package hyein.dev.javatest;

public class Study {
    private StudyStatus studyStatus = StudyStatus.DRAFT;
    private int limit = 0;

    public Study() {

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
}
