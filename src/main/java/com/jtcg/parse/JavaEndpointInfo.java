package com.jtcg.parse;

/**
 * 컨트롤러에서 추출한 단일 API 엔드포인트 정보.
 *
 * <p>정규식/라인 기반 추출이므로 Java AST 수준의 정확도를 보장하지 않습니다.
 * 테스트 생성에서 "어떤 경로에 어떤 HTTP 메서드로 요청을 날릴지"를 결정하는 최소 정보만 담습니다.
 *
 * @param httpMethod  HTTP 메서드(예: GET/POST)
 * @param fullPath    클래스/메서드 레벨 매핑을 합친 최종 경로(예: /api/foo)
 * @param javaMethodName  자바 메서드명(디버깅용/테스트 메서드명 구성용)
 * @param paramCount  메서드 파라미터 개수(테스트 메서드명 안정화를 위한 값)
 */
public record JavaEndpointInfo(
        String httpMethod,
        String fullPath,
        String javaMethodName,
        int paramCount
) {
}
