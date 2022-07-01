package hyein.dev.javatest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@SpringBootTest
class StudyTest {

    @Test
    @DisplayName("스터디_테스트_😂")
    void create_new_test() {
        Study study = new Study();
        assertNotNull(study);
        assertEquals(StudyStatus.DRAFT, study.getStatus(), () -> "스터디를 처음 만들면 상태값이 " + StudyStatus.DRAFT + " 여야 한다.");
    }

    @Test
    @DisplayName("모든 테스트 한 번에 실행")
    void execute_all_test() {
        Study study = new Study();

        assertAll(
                () -> assertNotNull(study),
                () -> assertEquals(StudyStatus.END, study.getStatus(), () -> "스터디를 처음 만들면 상태값이 " + StudyStatus.DRAFT + " 여야 한다."),
                () -> assertTrue(study.getLimit() > 0, "스터디 인원 수는 0명을 넘어야한다.")
        );
    }

    @Test
    @DisplayName("익셉션 발생 테스트")
    void create_study_limit() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> new Study(-10));
        assertEquals("최소 인원은 0보다 커야 한다.", exception.getMessage());
    }

    @Test
    @DisplayName("타임아웃 테스트")
    void create_study_timeout() {
        assertTimeoutPreemptively(Duration.ofMillis(100), () -> {
            new Study(10);
            Thread.sleep(300);
        });
    }

    @Test
    @DisplayName("assertThat 테스트")
    void create_study_that() {
        Study actual = new Study(10);
        assertThat(actual.getLimit()).isGreaterThan(0);
    }
}
