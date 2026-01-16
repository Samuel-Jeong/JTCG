package com.jtcg.generated.support;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * 생성된 Service 테스트에서 사용하는 리플렉션 유틸.
 *
 * <p>기본 정책:
 * <ul>
 *   <li>메서드는 이름 + 파라미터 개수로 찾습니다(정규식 파서의 한계 때문에 타입까지는 모름).</li>
 *   <li>인스턴스 메서드면 기본 생성자(무인자)로 인스턴스 생성을 시도합니다.</li>
 *   <li>파라미터는 primitive=0/false, String="", 그 외=null 로 채웁니다.</li>
 * </ul>
 */
public final class ReflectionTestSupport {

    private ReflectionTestSupport() {
    }

    public static Method findMethod(Class<?> type, String name, int paramCount) {
        // public 메서드(상속 포함) 우선
        for (Method m : type.getMethods()) {
            if (m.getName().equals(name) && m.getParameterCount() == paramCount) {
                return m;
            }
        }
        // declared 메서드(비공개 포함)
        for (Method m : type.getDeclaredMethods()) {
            if (m.getName().equals(name) && m.getParameterCount() == paramCount) {
                return m;
            }
        }
        return null;
    }

    public static Object tryCreate(Class<?> type) {
        try {
            var ctor = type.getDeclaredConstructor();
            ctor.setAccessible(true);
            return ctor.newInstance();
        } catch (Exception e) {
            return null;
        }
    }

    public static Object invokeWithDefaults(Method method, Object target) throws Exception {
        method.setAccessible(true);
        Class<?>[] types = method.getParameterTypes();
        Object[] args = new Object[types.length];
        for (int i = 0; i < types.length; i++) {
            args[i] = defaultValue(types[i]);
        }
        return method.invoke(target, args);
    }

    public static Object targetOrNullFor(Method method, Class<?> declaringType) {
        if (method == null) {
            return null;
        }
        if (Modifier.isStatic(method.getModifiers())) {
            return null;
        }
        return tryCreate(declaringType);
    }

    private static Object defaultValue(Class<?> type) {
        if (!type.isPrimitive()) {
            if (type == String.class) return "";
            return null;
        }
        if (type == boolean.class) return false;
        if (type == char.class) return (char) 0;
        if (type == byte.class) return (byte) 0;
        if (type == short.class) return (short) 0;
        if (type == int.class) return 0;
        if (type == long.class) return 0L;
        if (type == float.class) return 0.0f;
        if (type == double.class) return 0.0d;
        return 0;
    }
}
