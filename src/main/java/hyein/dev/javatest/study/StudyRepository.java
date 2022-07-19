package hyein.dev.javatest.study;

import hyein.dev.javatest.domain.Study;

public interface StudyRepository {
    Study save(Study study);
}
