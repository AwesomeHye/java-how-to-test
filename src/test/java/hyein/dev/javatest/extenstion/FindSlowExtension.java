package hyein.dev.javatest.extenstion;

import hyein.dev.javatest.annotation.SlowTest;
import org.junit.jupiter.api.extension.*;

import java.lang.reflect.Method;

/**
 * 테스트 수행 시간 측정 후 임계 시간을 넘으면 @SlowTest 선언을 유도하는 extension
 */
public class FindSlowExtension implements BeforeTestExecutionCallback, AfterTestExecutionCallback {

    public static final String START_TIME = "START_TIME";
    private long THRESHOLD = 1000L;

    public FindSlowExtension(long threshold) {
        this.THRESHOLD = threshold;
    }


    @Override
    public void beforeTestExecution(ExtensionContext context) throws Exception {
        ExtensionContext.Store store = getStore(context);

        // store 에 START_TIME 넣기
        store.put(START_TIME, System.currentTimeMillis());
    }

    @Override
    public void afterTestExecution(ExtensionContext context) throws Exception {
        ExtensionContext.Store store = getStore(context);

        Method testMethod = context.getRequiredTestMethod();
        // @SlowTest 애노테이션이 있는지 검증하기 위해
        SlowTest annotation = testMethod.getAnnotation(SlowTest.class);

        // store 에서 START_TIME 꺼내기
        long startTime = store.remove(START_TIME, Long.class);
        long duration = System.currentTimeMillis() - startTime;
        if(duration > THRESHOLD && annotation == null) {
            System.out.printf("Please consider mark method [%s] with @SlowTest\n", testMethod.getName());
        }
    }

    /**
     * testClassName, testMethodName 로 구성된 namespace의 store 반환
     * @param context
     * @return Store
     */
    private ExtensionContext.Store getStore(ExtensionContext context) {
        String testClassName = context.getRequiredTestClass().getName();
        String testMethodName = context.getRequiredTestMethod().getName();

        /* namespace 라는 키에 값을 넣고 뺄 수 있는 store가 있다. */
        // 1) namespace 생성
        ExtensionContext.Namespace namespace = ExtensionContext.Namespace.create(testClassName, testMethodName);
        // 2) namespace로 store 가져오기
        return context.getStore(namespace);
    }
}
