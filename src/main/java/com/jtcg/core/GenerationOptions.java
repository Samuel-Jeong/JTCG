package com.jtcg.core;

import java.nio.file.Path;

/**
 * 테스트 코드 생성에 필요한 입력값 묶음.
 *
 * @param inputDir   스캔할 루트 디렉터리(재귀)
 * @param outputDir  생성된 테스트 파일을 쓸 루트 디렉터리
 * @param overwrite  동일 경로에 파일이 이미 존재할 때 덮어쓸지 여부
 * @param classpath  (선택) 대상 프로젝트의 클래스패스(예: build/classes + 의존 JAR). 제공되면 타입 해석/샘플 생성 정확도를 올릴 수 있습니다.
 */
public record GenerationOptions(Path inputDir, Path outputDir, boolean overwrite, String classpath) {
}
