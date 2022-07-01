package hyein.dev.javatest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@SpringBootTest
class StudyTest {

    @Test
    @DisplayName("스터디_테스트_😂")
    void create_new_test() {
        System.out.println("it is test");
    }
}
