package com.jtcg.core;

import java.nio.file.Path;

/**
 * 테스트 코드 생성에 필요한 입력값 묶음.
 *
 * @param inputDir   스캔할 루트 디렉터리(재귀)
 * @param outputDir  생성된 테스트 파일을 쓸 루트 디렉터리
 * @param overwrite  동일 경로에 파일이 이미 존재할 때 덮어쓸지 여부
 */
public record GenerationOptions(Path inputDir, Path outputDir, boolean overwrite) {
}
