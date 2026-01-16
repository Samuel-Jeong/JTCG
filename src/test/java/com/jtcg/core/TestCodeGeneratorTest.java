package com.jtcg.core;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class TestCodeGeneratorTest {

    @Test
    void generatesTestFileForSimpleJavaClass() throws Exception {
        Path input = Files.createTempDirectory("jtcg-in");
        Path output = Files.createTempDirectory("jtcg-out");

        Path srcDir = input.resolve("src").resolve("main").resolve("java").resolve("com").resolve("example");
        Files.createDirectories(srcDir);
        Path controllerFile = srcDir.resolve("HelloController.java");
        Path serviceFile = srcDir.resolve("CalcService.java");
        Path otherFile = srcDir.resolve("Plain.java");

        String controllerCode = """
                package com.example;

                import org.springframework.web.bind.annotation.GetMapping;
                import org.springframework.web.bind.annotation.PostMapping;
                import org.springframework.web.bind.annotation.PathVariable;
                import org.springframework.web.bind.annotation.RestController;

                @RestController
                public class HelloController {
                    @GetMapping("/hello")
                    public String hello() {
                        return "ok";
                    }

                    @PostMapping("/items/{id}")
                    public String create(@PathVariable("id") String id) {
                        return id;
                    }
                }
                """;

        String serviceCode = """
                package com.example;

                import org.springframework.stereotype.Service;

                @Service
                public class CalcService {
                    public int add(int a, int b) {
                        return a + b;
                    }
                }
                """;

        String otherCode = """
                package com.example;

                public class Plain {
                    public void noop() {
                    }
                }
                """;

        Files.writeString(controllerFile, controllerCode, StandardCharsets.UTF_8);
        Files.writeString(serviceFile, serviceCode, StandardCharsets.UTF_8);
        Files.writeString(otherFile, otherCode, StandardCharsets.UTF_8);

        GenerationOptions options = new GenerationOptions(input, output, true);
        int generated = new TestCodeGenerator().generate(options);

        assertTrue(generated >= 2);

        // 공통 유틸은 출력 루트에 1회 생성되어야 합니다.
        Path reflectionSupport = output.resolve("com").resolve("jtcg").resolve("generated").resolve("support")
                .resolve("ReflectionTestSupport.java");
        Path controllerSupport = output.resolve("com").resolve("jtcg").resolve("generated").resolve("support")
                .resolve("ControllerTestSupport.java");
        assertTrue(Files.exists(reflectionSupport));
        assertTrue(Files.exists(controllerSupport));

        Path controllerTest = output.resolve("com").resolve("example").resolve("HelloControllerTest.java");
        Path serviceTest = output.resolve("com").resolve("example").resolve("CalcServiceTest.java");
        Path otherTest = output.resolve("com").resolve("example").resolve("PlainTest.java");

        assertTrue(Files.exists(controllerTest));
        assertTrue(Files.exists(serviceTest));
        assertTrue(!Files.exists(otherTest));

        String controllerGenerated = Files.readString(controllerTest, StandardCharsets.UTF_8);
        assertTrue(controllerGenerated.contains("ControllerTestSupport"));
        assertTrue(controllerGenerated.contains("MediaType.APPLICATION_JSON"));
        assertTrue(controllerGenerated.contains("/hello"));
        assertTrue(controllerGenerated.contains("/items/{id}"));
        assertTrue(controllerGenerated.contains("fillPathVariables"));
        assertTrue(controllerGenerated.contains("content(\"{}\")"));

        String serviceGenerated = Files.readString(serviceTest, StandardCharsets.UTF_8);
        assertTrue(serviceGenerated.contains("ReflectionTestSupport"));
        assertTrue(serviceGenerated.contains("assertDoesNotThrow"));
        assertTrue(serviceGenerated.contains("void test_add__2params"));
    }
}
