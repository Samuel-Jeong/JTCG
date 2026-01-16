package com.jtcg.cli;

import java.util.Objects;

/**
 * CLI 인자 파서.
 *
 * <p>아주 작은 도구이기 때문에 외부 라이브러리 없이 최소한의 규칙만 구현합니다.
 * 잘못된 인자/누락된 값이 있으면 사용법을 출력하고 종료합니다.
 */
final class CliArgs {
    final String inputDir;
    final String outputDir;
    final boolean overwrite;

    private CliArgs(String inputDir, String outputDir, boolean overwrite) {
        this.inputDir = inputDir;
        this.outputDir = outputDir;
        this.overwrite = overwrite;
    }

    /**
     * 커맨드라인 인자를 파싱합니다.
     *
     * <ul>
     *   <li>{@code --input} (필수): 입력 디렉터리</li>
     *   <li>{@code --output} (선택): 출력 디렉터리(기본값: {@code ./generated-tests})</li>
     *   <li>{@code --overwrite} (선택): 기존 파일 덮어쓰기</li>
     * </ul>
     */
    static CliArgs parse(String[] args) {
        String input = null;
        String output = "./generated-tests";
        boolean overwrite = false;

        for (int i = 0; i < args.length; i++) {
            String a = Objects.toString(args[i], "");
            switch (a) {
                case "--input" -> {
                    if (i + 1 >= args.length) {
                        usageAndExit("--input requires a value");
                    }
                    input = args[++i];
                }
                case "--output" -> {
                    if (i + 1 >= args.length) {
                        usageAndExit("--output requires a value");
                    }
                    output = args[++i];
                }
                case "--overwrite" -> overwrite = true;
                case "-h", "--help" -> usageAndExit(null);
                default -> usageAndExit("Unknown argument: " + a);
            }
        }

        if (input == null || input.isBlank()) {
            usageAndExit("--input is required");
        }

        return new CliArgs(input, output, overwrite);
    }

    /**
     * 에러 메시지(선택)를 출력하고 사용법을 표시한 뒤 프로세스를 종료합니다.
     *
     * <p>이 도구는 CLI 단독 실행을 전제로 하므로 예외 전파 대신 {@link System#exit(int)}를 사용합니다.
     */
    private static void usageAndExit(String error) {
        if (error != null) {
            System.err.println("ERROR: " + error);
        }
        System.err.println("Usage: java -jar jtcg.jar --input /abs/path [--output /abs/out] [--overwrite]");
        System.exit(error == null ? 0 : 2);
    }
}
