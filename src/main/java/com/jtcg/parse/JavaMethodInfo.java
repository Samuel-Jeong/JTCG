package com.jtcg.parse;

/**
 * 소스에서 추출한 단일 public 메서드 정보.
 *
 * <p>현재 프로젝트는 정규식 기반 파싱이므로, 여기의 값은 Java AST 수준의 정확도를 보장하지 않습니다.
 * 그래도 테스트 생성에서 "어떤 메서드에 대해 테스트를 만들지"를 결정하는 데 필요한 최소 정보를 담습니다.
 *
 * @param name       메서드명
 * @param paramCount 파라미터 개수(쉼표 기준 단순 계산)
 */
public record JavaMethodInfo(String name, int paramCount) {
}
