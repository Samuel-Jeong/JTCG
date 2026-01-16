package com.jtcg.render;

import com.jtcg.parse.JavaMethodInfo;
import com.jtcg.parse.JavaSourceInfo;

import java.util.List;

/**
 * {@link com.jtcg.parse.JavaSourceInfo}를 기반으로 JUnit5 테스트 스켈레톤 소스 코드를 생성합니다.
 *
 * <p>현재 버전은 "자동으로 실행 가능한 최소 테스트"를 목표로 합니다.
 * 구체적으로는 각 public 메서드에 대해 리플렉션으로 호출을 시도하고, 기본 인자 값으로 호출했을 때
 * 예외가 발생하지 않는지(`assertDoesNotThrow`)를 검증합니다.
 *
 * <p>대상 타입이 기본 생성자(무인자 생성자)가 없으면 인스턴스 생성이 불가능하므로 해당 테스트는
 * 실패시키지 않고 {@code Assumptions}로 스킵합니다.
 */
public final class JunitTestRenderer {

    /**
     * 테스트 소스 코드를 문자열로 렌더링합니다.
     *
     * <p>공개 메서드가 하나도 잡히지 않는 경우에도 빈 파일이 되지 않도록 `placeholder` 테스트를 생성합니다.
     */
    public String render(JavaSourceInfo info) {
        if (info.componentType() == null) {
            return renderServiceTest(info);
        }
        return switch (info.componentType()) {
            case CONTROLLER -> renderControllerTest(info);
            case SERVICE -> renderServiceTest(info);
            case OTHER -> renderServiceTest(info);
        };
    }

    private String renderServiceTest(JavaSourceInfo info) {
        StringBuilder out = new StringBuilder();
        if (info.packageName() != null && !info.packageName().isBlank()) {
            out.append("package ").append(info.packageName()).append(";\n\n");
        }

        out.append("import com.jtcg.generated.support.ReflectionTestSupport;\n\n");

        out.append("import org.junit.jupiter.api.Assumptions;\n");
        out.append("import org.junit.jupiter.api.Test;\n\n");
        out.append("import java.lang.reflect.Method;\n\n");

        out.append("import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;\n");
        out.append("import static org.junit.jupiter.api.Assertions.assertNotNull;\n\n");

        String testClassName = info.typeName() + "Test";
        out.append("class ").append(testClassName).append(" {\n\n");

        List<JavaMethodInfo> methods = info.publicMethods();
        if (methods == null || methods.isEmpty()) {
            out.append("    @Test\n");
            out.append("    void placeholder() {\n");
            out.append("        assertDoesNotThrow(() -> {\n");
            out.append("            // no public methods found by parser\n");
            out.append("        });\n");
            out.append("    }\n\n");
        } else {
            for (JavaMethodInfo m : methods) {
                String name = m.name();
                int paramCount = Math.max(0, m.paramCount());
                out.append("    @Test\n");
                out.append("    void test_")
                        .append(sanitize(name))
                        .append("__")
                        .append(paramCount)
                        .append("params() throws Exception {\n");
                out.append("        Method method = ReflectionTestSupport.findMethod(")
                        .append(info.typeName())
                        .append(".class, \"")
                        .append(escapeJavaString(name))
                        .append("\", ")
                        .append(paramCount)
                        .append(");\n");
                out.append("        Assumptions.assumeTrue(method != null, \"Method not found by reflection: ")
                        .append(escapeJavaString(name))
                        .append("\");\n");

                out.append("        Object target = ReflectionTestSupport.targetOrNullFor(method, ")
                        .append(info.typeName())
                        .append(".class);\n");
                out.append("        if (!java.lang.reflect.Modifier.isStatic(method.getModifiers())) {\n");
                out.append("            Assumptions.assumeTrue(target != null, \"No default constructor: ")
                        .append(info.typeName())
                        .append("\");\n");
                out.append("        }\n\n");

                out.append("        Object result = assertDoesNotThrow(() -> ReflectionTestSupport.invokeWithDefaults(method, target));\n");
                out.append("        if (method.getReturnType() != void.class && !method.getReturnType().isPrimitive()) {\n");
                out.append("            assertNotNull(result);\n");
                out.append("        }\n");
                out.append("    }\n\n");
            }
        }

        out.append("}\n");
        return out.toString();
    }

    private String renderControllerTest(JavaSourceInfo info) {
        StringBuilder out = new StringBuilder();
        if (info.packageName() != null && !info.packageName().isBlank()) {
            out.append("package ").append(info.packageName()).append(";\n\n");
        }

        out.append("import com.jtcg.generated.support.ControllerTestSupport;\n\n");

        out.append("import org.junit.jupiter.api.Assumptions;\n");
        out.append("import org.junit.jupiter.api.Test;\n\n");

        out.append("import org.springframework.http.MediaType;\n");
        out.append("import org.springframework.test.web.servlet.MockMvc;\n");
        out.append("import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;\n");
        out.append("import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;\n\n");

        out.append("import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;\n\n");

        String testClassName = info.typeName() + "Test";
        out.append("class ").append(testClassName).append(" {\n\n");

        out.append("    private MockMvc mockMvc() {\n");
        out.append("        return ControllerTestSupport.mockMvcFor(").append(info.typeName()).append(".class);\n");
        out.append("    }\n\n");

        var endpoints = info.endpoints();
        if (endpoints == null || endpoints.isEmpty()) {
            out.append("    @Test\n");
            out.append("    void placeholder() {\n");
            out.append("        // no endpoint mappings found by parser\n");
            out.append("        Assumptions.assumeTrue(true);\n");
            out.append("    }\n\n");
        } else {
            for (var ep : endpoints) {
                String methodName = ep.javaMethodName();
                int paramCount = Math.max(0, ep.paramCount());
                String httpMethod = ep.httpMethod();
                String path = ep.fullPath();

                out.append("    @Test\n");
                out.append("    void api_")
                        .append(sanitize(methodName))
                        .append("__")
                        .append(paramCount)
                        .append("params__")
                        .append(sanitize(httpMethod))
                        .append("() throws Exception {\n");
                out.append("        MockMvc mvc = mockMvc();\n");
                out.append("        String path = ControllerTestSupport.fillPathVariables(\"")
                        .append(escapeJavaString(path))
                        .append("\");\n");
                out.append("        MockHttpServletRequestBuilder req = MockMvcRequestBuilders.")
                        .append(toMockMvcBuilderMethod(httpMethod))
                        .append("(path)\n")
                        .append("                .accept(MediaType.APPLICATION_JSON)");

                String hm = httpMethod == null ? "GET" : httpMethod.toUpperCase();
                if (hm.equals("POST") || hm.equals("PUT") || hm.equals("PATCH")) {
                    out.append("\n                .contentType(MediaType.APPLICATION_JSON)\n                .content(\"{}\")");
                }

                out.append(";\n");
                out.append("        mvc.perform(req)\n");
                out.append("                .andExpect(status().is2xxSuccessful());\n");
                out.append("    }\n\n");
            }
        }

        out.append("    private static String toMockMvcBuilderMethod(String httpMethod) {\n");
        out.append("        if (httpMethod == null) return \"get\";\n");
        out.append("        return switch (httpMethod.toUpperCase()) {\n");
        out.append("            case \"POST\" -> \"post\";\n");
        out.append("            case \"PUT\" -> \"put\";\n");
        out.append("            case \"DELETE\" -> \"delete\";\n");
        out.append("            case \"PATCH\" -> \"patch\";\n");
        out.append("            default -> \"get\";\n");
        out.append("        };\n");
        out.append("    }\n\n");

        out.append("}\n");
        return out.toString();
    }

    private static String toMockMvcBuilderMethod(String httpMethod) {
        if (httpMethod == null) {
            return "get";
        }
        return switch (httpMethod.toUpperCase()) {
            case "POST" -> "post";
            case "PUT" -> "put";
            case "DELETE" -> "delete";
            case "PATCH" -> "patch";
            default -> "get";
        };
    }

    private static String escapeJavaString(String s) {
        if (s == null) {
            return "";
        }
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    private static String sanitize(String s) {
        // JUnit 메서드명으로 무난하게 사용: 식별자에 쓰기 곤란한 문자를 '_'로 치환
        return s.replaceAll("[^A-Za-z0-9_]", "_");
    }
}
