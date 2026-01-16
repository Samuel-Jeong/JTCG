package com.jtcg.generated.support;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * 생성된 Service 테스트에서 사용하는 리플렉션 유틸.
 *
 * <p>기본 정책:
 * <ul>
 *   <li>메서드는 이름 + 파라미터 개수로 찾습니다(정규식 파서의 한계 때문에 타입까지는 모름).</li>
 *   <li>인스턴스 메서드면 기본 생성자(무인자)로 인스턴스 생성을 시도합니다.</li>
 *   <li>파라미터는 타입별로 "안전한 기본값"을 생성합니다(컬렉션은 empty, Optional은 empty 등).</li>
 *   <li>객체 파라미터를 만들 수 없으면 해당 테스트를 실패시키지 않고 skip 할 수 있도록 사유를 반환합니다.</li>
 * </ul>
 */
public final class ReflectionTestSupport {

    private ReflectionTestSupport() {
    }

    /**
     * 호출 준비 결과.
     *
     * <p>{@code skipReason}이 null이 아니면, 호출은 안전하게 스킵하는 것을 권장합니다.
     */
    public static final class InvocationPlan {
        public final Object target;
        public final Object[] args;
        public final String skipReason;

        private InvocationPlan(Object target, Object[] args, String skipReason) {
            this.target = target;
            this.args = args;
            this.skipReason = skipReason;
        }

        public static InvocationPlan ready(Object target, Object[] args) {
            return new InvocationPlan(target, args, null);
        }

        public static InvocationPlan skip(String reason) {
            return new InvocationPlan(null, new Object[0], reason);
        }
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

    /**
     * 메서드 호출을 위해 target/args를 준비합니다.
     */
    public static InvocationPlan planInvocation(Method method, Class<?> declaringType) {
        if (method == null) {
            return InvocationPlan.skip("method is null");
        }

        Object target = null;
        if (!Modifier.isStatic(method.getModifiers())) {
            target = tryCreate(declaringType);
            if (target == null) {
                return InvocationPlan.skip("No default constructor: " + declaringType.getSimpleName());
            }
        }

        Class<?>[] types = method.getParameterTypes();
        Object[] args = new Object[types.length];

        IdentityHashMap<Class<?>, Boolean> visiting = new IdentityHashMap<>();
        Deque<Class<?>> path = new ArrayDeque<>();
        for (int i = 0; i < types.length; i++) {
            Object v = createValue(types[i], 2, visiting, path);
            if (v == UNCREATABLE && !types[i].isPrimitive()) {
                return InvocationPlan.skip("Unable to create argument for type: " + types[i].getName());
            }
            args[i] = (v == UNCREATABLE) ? defaultValue(types[i]) : v;
        }

        return InvocationPlan.ready(target, args);
    }

    public static Object invoke(Method method, Object target, Object[] args) throws Exception {
        method.setAccessible(true);
        return method.invoke(target, args);
    }

    /**
     * 이전 버전 호환용: 모든 파라미터를 defaultValue로 채워 호출합니다.
     */
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

    private static final Object UNCREATABLE = new Object();

    private static Object createValue(Class<?> type, int depth, IdentityHashMap<Class<?>, Boolean> visiting, Deque<Class<?>> path) {
        if (type == null) {
            return UNCREATABLE;
        }
        if (type.isPrimitive()) {
            return defaultValue(type);
        }
        if (type == String.class) return "";
        if (type == Boolean.class) return false;
        if (Number.class.isAssignableFrom(type)) {
            if (type == Integer.class) return 0;
            if (type == Long.class) return 0L;
            if (type == Short.class) return (short) 0;
            if (type == Byte.class) return (byte) 0;
            if (type == Double.class) return 0.0d;
            if (type == Float.class) return 0.0f;
            if (type == BigDecimal.class) return BigDecimal.ZERO;
        }
        if (type == UUID.class) return new UUID(0L, 0L);
        if (type == Instant.class) return Instant.EPOCH;
        if (type == LocalDate.class) return LocalDate.of(1970, 1, 1);
        if (type == LocalTime.class) return LocalTime.MIDNIGHT;
        if (type == LocalDateTime.class) return LocalDateTime.of(1970, 1, 1, 0, 0);

        if (type.isEnum()) {
            Object[] c = type.getEnumConstants();
            return (c != null && c.length > 0) ? c[0] : UNCREATABLE;
        }

        if (type.isArray()) {
            return Array.newInstance(type.getComponentType(), 0);
        }

        if (Optional.class.isAssignableFrom(type)) {
            return Optional.empty();
        }
        if (List.class.isAssignableFrom(type)) {
            return List.of();
        }
        if (Set.class.isAssignableFrom(type)) {
            return Set.of();
        }
        if (Collection.class.isAssignableFrom(type)) {
            return List.of();
        }
        if (Map.class.isAssignableFrom(type)) {
            return Map.of();
        }

        if (depth <= 0) {
            return UNCREATABLE;
        }

        if (visiting.containsKey(type)) {
            return UNCREATABLE;
        }
        visiting.put(type, Boolean.TRUE);
        path.push(type);
        try {
            Object v = tryCreate(type);
            if (v != null) {
                return v;
            }

            // 기본 생성자가 없으면, 파라미터가 가장 적은 생성자를 선택해 재귀적으로 인자 생성
            Constructor<?> best = null;
            for (Constructor<?> c : type.getDeclaredConstructors()) {
                if (best == null || c.getParameterCount() < best.getParameterCount()) {
                    best = c;
                }
            }
            if (best == null) {
                return UNCREATABLE;
            }

            Class<?>[] pts = best.getParameterTypes();
            Object[] args = new Object[pts.length];
            for (int i = 0; i < pts.length; i++) {
                Object a = createValue(pts[i], depth - 1, visiting, path);
                if (a == UNCREATABLE && !pts[i].isPrimitive()) {
                    return UNCREATABLE;
                }
                args[i] = (a == UNCREATABLE) ? defaultValue(pts[i]) : a;
            }

            best.setAccessible(true);
            return best.newInstance(args);
        } catch (Exception e) {
            return UNCREATABLE;
        } finally {
            path.pop();
            visiting.remove(type);
        }
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
