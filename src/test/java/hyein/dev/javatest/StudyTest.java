package hyein.dev.javatest;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.*;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.AggregateWith;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.aggregator.ArgumentsAggregationException;
import org.junit.jupiter.params.aggregator.ArgumentsAggregator;
import org.junit.jupiter.params.converter.ArgumentConversionException;
import org.junit.jupiter.params.converter.ConvertWith;
import org.junit.jupiter.params.converter.SimpleArgumentConverter;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.junit.jupiter.api.Assumptions.assumingThat;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
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

    @Test
    @DisplayName("assume 테스트")
    void assume_true() {
        String env = System.getenv("PROCESSOR_LEVEL");
        System.out.println(env);
        assumeTrue("6".equalsIgnoreCase(env)); // 이 조건이 만족해야지만 하위 코드 실행

        assumingThat("6".equalsIgnoreCase(env), () -> {
            Study actual = new Study(10);
            assertThat(actual.getLimit()).isGreaterThan(0);
        });

        assumingThat("8".equalsIgnoreCase(env), () -> {
            Study actual = new Study(80);
            assertThat(actual.getLimit()).isGreaterThan(0);
        });
    }

    @Test
    @DisplayName("@Enabled OS 테스트")
    @EnabledOnOs({OS.WINDOWS, OS.LINUX})
    public void window() {
        System.out.println("WINDOW/LINUX TEST");
    }

    @Test
    @DisplayName("@Disabled OS 테스트")
    @DisabledOnOs({OS.LINUX})
    public void not_linux() {
        System.out.println("LINUX TEST");
    }

    @Test
    @DisplayName("@Enabled JRE 테스트")
    @EnabledOnJre({JRE.JAVA_8, JRE.JAVA_9, JRE.JAVA_10, JRE.JAVA_11})
    public void jre() {
        System.out.println("JRE TEST");
    }

    @Test
    @DisplayName("@Enabled 환경변수 테스트")
    @EnabledIfEnvironmentVariable(named = "PROCESSOR_LEVEL", matches = "6")
    public void env() {
        System.out.println("env TEST");
    }

    @DisplayName("@Tag 테스트")
    @FastTest
    public void tag() {
        System.out.println("fast");
    }

    @DisplayName("반복 테스트")
    @RepeatedTest(value = 3, name = "{displayName}, {currentRepetition} / {totalRepetitions}")
    public void repeat(RepetitionInfo repetitionInfo) {
        System.out.println("repeat: " + repetitionInfo.getCurrentRepetition() + "/" + repetitionInfo.getTotalRepetitions());
    }

    @DisplayName("여러 인자 객체 바인딩")
    @ParameterizedTest(name = "{index} {displayName} name={0}")
    @CsvSource({"10, '자바 스터디", "20, '스프링'"})
    public void param(@AggregateWith(StudyAggregator.class) Study study) {
        System.out.println(study);
    }

    static class StudyAggregator implements ArgumentsAggregator {
        @Override
        public Object aggregateArguments(ArgumentsAccessor argumentsAccessor, ParameterContext parameterContext) throws ArgumentsAggregationException {
            return new Study(argumentsAccessor.getInteger(0), argumentsAccessor.getString(1));
        }
    }

    @DisplayName("인자 1개 객체 바인딩")
    @ParameterizedTest(name = "{index} {displayName} limit={0}")
    @ValueSource(ints = {10, 20})
    public void study(@ConvertWith(StudyConverter.class) Study study) {
        System.out.println(study.toString());
    }

    static class StudyConverter extends SimpleArgumentConverter {
        @Override
        protected Object convert(Object source, Class<?> targetClass) throws ArgumentConversionException {
            assertEquals(Study.class, targetClass, "Can only convert to study");
            return new Study(Integer.parseInt(source.toString()));
        }
    }
}