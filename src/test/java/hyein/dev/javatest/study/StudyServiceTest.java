package hyein.dev.javatest.study;

import hyein.dev.javatest.domain.Member;
import hyein.dev.javatest.domain.Study;
import hyein.dev.javatest.member.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class) // @Mock 애노테이션을 처리해준다.
@MockitoSettings(strictness = Strictness.LENIENT)
class StudyServiceTest {

    @Mock // Mockito.mock(MemberService.class) 을 간단하게 애노테이션으로 만들 수 있다.
    MemberService memberService;

    @Mock
    StudyRepository studyRepository;

    /**
     * mocking 하기 좋은 경우: 인터페이스를 사용해 개발하는 경우. 구현체를 mocking하면 좋다.
     */
    @Test
    @DisplayName("Mock을 사용해 StudyService 생성하기")
    void createStudyService(@Mock MemberService memberService, @Mock StudyRepository studyRepository) {
        StudyService studyService = new StudyService(memberService, studyRepository);

        assertNotNull(studyService);
    }

    @Test
    @DisplayName("return 있는 메소드 stubbing 하기")
    void stubReturnMethod(@Mock MemberService memberService) {
        String email = "catsarah3333@gmail.com";
        Member member = new Member(1L, email);
        // stubbing
        when(memberService.findById(1L)).thenReturn(Optional.of(member)); // memberId가 1L 이어야지만 member 를 반환한다.
        when(memberService.findById(any())).thenReturn(Optional.of(member)); // ArgumentMatchers.any(): memberId가 뭐든간에 member 를 반환한다.
        when(memberService.findById(-1L)).thenThrow(new RuntimeException()); // 인자가 -1L이면 RuntimeException을 던져라.

        assertEquals(email, memberService.findById(1L).get().getEmail());
    }

    @Test
    @DisplayName("return 없는 메소드 예외 발생 stubbing 하기")
    void stubVoidMethod(@Mock MemberService memberService) {
        // stubbing
        // void 메소드에서 예외 던지기
        doThrow(new RuntimeException()).when(memberService).validate(-10L);

        assertThrows(RuntimeException.class, () -> memberService.validate(-10L));
    }

    @Test
    @DisplayName("동일한 파라미터로 여러 번 호출될 때마다 각기 다르게 return 하도록 stubbing 하기")
    void stubDifferentReturnMethod(@Mock MemberService memberService) {

        String email = "catsarah3333@gmail.com";
        Member member = new Member(1L, email);
        // stubbing
        when(memberService.findById(any()))
                .thenReturn(Optional.of(member)) // findById() 1번쨰 호출: Optional<Member> 반환
                .thenThrow(RuntimeException.class) // findById() 2번쨰 호출: RuntimeException 던짐
                .thenReturn(Optional.empty()) // findById() 3번쨰 호출: Optional.empty() 반환
                ;

        assertEquals(email, memberService.findById(1L).get().getEmail());
        assertThrows(RuntimeException.class, () -> memberService.findById(3L));
        assertEquals(Optional.empty(), memberService.findById(10L));
    }
}