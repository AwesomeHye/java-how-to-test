package hyein.dev.javatest.member;

import hyein.dev.javatest.domain.Member;

import java.util.Optional;

public interface MemberService {
    Optional<Member> findById(Long memberId) throws MemberNotFoundException;
    void validate(Long memberId);
}
