package com.jtcg.parse;

import java.util.List;

/**
 * 단일 `.java` 소스 파일에서 추출한 최소 정보.
 *
 * <p>현재 구현은 정규식 기반의 매우 단순한 파서({@link JavaSourceParser})를 사용하므로,
 * 여기 들어있는 값들은 "추정"에 가깝습니다. (예: 여러 타입이 한 파일에 있을 때 첫 타입만 잡힘)
 *
 * @param packageName 패키지명(없으면 null)
 * @param typeName 파일에서 찾은 첫 번째 타입명(클래스/인터페이스/열거형)
 * @param componentType @Controller/@Service 등 스테레오타입 분류(해당 없으면 OTHER)
 * @param publicMethods public 메서드 정보 목록(정규식 기반 추출)
 * @param endpoints 컨트롤러 매핑에서 추출한 엔드포인트 목록(컨트롤러가 아니면 빈 리스트)
 */
public record JavaSourceInfo(
        String packageName,
        String typeName,
        JavaComponentType componentType,
        List<JavaMethodInfo> publicMethods,
        List<JavaEndpointInfo> endpoints
) {
}
