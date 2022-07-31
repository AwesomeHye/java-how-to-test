package hyein.dev.javatest.member;

import hyein.dev.javatest.domain.Member;
import hyein.dev.javatest.domain.Study;

import java.util.Optional;

public interface MemberService {
    Optional<Member> findById(Long memberId) throws MemberNotFoundException;
    void validate(Long memberId);

    void notify(Study study);
    void notify(Member member);
}
