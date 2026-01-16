package com.jtcg.core;

import com.jtcg.parse.JavaSourceInfo;
import com.jtcg.parse.JavaSourceParser;
import com.jtcg.parse.JavaComponentType;
import com.jtcg.parse.DtoIndex;
import com.jtcg.render.GeneratedTestSupportFiles;
import com.jtcg.render.JunitTestRenderer;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

/**
 * 입력 디렉터리 하위의 모든 `.java` 파일을 스캔하여 JUnit5 테스트 스켈레톤을 생성합니다.
 *
 * <p>현재 버전은 Java AST 파서가 아니라 텍스트(정규식) 기반 파서를 사용합니다.
 * 따라서 복잡한 문법(중첩 타입, 여러 타입이 한 파일에 존재, 특이한 메서드 선언 등)에 대해
 * 누락/오탐이 있을 수 있습니다.
 */
public final class TestCodeGenerator {

    /**
     * 테스트 파일 생성 메인 루틴.
     *
     * <p>동작 개요:
     * <ol>
     *   <li>{@code inputDir}를 재귀적으로 순회하며 `.java` 파일 목록을 수집</li>
     *   <li>각 파일을 파싱하여 패키지/타입명/공개 메서드 목록을 추출</li>
     *   <li>테스트 코드 문자열을 렌더링 후, 패키지 경로에 맞춰 출력 디렉터리에 파일 작성</li>
     * </ol>
     *
     * @return 실제로 파일을 쓴(생성/덮어쓰기) 테스트 파일 개수
     * @throws IllegalArgumentException inputDir가 디렉터리가 아닐 때
     */
    public int generate(GenerationOptions options) {
        Path inputDir = options.inputDir().toAbsolutePath().normalize();
        Path outputDir = options.outputDir().toAbsolutePath().normalize();

        if (!Files.isDirectory(inputDir)) {
            throw new IllegalArgumentException("inputDir must be a directory: " + inputDir);
        }

        List<Path> javaFiles = new ArrayList<>();
        try {
            Files.walkFileTree(inputDir, new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                    if (file.getFileName().toString().endsWith(".java")) {
                        javaFiles.add(file);
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            throw new RuntimeException("Failed to scan directory: " + inputDir, e);
        }

        JavaSourceParser parser = new JavaSourceParser();
        JunitTestRenderer renderer = new JunitTestRenderer();

        // 입력 소스 트리에서 DTO(요청/응답)에 해당할 수 있는 타입 정보를 전수 조사합니다.
        // (Controller 테스트의 JSON 바디/응답 jsonPath assert 생성에 사용)
        DtoIndex dtoIndex = DtoIndex.build(inputDir, javaFiles);

        // 생성된 테스트들이 공통으로 사용하는 유틸을 출력 루트에 한 번 생성합니다.
        writeSupportFiles(outputDir, options.overwrite());

        int generated = 0;
        for (Path javaFile : javaFiles) {
            JavaSourceInfo info;
            try {
                info = parser.parse(javaFile);
            } catch (IOException e) {
                throw new RuntimeException("Failed to read: " + javaFile, e);
            }

            if (info.typeName() == null || info.typeName().isBlank()) {
                continue;
            }

            // 이번 버전은 @Controller/@Service만 대상으로 테스트를 생성합니다.
            if (info.componentType() == null || info.componentType() == JavaComponentType.OTHER) {
                continue;
            }

            String testCode = renderer.render(info, dtoIndex);
            Path outFile = outputPathFor(outputDir, info.packageName(), info.typeName() + "Test.java");
            try {
                Files.createDirectories(outFile.getParent());
                if (!options.overwrite() && Files.exists(outFile)) {
                    continue;
                }
                Files.writeString(outFile, testCode, StandardCharsets.UTF_8);
                generated++;
            } catch (IOException e) {
                throw new RuntimeException("Failed to write: " + outFile, e);
            }
        }

        return generated;
    }

    private static void writeSupportFiles(Path outputRoot, boolean overwrite) {
        GeneratedTestSupportFiles support = new GeneratedTestSupportFiles();
        for (var f : support.files()) {
            Path outFile = outputPathFor(outputRoot, f.packageName(), f.fileName());
            try {
                Files.createDirectories(outFile.getParent());
                if (!overwrite && Files.exists(outFile)) {
                    continue;
                }
                Files.writeString(outFile, f.sourceCode(), StandardCharsets.UTF_8);
            } catch (IOException e) {
                throw new RuntimeException("Failed to write support file: " + outFile, e);
            }
        }
    }

    /**
     * 패키지명을 디렉터리 경로로 변환하여 출력 경로를 구성합니다.
     *
     * <p>패키지가 없으면(outputRoot 바로 아래) 파일을 생성합니다.
     */
    private static Path outputPathFor(Path outputRoot, String packageName, String fileName) {
        if (packageName == null || packageName.isBlank()) {
            return outputRoot.resolve(fileName);
        }
        return outputRoot.resolve(packageName.replace('.', '/')).resolve(fileName);
    }
}
