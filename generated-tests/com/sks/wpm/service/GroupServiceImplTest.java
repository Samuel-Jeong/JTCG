package com.sks.wpm.service;

import com.jtcg.generated.support.ReflectionTestSupport;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class GroupServiceImplTest {

    @Test
    void test_getGroupList__3params() throws Exception {
        Method method = ReflectionTestSupport.findMethod(GroupServiceImpl.class, "getGroupList", 3);
        Assumptions.assumeTrue(method != null, "Method not found by reflection: getGroupList");
        Object target = ReflectionTestSupport.targetOrNullFor(method, GroupServiceImpl.class);
        if (!java.lang.reflect.Modifier.isStatic(method.getModifiers())) {
            Assumptions.assumeTrue(target != null, "No default constructor: GroupServiceImpl");
        }

        Object result = assertDoesNotThrow(() -> ReflectionTestSupport.invokeWithDefaults(method, target));
        if (method.getReturnType() != void.class && !method.getReturnType().isPrimitive()) {
            assertNotNull(result);
        }
    }

    @Test
    void test_getGroupInfo__3params() throws Exception {
        Method method = ReflectionTestSupport.findMethod(GroupServiceImpl.class, "getGroupInfo", 3);
        Assumptions.assumeTrue(method != null, "Method not found by reflection: getGroupInfo");
        Object target = ReflectionTestSupport.targetOrNullFor(method, GroupServiceImpl.class);
        if (!java.lang.reflect.Modifier.isStatic(method.getModifiers())) {
            Assumptions.assumeTrue(target != null, "No default constructor: GroupServiceImpl");
        }

        Object result = assertDoesNotThrow(() -> ReflectionTestSupport.invokeWithDefaults(method, target));
        if (method.getReturnType() != void.class && !method.getReturnType().isPrimitive()) {
            assertNotNull(result);
        }
    }

    @Test
    void test_addGroup__3params() throws Exception {
        Method method = ReflectionTestSupport.findMethod(GroupServiceImpl.class, "addGroup", 3);
        Assumptions.assumeTrue(method != null, "Method not found by reflection: addGroup");
        Object target = ReflectionTestSupport.targetOrNullFor(method, GroupServiceImpl.class);
        if (!java.lang.reflect.Modifier.isStatic(method.getModifiers())) {
            Assumptions.assumeTrue(target != null, "No default constructor: GroupServiceImpl");
        }

        Object result = assertDoesNotThrow(() -> ReflectionTestSupport.invokeWithDefaults(method, target));
        if (method.getReturnType() != void.class && !method.getReturnType().isPrimitive()) {
            assertNotNull(result);
        }
    }

    @Test
    void test_modGroup__4params() throws Exception {
        Method method = ReflectionTestSupport.findMethod(GroupServiceImpl.class, "modGroup", 4);
        Assumptions.assumeTrue(method != null, "Method not found by reflection: modGroup");
        Object target = ReflectionTestSupport.targetOrNullFor(method, GroupServiceImpl.class);
        if (!java.lang.reflect.Modifier.isStatic(method.getModifiers())) {
            Assumptions.assumeTrue(target != null, "No default constructor: GroupServiceImpl");
        }

        Object result = assertDoesNotThrow(() -> ReflectionTestSupport.invokeWithDefaults(method, target));
        if (method.getReturnType() != void.class && !method.getReturnType().isPrimitive()) {
            assertNotNull(result);
        }
    }

    @Test
    void test_deleteGroup__3params() throws Exception {
        Method method = ReflectionTestSupport.findMethod(GroupServiceImpl.class, "deleteGroup", 3);
        Assumptions.assumeTrue(method != null, "Method not found by reflection: deleteGroup");
        Object target = ReflectionTestSupport.targetOrNullFor(method, GroupServiceImpl.class);
        if (!java.lang.reflect.Modifier.isStatic(method.getModifiers())) {
            Assumptions.assumeTrue(target != null, "No default constructor: GroupServiceImpl");
        }

        Object result = assertDoesNotThrow(() -> ReflectionTestSupport.invokeWithDefaults(method, target));
        if (method.getReturnType() != void.class && !method.getReturnType().isPrimitive()) {
            assertNotNull(result);
        }
    }

    @Test
    void test_addDeviceToGroup__4params() throws Exception {
        Method method = ReflectionTestSupport.findMethod(GroupServiceImpl.class, "addDeviceToGroup", 4);
        Assumptions.assumeTrue(method != null, "Method not found by reflection: addDeviceToGroup");
        Object target = ReflectionTestSupport.targetOrNullFor(method, GroupServiceImpl.class);
        if (!java.lang.reflect.Modifier.isStatic(method.getModifiers())) {
            Assumptions.assumeTrue(target != null, "No default constructor: GroupServiceImpl");
        }

        Object result = assertDoesNotThrow(() -> ReflectionTestSupport.invokeWithDefaults(method, target));
        if (method.getReturnType() != void.class && !method.getReturnType().isPrimitive()) {
            assertNotNull(result);
        }
    }

    @Test
    void test_removeDeviceFromGroup__4params() throws Exception {
        Method method = ReflectionTestSupport.findMethod(GroupServiceImpl.class, "removeDeviceFromGroup", 4);
        Assumptions.assumeTrue(method != null, "Method not found by reflection: removeDeviceFromGroup");
        Object target = ReflectionTestSupport.targetOrNullFor(method, GroupServiceImpl.class);
        if (!java.lang.reflect.Modifier.isStatic(method.getModifiers())) {
            Assumptions.assumeTrue(target != null, "No default constructor: GroupServiceImpl");
        }

        Object result = assertDoesNotThrow(() -> ReflectionTestSupport.invokeWithDefaults(method, target));
        if (method.getReturnType() != void.class && !method.getReturnType().isPrimitive()) {
            assertNotNull(result);
        }
    }

}
