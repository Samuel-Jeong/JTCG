package com.jtcg.render;

import com.jtcg.parse.JavaMethodInfo;
import com.jtcg.parse.JavaSourceInfo;
import com.jtcg.parse.DtoIndex;

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
    public String render(JavaSourceInfo info, DtoIndex dtoIndex) {
        if (info.componentType() == null) {
            return renderServiceTest(info);
        }
        return switch (info.componentType()) {
            case CONTROLLER -> renderControllerTest(info, dtoIndex);
            case SERVICE -> renderServiceTest(info);
            case OTHER -> renderServiceTest(info);
        };
    }

    /**
     * 이전 버전과의 호환을 위해 DTO 인덱스 없이도 호출 가능하게 둡니다.
     */
    public String render(JavaSourceInfo info) {
        return render(info, null);
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

                out.append("        ReflectionTestSupport.InvocationPlan plan = ReflectionTestSupport.planInvocation(method, ")
                        .append(info.typeName())
                        .append(".class);\n");
                out.append("        Assumptions.assumeTrue(plan.skipReason == null, plan.skipReason);\n\n");

                out.append("        Object result = assertDoesNotThrow(() -> ReflectionTestSupport.invoke(method, plan.target, plan.args));\n");
                out.append("        if (method.getReturnType() != void.class && !method.getReturnType().isPrimitive()) {\n");
                out.append("            assertNotNull(result);\n");
                out.append("        }\n");
                out.append("    }\n\n");
            }
        }

        out.append("}\n");
        return out.toString();
    }

    private String renderControllerTest(JavaSourceInfo info, DtoIndex dtoIndex) {
        StringBuilder out = new StringBuilder();
        if (info.packageName() != null && !info.packageName().isBlank()) {
            out.append("package ").append(info.packageName()).append(";\n\n");
        }

        out.append("import com.jtcg.generated.support.ControllerTestSupport;\n\n");

        out.append("import org.junit.jupiter.api.Test;\n\n");

        out.append("import org.springframework.beans.factory.annotation.Autowired;\n");
        out.append("import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;\n");
        out.append("import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;\n");
        out.append("import org.springframework.boot.test.mock.mockito.MockBean;\n");
        out.append("import org.springframework.http.MediaType;\n");
        out.append("import org.springframework.test.web.servlet.MockMvc;\n");
        out.append("import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;\n");
        out.append("import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;\n\n");

        out.append("import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;\n\n");

        String testClassName = info.typeName() + "Test";

        out.append("@WebMvcTest(controllers = ").append(info.typeName()).append(".class)\n");
        // 보안 필터가 있는 프로젝트에서도 최소한 테스트가 뜰 수 있도록 기본값으로 필터를 끕니다.
        // (사용자가 실제 보안 동작까지 검증하고 싶다면 추후 옵션으로 조절)
        out.append("@AutoConfigureMockMvc(addFilters = false)\n");
        out.append("class ").append(testClassName).append(" {\n\n");

        out.append("    @Autowired\n");
        out.append("    private MockMvc mvc;\n\n");

        // 컨트롤러가 주입 받는 의존성이 있으면, WebMvcTest 컨텍스트가 뜨도록 MockBean으로 등록합니다.
        var deps = info.injectedDependencyTypeNames();
        if (deps != null) {
            for (String depType : deps) {
                if (depType == null || depType.isBlank()) {
                    continue;
                }
                out.append("    @MockBean\n");
                out.append("    private ").append(depType).append(" ").append(toFieldName(depType)).append(";\n\n");
            }
        }

        var endpoints = info.endpoints();
        if (endpoints == null || endpoints.isEmpty()) {
            out.append("    @Test\n");
            out.append("    void placeholder() {\n");
            out.append("        // no endpoint mappings found by parser\n");
            out.append("        // (generated placeholder)\n");
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
                out.append("        String path = ControllerTestSupport.fillPathVariables(\"")
                        .append(escapeJavaString(path))
                        .append("\");\n");
                out.append("        MockHttpServletRequestBuilder req = MockMvcRequestBuilders.")
                        .append(toMockMvcBuilderMethod(httpMethod))
                        .append("(path)\n")
                        .append("                .accept(MediaType.APPLICATION_JSON)");

                String hm = httpMethod == null ? "GET" : httpMethod.toUpperCase();
                if (hm.equals("POST") || hm.equals("PUT") || hm.equals("PATCH")) {
                    out.append("\n                .contentType(MediaType.APPLICATION_JSON)");
                    String body = buildJsonBodyFor(ep.requestBodyType(), dtoIndex);
                    out.append("\n                .content(\"").append(escapeJavaString(body)).append("\")");
                }

                out.append(";\n");
                out.append("        mvc.perform(req)\n");
                out.append("                .andExpect(status().is2xxSuccessful())");

                // JSON 응답 타입을 추정할 수 있으면 최소한의 구조 검증을 추가합니다.
                DtoIndex.DtoInfo dto = dtoIndex == null ? null : dtoIndex.findBySimpleName(ep.responseBodyType());
                if (dto != null && dto.fields() != null && !dto.fields().isEmpty()) {
                    out.append("\n                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))");
                    for (var f : dto.fields()) {
                        if (f == null || f.name() == null || f.name().isBlank()) {
                            continue;
                        }
                        out.append("\n                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath(\"$.")
                                .append(escapeJavaString(f.name()))
                                .append("\").exists())");
                    }
                }

                out.append(";\n");
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

    private static String toFieldName(String simpleTypeName) {
        if (simpleTypeName == null || simpleTypeName.isBlank()) {
            return "dep";
        }
        String s = simpleTypeName.trim();
        // FooService -> fooService
        return Character.toLowerCase(s.charAt(0)) + s.substring(1);
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

    private static String buildJsonBodyFor(String requestBodyType, DtoIndex dtoIndex) {
        if (requestBodyType == null || requestBodyType.isBlank()) {
            return "{}";
        }
        if (dtoIndex == null) {
            return "{}";
        }

        DtoIndex.DtoInfo dto = dtoIndex.findBySimpleName(requestBodyType);
        if (dto == null || dto.fields() == null || dto.fields().isEmpty()) {
            return "{}";
        }

        StringBuilder json = new StringBuilder();
        json.append("{");
        boolean first = true;
        for (DtoIndex.DtoField f : dto.fields()) {
            if (f == null || f.name() == null || f.name().isBlank()) {
                continue;
            }
            if (!first) {
                json.append(',');
            }
            first = false;
            json.append('"').append(escapeJson(f.name())).append('"').append(':');
            json.append(defaultJsonValueForType(f.type()));
        }
        json.append('}');
        return json.toString();
    }

    private static String defaultJsonValueForType(String javaType) {
        if (javaType == null) {
            return "null";
        }
        String t = javaType.trim();

        // 제네릭 제거
        int gen = t.indexOf('<');
        if (gen >= 0) {
            t = t.substring(0, gen).trim();
        }
        int dot = t.lastIndexOf('.');
        if (dot >= 0 && dot + 1 < t.length()) {
            t = t.substring(dot + 1);
        }

        return switch (t) {
            case "String", "CharSequence" -> "\"\"";
            case "boolean", "Boolean" -> "false";
            case "byte", "short", "int", "long", "float", "double",
                 "Byte", "Short", "Integer", "Long", "Float", "Double" -> "0";
            default -> {
                // 컬렉션/배열처럼 보이면 빈 배열, 그 외는 null
                if (t.endsWith("[]") || t.equals("List") || t.equals("Set") || t.equals("Collection")) {
                    yield "[]";
                }
                yield "null";
            }
        };
    }

    private static String escapeJson(String s) {
        if (s == null) {
            return "";
        }
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
