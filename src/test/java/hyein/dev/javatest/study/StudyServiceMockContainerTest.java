package hyein.dev.javatest.study;

import hyein.dev.javatest.domain.Member;
import hyein.dev.javatest.domain.Study;
import hyein.dev.javatest.member.MemberService;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.C;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.File;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

/**
 * test container test
 */
@SpringBootTest
@ContextConfiguration(initializers = StudyServiceMockContainerTest.ContainerPropertyInitializer.class)
@Testcontainers // 확장체 지원하는 composed annotation
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@ActiveProfiles("test")
@Slf4j
class StudyServiceMockContainerTest {

    @Mock
    MemberService memberService;

    @Mock
    StudyRepository studyRepository;

    @Autowired
    Environment environment;

    @Value("${container.port}")
    int port;

//    @Container
    static PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer("postgres:9.6.12")
            .withDatabaseName("studytest")
            ; // 모든 테스트에서 공유하기 위해 static 선언


//    @Container
    static GenericContainer container = new GenericContainer("postgres:9.6.12")// 지원하는 클래스 없을 때 GenericContainer 로 생성 가능. 로컬에서 이미지 찾고 없으면 땡겨온다.
            .withExposedPorts(5432) // 컨테이너 내 포트
            .withEnv("POSTGRES_DB", "studytest")
//            .waitingFor(Wait.forListeningPort()) // 특정 포트 기다리기
//            .waitingFor(Wait.forHttp("/hello")) // uri 응답 기다리기
//            .waitingFor(Wait.forLogMessage("regex", 1))// 컨테이너의 특정한 로그 메시지 기다리기
            ;

    @Container
    static DockerComposeContainer dockerComposeContainer = new DockerComposeContainer(new File("src/test/resources/docker-compose.yml"))
            .withExposedService("study-db", 5432)
            ;

//    @BeforeAll
//    static void beforeAll() {
//        log.info("host port: {}", container.getMappedPort(5432)); // 바인딩된 호스트 포트
//        log.info("getLogs(): {}", container.getLogs()); // 컨테이너 안 모든 로그
//
//        container.followOutput(new Slf4jLogConsumer(log)); // log streaming
//    }
//
    @BeforeEach
    void beforeEach() {
        log.info("host port by spring environment: {}", environment.getProperty("container.port"));
        log.info("host port by spring value: {}", port);
    }

    @Test
    public void createNewStudy() {
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


    static class ContainerPropertyInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(ConfigurableApplicationContext applicationContext) {
            TestPropertyValues.of("container.port=" + dockerComposeContainer.getServicePort("study-db", 5432))
//            TestPropertyValues.of("container.port=" + container.getMappedPort(5432))
            .applyTo(applicationContext.getEnvironment()) // applicationContext의 environment 에다 프로퍼티를 apply 한다.
            ;
        }
    }
}