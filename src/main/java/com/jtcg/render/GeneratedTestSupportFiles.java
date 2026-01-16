package com.jtcg.render;

import java.util.List;

/**
 * 생성된 테스트 코드에서 공통으로 사용하는 유틸 소스 파일 모음.
 *
 * <p>생성된 테스트들은 패키지별로 흩어져 만들어지기 때문에, 클래스 내부에 헬퍼를 인라인으로 넣으면
 * 파일마다 동일한 코드가 반복됩니다. 이 클래스를 통해 공통 유틸을 한 번만 생성하고, 각 테스트는 이를 import 하도록 합니다.
 */
public final class GeneratedTestSupportFiles {

    /** 생성되는 유틸의 고정 패키지. */
    public static final String SUPPORT_PACKAGE = "com.jtcg.generated.support";

    /**
     * 출력 디렉터리 루트에 함께 생성할 공통 유틸 파일들.
     */
    public List<SupportFile> files() {
        return List.of(
                new SupportFile(SUPPORT_PACKAGE, "ReflectionTestSupport.java", reflectionTestSupportSource()),
                new SupportFile(SUPPORT_PACKAGE, "ControllerTestSupport.java", controllerTestSupportSource())
        );
    }

    /** 단일 지원 파일 정의. */
    public record SupportFile(String packageName, String fileName, String sourceCode) {
    }

    private static String reflectionTestSupportSource() {
        return """
                package %s;

                import java.lang.reflect.Method;
                import java.lang.reflect.Modifier;

                /**
                 * 생성된 Service 테스트에서 사용하는 리플렉션 유틸.
                 *
                 * <p>기본 정책:
                 * <ul>
                 *   <li>메서드는 이름 + 파라미터 개수로 찾습니다(정규식 파서의 한계 때문에 타입까지는 모름).</li>
                 *   <li>인스턴스 메서드면 기본 생성자(무인자)로 인스턴스 생성을 시도합니다.</li>
                 *   <li>파라미터는 primitive=0/false, String="", 그 외=null 로 채웁니다.</li>
                 * </ul>
                 */
                public final class ReflectionTestSupport {

                    private ReflectionTestSupport() {
                    }

                    public static Method findMethod(Class<?> type, String name, int paramCount) {
                        // public 메서드(상속 포함) 우선
                        for (Method m : type.getMethods()) {
                            if (m.getName().equals(name) && m.getParameterCount() == paramCount) {
                                return m;
                            }
                        }
                        // declared 메서드(비공개 포함)
                        for (Method m : type.getDeclaredMethods()) {
                            if (m.getName().equals(name) && m.getParameterCount() == paramCount) {
                                return m;
                            }
                        }
                        return null;
                    }

                    public static Object tryCreate(Class<?> type) {
                        try {
                            var ctor = type.getDeclaredConstructor();
                            ctor.setAccessible(true);
                            return ctor.newInstance();
                        } catch (Exception e) {
                            return null;
                        }
                    }

                    public static Object invokeWithDefaults(Method method, Object target) throws Exception {
                        method.setAccessible(true);
                        Class<?>[] types = method.getParameterTypes();
                        Object[] args = new Object[types.length];
                        for (int i = 0; i < types.length; i++) {
                            args[i] = defaultValue(types[i]);
                        }
                        return method.invoke(target, args);
                    }

                    public static Object targetOrNullFor(Method method, Class<?> declaringType) {
                        if (method == null) {
                            return null;
                        }
                        if (Modifier.isStatic(method.getModifiers())) {
                            return null;
                        }
                        return tryCreate(declaringType);
                    }

                    private static Object defaultValue(Class<?> type) {
                        if (!type.isPrimitive()) {
                            if (type == String.class) return "";
                            return null;
                        }
                        if (type == boolean.class) return false;
                        if (type == char.class) return (char) 0;
                        if (type == byte.class) return (byte) 0;
                        if (type == short.class) return (short) 0;
                        if (type == int.class) return 0;
                        if (type == long.class) return 0L;
                        if (type == float.class) return 0.0f;
                        if (type == double.class) return 0.0d;
                        return 0;
                    }
                }
                """.formatted(SUPPORT_PACKAGE);
    }

    private static String controllerTestSupportSource() {
        return """
                package %s;

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
                        return path.replaceAll("\\\\{[^/]+?\\\\}", "1");
                    }
                }
                """.formatted(SUPPORT_PACKAGE);
    }
}
