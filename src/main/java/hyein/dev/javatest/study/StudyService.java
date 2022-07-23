package hyein.dev.javatest.study;

import hyein.dev.javatest.domain.Study;
import hyein.dev.javatest.domain.Member;
import hyein.dev.javatest.member.MemberService;

import java.util.Optional;

public class StudyService {
    private final MemberService memberService;
    private final StudyRepository repository;

    public StudyService(MemberService memberService, StudyRepository repository) {
        assert memberService != null;
        assert repository != null;
        this.memberService = memberService;
        this.repository = repository;
    }

    /**
     * 멤버ID 가 스터디를 만든다.
     * @param memberId
     * @param study
     * @return
     */
    public Study createNewStudy(Long memberId, Study study) {
        Optional<Member> member = memberService.findById(memberId);
        study.setOwner(member.orElseThrow(() -> new IllegalArgumentException("Member must not be null")));
        return repository.save(study);
    }

    public Study openStudy(Study study) {
        study.open();
        Study openedStudy = repository.save(study);
        memberService.notify(openedStudy);
        return openedStudy;
    }

}
