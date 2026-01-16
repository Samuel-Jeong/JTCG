package com.jtcg.parse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * `.java` 소스 파일에서 테스트 생성에 필요한 최소 정보만 추출하는 파서.
 *
 * <p>정식 AST 파서가 아니라 정규식 기반으로 동작합니다. 따라서 아래와 같은 제약이 있습니다.
 * <ul>
 *   <li>여러 타입이 한 파일에 있으면 "첫 번째로 매칭되는" 타입만 사용</li>
 *   <li>메서드 추출은 단순 패턴이므로, 복잡한 선언/어노테이션/라인 브레이크 형태에 따라 누락될 수 있음</li>
 *   <li>생성자는 반환 타입이 없어서 보통 매칭되지 않지만, 예외 케이스를 위해 타입명과 동일한 메서드명은 제외</li>
 * </ul>
 */
public final class JavaSourceParser {
    private static final Pattern PACKAGE = Pattern.compile("^\\s*package\\s+([a-zA-Z0-9_$.]+)\\s*;", Pattern.MULTILINE);
    private static final Pattern TYPE = Pattern.compile("^\\s*(?:public\\s+)?(?:final\\s+|abstract\\s+)?(class|interface|enum)\\s+([A-Za-z_][A-Za-z0-9_]*)\\b", Pattern.MULTILINE);

    // public 메서드 "시작"만 잡는 매우 단순한 패턴.
    //
    // 주의: 파라미터에 @PathVariable("id") 처럼 괄호가 포함되면, 정규식만으로는 올바른 닫는 ')'를 찾기 어렵습니다.
    // 그래서 이 패턴은 '(' 까지만 매칭하고, 실제 파라미터 문자열 추출은 별도 스캔(괄호 매칭)으로 처리합니다.
    // group(1) = methodName
    private static final Pattern PUBLIC_METHOD_START = Pattern.compile(
            "^\\s*public\\s+(?:static\\s+)?[^=;{}]+?\\s+([A-Za-z_][A-Za-z0-9_]*)\\s*\\(",
            Pattern.MULTILINE
    );

    private static final Pattern FIRST_STRING_LITERAL = Pattern.compile("\"([^\"]*)\"");
    private static final Pattern REQUEST_METHOD = Pattern.compile("RequestMethod\\.(GET|POST|PUT|DELETE|PATCH)");

    /**
     * 파일을 UTF-8로 읽고, 패키지/타입명/공개 메서드명을 추출합니다.
     */
    public JavaSourceInfo parse(Path javaFile) throws IOException {
        String src = Files.readString(javaFile, StandardCharsets.UTF_8);

        String pkg = null;
        Matcher pm = PACKAGE.matcher(src);
        if (pm.find()) {
            pkg = pm.group(1);
        }

        String typeName = null;
        int typeStart = -1;
        Matcher tm = TYPE.matcher(src);
        if (tm.find()) {
            typeName = tm.group(2);
            typeStart = tm.start();
        }

        JavaComponentType componentType = JavaComponentType.OTHER;
        String controllerBasePath = null;
        if (typeName != null) {
            // 타입 선언 직전의 어노테이션 블록에서 @Controller/@RestController/@Service 등을 찾습니다.
            String header = src.substring(0, Math.max(0, typeStart));
            String[] lines = header.split("\\R", -1);

            // 마지막으로 등장한 타입 선언 직전 라인들만(거의 대부분 어노테이션이 몰려있음) 확인
            int from = Math.max(0, lines.length - 30);
            for (int i = from; i < lines.length; i++) {
                String line = lines[i].trim();
                if (!line.startsWith("@")) {
                    continue;
                }

                if (isControllerAnnotation(line)) {
                    componentType = JavaComponentType.CONTROLLER;
                } else if (isServiceAnnotation(line)) {
                    // @Controller가 이미 잡혔다면 컨트롤러 우선
                    if (componentType != JavaComponentType.CONTROLLER) {
                        componentType = JavaComponentType.SERVICE;
                    }
                }

                if (isRequestMappingAnnotation(line)) {
                    String p = extractFirstPath(line);
                    if (p != null) {
                        controllerBasePath = p;
                    }
                }
            }
        }

        List<JavaMethodInfo> publicMethods = new ArrayList<>();
        List<JavaEndpointInfo> endpoints = new ArrayList<>();
        if (typeName != null) {
            Matcher mm = PUBLIC_METHOD_START.matcher(src);
            while (mm.find()) {
                String methodName = mm.group(1);
                if (methodName.equals(typeName)) {
                    continue; // 생성자처럼 보이는 케이스 제외
                }

                int openParenIndex = mm.end() - 1; // '(' 위치
                int closeParenIndex = findMatchingParen(src, openParenIndex);
                if (closeParenIndex < 0) {
                    continue;
                }

                // 파라미터 문자열(바깥 괄호 내부)
                String paramList = src.substring(openParenIndex + 1, closeParenIndex);
                int paramCount = countParams(paramList);

                // 메서드 선언으로 볼 수 있는지 간단 확인: 파라미터 닫는 괄호 이후에 '{' 또는 'throws ... {' 패턴이 있어야 함
                // (인터페이스 메서드/추상 메서드의 ';' 등은 제외)
                if (!looksLikeMethodBodyStartsAfter(src, closeParenIndex + 1)) {
                    continue;
                }

                publicMethods.add(new JavaMethodInfo(methodName, paramCount));

                if (componentType == JavaComponentType.CONTROLLER) {
                    int lineIndex = lineIndexAt(src, mm.start());
                    EndpointAnn ann = findEndpointAnnotationNear(src, lineIndex);
                    if (ann != null) {
                        String fullPath = joinPaths(controllerBasePath, ann.path);
                        endpoints.add(new JavaEndpointInfo(ann.httpMethod, fullPath, methodName, paramCount));
                    }
                }
            }
        }

        return new JavaSourceInfo(
                pkg,
                typeName,
                componentType,
                List.copyOf(publicMethods),
                List.copyOf(endpoints)
        );
    }

    private static boolean isControllerAnnotation(String line) {
        // @Controller, @RestController, 또는 fully-qualified
        return line.startsWith("@Controller")
                || line.startsWith("@RestController")
                || line.startsWith("@org.springframework.web.bind.annotation.RestController")
                || line.startsWith("@org.springframework.stereotype.Controller");
    }

    private static boolean isServiceAnnotation(String line) {
        return line.startsWith("@Service") || line.startsWith("@org.springframework.stereotype.Service");
    }

    private static boolean isRequestMappingAnnotation(String line) {
        return line.startsWith("@RequestMapping") || line.startsWith("@org.springframework.web.bind.annotation.RequestMapping");
    }

    private static String extractFirstPath(String annotationLine) {
        if (annotationLine == null) {
            return null;
        }
        Matcher m = FIRST_STRING_LITERAL.matcher(annotationLine);
        if (!m.find()) {
            return null;
        }
        return m.group(1);
    }

    private static String joinPaths(String base, String path) {
        String b = base == null ? "" : base.trim();
        String p = path == null ? "" : path.trim();
        if (b.isEmpty()) {
            b = "";
        }
        if (p.isEmpty()) {
            p = "";
        }

        String merged = (b + "/" + p).replaceAll("/+", "/");
        if (!merged.startsWith("/")) {
            merged = "/" + merged;
        }
        // "/" 단독이면 그대로 유지
        if (merged.length() > 1 && merged.endsWith("/")) {
            merged = merged.substring(0, merged.length() - 1);
        }
        return merged;
    }

    private static int lineIndexAt(String src, int charIndex) {
        int line = 0;
        for (int i = 0; i < Math.min(charIndex, src.length()); i++) {
            if (src.charAt(i) == '\n') {
                line++;
            }
        }
        return line;
    }

    private record EndpointAnn(String httpMethod, String path) {
    }

    private static EndpointAnn findEndpointAnnotationNear(String src, int methodLineIndex) {
        // 메서드 선언 바로 위쪽 몇 줄만 검사(정규식 기반 도구이므로 단순 규칙)
        String[] lines = src.split("\\R", -1);
        int from = Math.max(0, methodLineIndex - 8);
        for (int i = methodLineIndex - 1; i >= from; i--) {
            String line = lines[i].trim();
            if (!line.startsWith("@")) {
                continue;
            }

            EndpointAnn ann = parseEndpointAnnotation(line);
            if (ann != null) {
                return ann;
            }
        }
        return null;
    }

    private static EndpointAnn parseEndpointAnnotation(String line) {
        if (line.startsWith("@GetMapping") || line.startsWith("@org.springframework.web.bind.annotation.GetMapping")) {
            return new EndpointAnn("GET", defaultPath(extractFirstPath(line)));
        }
        if (line.startsWith("@PostMapping") || line.startsWith("@org.springframework.web.bind.annotation.PostMapping")) {
            return new EndpointAnn("POST", defaultPath(extractFirstPath(line)));
        }
        if (line.startsWith("@PutMapping") || line.startsWith("@org.springframework.web.bind.annotation.PutMapping")) {
            return new EndpointAnn("PUT", defaultPath(extractFirstPath(line)));
        }
        if (line.startsWith("@DeleteMapping") || line.startsWith("@org.springframework.web.bind.annotation.DeleteMapping")) {
            return new EndpointAnn("DELETE", defaultPath(extractFirstPath(line)));
        }
        if (line.startsWith("@PatchMapping") || line.startsWith("@org.springframework.web.bind.annotation.PatchMapping")) {
            return new EndpointAnn("PATCH", defaultPath(extractFirstPath(line)));
        }
        if (isRequestMappingAnnotation(line)) {
            String method = "GET";
            Matcher rm = REQUEST_METHOD.matcher(line);
            if (rm.find()) {
                method = rm.group(1);
            }
            return new EndpointAnn(method, defaultPath(extractFirstPath(line)));
        }
        return null;
    }

    private static String defaultPath(String p) {
        if (p == null || p.isBlank()) {
            return "/";
        }
        return p;
    }

    /**
     * 파라미터 문자열에서 파라미터 개수를 계산합니다.
     *
     * <p>주의: 제네릭/람다/중첩 타입 등 쉼표가 등장하는 복잡한 타입은 정확히 처리하지 못합니다.
     * 현재 도구의 목적(대략적인 자동 테스트 생성)상 "0개인지, 1개 이상인지" 및 단순 케이스에서의 개수만 유효합니다.
     */
    private static int countParams(String raw) {
        if (raw == null) {
            return 0;
        }
        String s = raw.trim();
        if (s.isEmpty()) {
            return 0;
        }

        // 가장 단순한 카운트: 쉼표로 split
        int count = 1;
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == ',') {
                count++;
            }
        }
        return count;
    }

    /**
     * {@code openIndex} 위치의 '('에 대해 대응되는 ')'의 인덱스를 찾습니다.
     *
     * <p>파라미터에 어노테이션(@PathVariable("id"))처럼 괄호가 포함될 수 있어 단순 정규식으로는 처리하기 어렵습니다.
     */
    private static int findMatchingParen(String src, int openIndex) {
        if (src == null || openIndex < 0 || openIndex >= src.length() || src.charAt(openIndex) != '(') {
            return -1;
        }

        int depth = 0;
        boolean inString = false;
        boolean escaped = false;

        for (int i = openIndex; i < src.length(); i++) {
            char c = src.charAt(i);

            if (inString) {
                if (escaped) {
                    escaped = false;
                    continue;
                }
                if (c == '\\') {
                    escaped = true;
                    continue;
                }
                if (c == '"') {
                    inString = false;
                }
                continue;
            }

            if (c == '"') {
                inString = true;
                continue;
            }

            if (c == '(') {
                depth++;
            } else if (c == ')') {
                depth--;
                if (depth == 0) {
                    return i;
                }
            }
        }

        return -1;
    }

    private static boolean looksLikeMethodBodyStartsAfter(String src, int index) {
        if (src == null) {
            return false;
        }
        int i = index;
        while (i < src.length() && Character.isWhitespace(src.charAt(i))) {
            i++;
        }
        if (i >= src.length()) {
            return false;
        }

        // 바로 '{'면 OK
        if (src.charAt(i) == '{') {
            return true;
        }

        // 'throws ... {' 형태 지원
        if (src.startsWith("throws", i)) {
            int j = i + "throws".length();
            while (j < src.length() && src.charAt(j) != '{' && src.charAt(j) != ';') {
                j++;
            }
            return j < src.length() && src.charAt(j) == '{';
        }

        return false;
    }
}
