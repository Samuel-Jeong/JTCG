package com.jtcg.generated.support;

import org.junit.jupiter.api.Assumptions;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

/**
 * 생성된 Controller 테스트에서 사용하는 공통 유틸.
 */
public final class ControllerTestSupport {

    private ControllerTestSupport() {
    }

    /**
     * 기본 생성자(무인자)로 컨트롤러 인스턴스를 만들 수 있을 때만 MockMvc를 구성합니다.
     * 만들 수 없으면 테스트를 실패시키지 않고 skip 합니다.
     */
    public static MockMvc mockMvcFor(Class<?> controllerType) {
        Object controller = ReflectionTestSupport.tryCreate(controllerType);
        Assumptions.assumeTrue(controller != null, "No default constructor: " + controllerType.getSimpleName());
        return MockMvcBuilders.standaloneSetup(controller).build();
    }

    /**
     * 경로에 포함된 path variable(예: /foo/{id})을 기본값으로 치환합니다.
     *
     * <p>단순 치환이며, 실제 컨트롤러의 제약조건을 만족하는 값을 보장하지는 않습니다.
     */
    public static String fillPathVariables(String path) {
        if (path == null || path.isBlank()) {
            return "/";
        }
        // {var} 형태를 1로 치환
        return path.replaceAll("\\{[^/]+?\\}", "1");
    }
}
