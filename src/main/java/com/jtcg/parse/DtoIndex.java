package com.jtcg.parse;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.RecordDeclaration;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 입력 소스 트리에서 DTO(간단한 POJO/record)의 필드 정보를 전수 조사해 보관하는 인덱스.
 *
 * <p>정확한 타입 해석(외부 의존성 포함)은 {@code --classpath}가 있을 때 더 잘할 수 있지만,
 * 이 클래스는 기본 모드(소스-온리)에서 "응답 JSON에 어떤 필드가 있을지"를 추정해
 * Controller 테스트에 최소한의 {@code jsonPath(...).exists()} 검증을 자동 생성하는 용도로 사용합니다.
 */
public final class DtoIndex {

    private final Map<String, DtoInfo> bySimpleName;

    private DtoIndex(Map<String, DtoInfo> bySimpleName) {
        this.bySimpleName = bySimpleName;
    }

    /**
     * 입력 디렉터리 하위의 모든 .java 파일을 파싱해서 DTO 필드 정보를 인덱싱합니다.
     */
    public static DtoIndex build(Path inputDir, List<Path> javaFiles) {
        Map<String, DtoInfo> out = new HashMap<>();
        if (javaFiles == null) {
            return new DtoIndex(out);
        }

        for (Path f : javaFiles) {
            if (f == null || !f.getFileName().toString().endsWith(".java")) {
                continue;
            }
            try {
                String src = Files.readString(f, StandardCharsets.UTF_8);
                CompilationUnit cu = StaticJavaParser.parse(src);

                cu.findAll(RecordDeclaration.class).forEach(rd -> {
                    String name = rd.getNameAsString();
                    List<DtoField> fields = new ArrayList<>();
                    rd.getParameters().forEach(p -> fields.add(new DtoField(p.getNameAsString(), p.getType().toString())));
                    if (!fields.isEmpty()) {
                        out.putIfAbsent(name, new DtoInfo(name, List.copyOf(fields)));
                    }
                });

                cu.findAll(ClassOrInterfaceDeclaration.class).forEach(cd -> {
                    if (cd.isInterface() || cd.isAbstract()) {
                        return;
                    }
                    String name = cd.getNameAsString();
                    List<DtoField> fields = new ArrayList<>();
                    for (FieldDeclaration fd : cd.getFields()) {
                        // static 필드 등은 JSON 바디에 보통 안 들어가므로 제외(단순 규칙)
                        if (fd.isStatic()) {
                            continue;
                        }
                        fd.getVariables().forEach(v -> fields.add(new DtoField(v.getNameAsString(), v.getType().toString())));
                    }
                    if (!fields.isEmpty()) {
                        out.putIfAbsent(name, new DtoInfo(name, List.copyOf(fields)));
                    }
                });
            } catch (IOException ignored) {
                // 인덱싱 실패는 테스트 생성을 막지 않습니다.
            } catch (RuntimeException ignored) {
                // JavaParser가 파싱 실패할 수 있으므로 무시
            }
        }

        return new DtoIndex(out);
    }

    public DtoInfo findBySimpleName(String simpleName) {
        if (simpleName == null) {
            return null;
        }
        String key = stripGenerics(simpleName.trim());
        int dot = key.lastIndexOf('.');
        if (dot >= 0 && dot + 1 < key.length()) {
            key = key.substring(dot + 1);
        }
        return bySimpleName.get(key);
    }

    private static String stripGenerics(String t) {
        int i = t.indexOf('<');
        if (i >= 0) {
            return t.substring(0, i).trim();
        }
        return t;
    }

    public record DtoInfo(String simpleName, List<DtoField> fields) {
    }

    public record DtoField(String name, String type) {
    }
}
