package com.jtcg.cli;

import com.jtcg.core.GenerationOptions;
import com.jtcg.core.TestCodeGenerator;

import java.nio.file.Path;

/**
 * JTCG의 CLI 엔트리포인트.
 *
 * <p>절대 경로로 주어진 입력 디렉터리를 재귀적으로 스캔하여 JUnit5 테스트 스켈레톤을 생성합니다.
 * 실제 로직은 {@link com.jtcg.core.TestCodeGenerator}에 있으며, 이 클래스는
 * CLI 인자 파싱 및 실행 흐름만 담당합니다.
 */
public final class TestCodeGeneratorApp {

    /**
     * CLI 실행 진입점.
     *
     * <p>사용 예:
     * <pre>
     * java -jar build/libs/jtcg.jar --input /abs/path --output /abs/out --overwrite
     * </pre>
     */
    public static void main(String[] args) {
        CliArgs parsed = CliArgs.parse(args);

        GenerationOptions options = new GenerationOptions(
                Path.of(parsed.inputDir),
                Path.of(parsed.outputDir),
                parsed.overwrite,
                parsed.classpath
        );

        int generated = new TestCodeGenerator().generate(options);
        System.out.println("Generated " + generated + " test file(s).");
    }
}
