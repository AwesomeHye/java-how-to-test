package hyein.dev.javatest.study;

import hyein.dev.javatest.domain.Member;
import hyein.dev.javatest.domain.Study;
import hyein.dev.javatest.member.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

/**
 * Mock test
 */
@ExtendWith(MockitoExtension.class) // @Mock 애노테이션을 처리해준다.
@MockitoSettings(strictness = Strictness.LENIENT)
class StudyServiceMockTest {

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

    @Test
    public void createNewStudy(@Mock MemberService memberService, @Mock StudyRepository studyRepository) {
        Study study = new Study(10, "테스트");
        Member member = new Member(1L, "gmail.com");

        // stubbing
        when(memberService.findById(1L)).thenReturn(Optional.of(member));
        when(studyRepository.save(study)).thenReturn(study);

        // when
        StudyService studyService = new StudyService(memberService, studyRepository);
        studyService.createNewStudy(1L, study);

        // then
        assertNotNull(study.getOwner());
        assertEquals(member, study.getOwner());
    }

    @Test
    @DisplayName("메소드 호출 횟수 검증")
    public void verifyCallCnt(@Mock MemberService memberService) {
        Study study = new Study(10, "테스트");

        verify(memberService, never()).notify(study); // memberService.notify() 한 번도 호출 안 돼야함

        memberService.notify(study);
        verify(memberService, times(1)).notify(study); // memberService.notify() 한 번 호출 돼야함
    }

    @Test
    @DisplayName("메소드 호출 순서 검증")
    public void verifyCallOrder(@Mock MemberService memberService) {
        Study study = new Study(10, "테스트");
        Member member = new Member(1L, "gmail.com");

        memberService.notify(study);
        memberService.notify(member);

        InOrder inOrder = inOrder(memberService);
        inOrder.verify(memberService).notify(study); // memberService.notify(study) 호출 다음에
        inOrder.verify(memberService).notify(member); // memberService.notify(member) 호출 돼야함
    }

    @Test
    @DisplayName("더 이상 검증할 메소드가 없는지 체크")
    public void verifyNoMoreCall(@Mock MemberService memberService) {
        Study study = new Study(10, "테스트");

        memberService.notify(study);
//        verify(memberService, times(1)).notify(study);
        verifyNoMoreInteractions(memberService);
    }

    @Test
    @DisplayName("mockito 를 BDD style로 변경하기")
    public void bddMockito(@Mock MemberService memberService, @Mock StudyRepository studyRepository) {
        // given
        Study study = new Study(10, "테스트");
        Member member = new Member(1L, "gmail.com");

//        Mockito.when(memberService.findById(1L)).thenReturn(Optional.of(member));
//        Mockito.when(studyRepository.save(study)).thenReturn(study);
        given(memberService.findById(1L)).willReturn(Optional.of(member));
        given(studyRepository.save(study)).willReturn(study);

        // when
        memberService.notify(study);

        // then
//        Mockito.verify(memberService, times(1)).notify(study);
//        Mockito.verifyNoMoreInteractions(memberService);
        BDDMockito.then(memberService).should(times(1)).notify(study);
        BDDMockito.then(memberService).shouldHaveNoMoreInteractions();;
    }

    @Test
    @DisplayName("다른 사용자가 볼 수 있도록 스터디를 공개한다.")
    public void openStudy(@Mock MemberService memberService, @Mock StudyRepository studyRepository) {
        // given
        StudyService studyService = new StudyService(memberService, studyRepository);
        Study study = new Study(10, "테스트");
        given(studyRepository.save(study)).willReturn(study);

        // when
        studyService.openStudy(study);

        // then
        assertEquals(StudyStatus.OPENED, study.getStatus());
        assertNotNull(study.getOpenedDateTime());
        then(memberService).should(times(1)).notify(study);
    }


}