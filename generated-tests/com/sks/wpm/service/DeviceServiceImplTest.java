package com.sks.wpm.service;

import com.jtcg.generated.support.ReflectionTestSupport;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class DeviceServiceImplTest {

    @Test
    void test_registerDevice__2params() throws Exception {
        Method method = ReflectionTestSupport.findMethod(DeviceServiceImpl.class, "registerDevice", 2);
        Assumptions.assumeTrue(method != null, "Method not found by reflection: registerDevice");
        ReflectionTestSupport.InvocationPlan plan = ReflectionTestSupport.planInvocation(method, DeviceServiceImpl.class);
        Assumptions.assumeTrue(plan.skipReason == null, plan.skipReason);

        Object result = assertDoesNotThrow(() -> ReflectionTestSupport.invoke(method, plan.target, plan.args));
        if (method.getReturnType() != void.class && !method.getReturnType().isPrimitive()) {
            assertNotNull(result);
        }
    }

    @Test
    void test_checkDeviceRegistration__2params() throws Exception {
        Method method = ReflectionTestSupport.findMethod(DeviceServiceImpl.class, "checkDeviceRegistration", 2);
        Assumptions.assumeTrue(method != null, "Method not found by reflection: checkDeviceRegistration");
        ReflectionTestSupport.InvocationPlan plan = ReflectionTestSupport.planInvocation(method, DeviceServiceImpl.class);
        Assumptions.assumeTrue(plan.skipReason == null, plan.skipReason);

        Object result = assertDoesNotThrow(() -> ReflectionTestSupport.invoke(method, plan.target, plan.args));
        if (method.getReturnType() != void.class && !method.getReturnType().isPrimitive()) {
            assertNotNull(result);
        }
    }

    @Test
    void test_getDeviceNetwork__2params() throws Exception {
        Method method = ReflectionTestSupport.findMethod(DeviceServiceImpl.class, "getDeviceNetwork", 2);
        Assumptions.assumeTrue(method != null, "Method not found by reflection: getDeviceNetwork");
        ReflectionTestSupport.InvocationPlan plan = ReflectionTestSupport.planInvocation(method, DeviceServiceImpl.class);
        Assumptions.assumeTrue(plan.skipReason == null, plan.skipReason);

        Object result = assertDoesNotThrow(() -> ReflectionTestSupport.invoke(method, plan.target, plan.args));
        if (method.getReturnType() != void.class && !method.getReturnType().isPrimitive()) {
            assertNotNull(result);
        }
    }

    @Test
    void test_getDeviceAction__2params() throws Exception {
        Method method = ReflectionTestSupport.findMethod(DeviceServiceImpl.class, "getDeviceAction", 2);
        Assumptions.assumeTrue(method != null, "Method not found by reflection: getDeviceAction");
        ReflectionTestSupport.InvocationPlan plan = ReflectionTestSupport.planInvocation(method, DeviceServiceImpl.class);
        Assumptions.assumeTrue(plan.skipReason == null, plan.skipReason);

        Object result = assertDoesNotThrow(() -> ReflectionTestSupport.invoke(method, plan.target, plan.args));
        if (method.getReturnType() != void.class && !method.getReturnType().isPrimitive()) {
            assertNotNull(result);
        }
    }

    @Test
    void test_controlDevice__3params() throws Exception {
        Method method = ReflectionTestSupport.findMethod(DeviceServiceImpl.class, "controlDevice", 3);
        Assumptions.assumeTrue(method != null, "Method not found by reflection: controlDevice");
        ReflectionTestSupport.InvocationPlan plan = ReflectionTestSupport.planInvocation(method, DeviceServiceImpl.class);
        Assumptions.assumeTrue(plan.skipReason == null, plan.skipReason);

        Object result = assertDoesNotThrow(() -> ReflectionTestSupport.invoke(method, plan.target, plan.args));
        if (method.getReturnType() != void.class && !method.getReturnType().isPrimitive()) {
            assertNotNull(result);
        }
    }

    @Test
    void test_completeDevice__2params() throws Exception {
        Method method = ReflectionTestSupport.findMethod(DeviceServiceImpl.class, "completeDevice", 2);
        Assumptions.assumeTrue(method != null, "Method not found by reflection: completeDevice");
        ReflectionTestSupport.InvocationPlan plan = ReflectionTestSupport.planInvocation(method, DeviceServiceImpl.class);
        Assumptions.assumeTrue(plan.skipReason == null, plan.skipReason);

        Object result = assertDoesNotThrow(() -> ReflectionTestSupport.invoke(method, plan.target, plan.args));
        if (method.getReturnType() != void.class && !method.getReturnType().isPrimitive()) {
            assertNotNull(result);
        }
    }

    @Test
    void test_scanDevice__2params() throws Exception {
        Method method = ReflectionTestSupport.findMethod(DeviceServiceImpl.class, "scanDevice", 2);
        Assumptions.assumeTrue(method != null, "Method not found by reflection: scanDevice");
        ReflectionTestSupport.InvocationPlan plan = ReflectionTestSupport.planInvocation(method, DeviceServiceImpl.class);
        Assumptions.assumeTrue(plan.skipReason == null, plan.skipReason);

        Object result = assertDoesNotThrow(() -> ReflectionTestSupport.invoke(method, plan.target, plan.args));
        if (method.getReturnType() != void.class && !method.getReturnType().isPrimitive()) {
            assertNotNull(result);
        }
    }

    @Test
    void test_scanCheckDevice__2params() throws Exception {
        Method method = ReflectionTestSupport.findMethod(DeviceServiceImpl.class, "scanCheckDevice", 2);
        Assumptions.assumeTrue(method != null, "Method not found by reflection: scanCheckDevice");
        ReflectionTestSupport.InvocationPlan plan = ReflectionTestSupport.planInvocation(method, DeviceServiceImpl.class);
        Assumptions.assumeTrue(plan.skipReason == null, plan.skipReason);

        Object result = assertDoesNotThrow(() -> ReflectionTestSupport.invoke(method, plan.target, plan.args));
        if (method.getReturnType() != void.class && !method.getReturnType().isPrimitive()) {
            assertNotNull(result);
        }
    }

    @Test
    void test_deleteDevice__3params() throws Exception {
        Method method = ReflectionTestSupport.findMethod(DeviceServiceImpl.class, "deleteDevice", 3);
        Assumptions.assumeTrue(method != null, "Method not found by reflection: deleteDevice");
        ReflectionTestSupport.InvocationPlan plan = ReflectionTestSupport.planInvocation(method, DeviceServiceImpl.class);
        Assumptions.assumeTrue(plan.skipReason == null, plan.skipReason);

        Object result = assertDoesNotThrow(() -> ReflectionTestSupport.invoke(method, plan.target, plan.args));
        if (method.getReturnType() != void.class && !method.getReturnType().isPrimitive()) {
            assertNotNull(result);
        }
    }

    @Test
    void test_deleteDeviceByMac__3params() throws Exception {
        Method method = ReflectionTestSupport.findMethod(DeviceServiceImpl.class, "deleteDeviceByMac", 3);
        Assumptions.assumeTrue(method != null, "Method not found by reflection: deleteDeviceByMac");
        ReflectionTestSupport.InvocationPlan plan = ReflectionTestSupport.planInvocation(method, DeviceServiceImpl.class);
        Assumptions.assumeTrue(plan.skipReason == null, plan.skipReason);

        Object result = assertDoesNotThrow(() -> ReflectionTestSupport.invoke(method, plan.target, plan.args));
        if (method.getReturnType() != void.class && !method.getReturnType().isPrimitive()) {
            assertNotNull(result);
        }
    }

    @Test
    void test_updateWifi__3params() throws Exception {
        Method method = ReflectionTestSupport.findMethod(DeviceServiceImpl.class, "updateWifi", 3);
        Assumptions.assumeTrue(method != null, "Method not found by reflection: updateWifi");
        ReflectionTestSupport.InvocationPlan plan = ReflectionTestSupport.planInvocation(method, DeviceServiceImpl.class);
        Assumptions.assumeTrue(plan.skipReason == null, plan.skipReason);

        Object result = assertDoesNotThrow(() -> ReflectionTestSupport.invoke(method, plan.target, plan.args));
        if (method.getReturnType() != void.class && !method.getReturnType().isPrimitive()) {
            assertNotNull(result);
        }
    }

}
