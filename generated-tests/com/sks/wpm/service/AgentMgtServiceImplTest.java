package com.sks.wpm.service;

import com.jtcg.generated.support.ReflectionTestSupport;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class AgentMgtServiceImplTest {

    @Test
    void test_getContractInfo__2params() throws Exception {
        Method method = ReflectionTestSupport.findMethod(AgentMgtServiceImpl.class, "getContractInfo", 2);
        Assumptions.assumeTrue(method != null, "Method not found by reflection: getContractInfo");
        ReflectionTestSupport.InvocationPlan plan = ReflectionTestSupport.planInvocation(method, AgentMgtServiceImpl.class);
        Assumptions.assumeTrue(plan.skipReason == null, plan.skipReason);

        Object result = assertDoesNotThrow(() -> ReflectionTestSupport.invoke(method, plan.target, plan.args));
        if (method.getReturnType() != void.class && !method.getReturnType().isPrimitive()) {
            assertNotNull(result);
        }
    }

    @Test
    void test_getDeviceList__2params() throws Exception {
        Method method = ReflectionTestSupport.findMethod(AgentMgtServiceImpl.class, "getDeviceList", 2);
        Assumptions.assumeTrue(method != null, "Method not found by reflection: getDeviceList");
        ReflectionTestSupport.InvocationPlan plan = ReflectionTestSupport.planInvocation(method, AgentMgtServiceImpl.class);
        Assumptions.assumeTrue(plan.skipReason == null, plan.skipReason);

        Object result = assertDoesNotThrow(() -> ReflectionTestSupport.invoke(method, plan.target, plan.args));
        if (method.getReturnType() != void.class && !method.getReturnType().isPrimitive()) {
            assertNotNull(result);
        }
    }

    @Test
    void test_getDeviceInfo__2params() throws Exception {
        Method method = ReflectionTestSupport.findMethod(AgentMgtServiceImpl.class, "getDeviceInfo", 2);
        Assumptions.assumeTrue(method != null, "Method not found by reflection: getDeviceInfo");
        ReflectionTestSupport.InvocationPlan plan = ReflectionTestSupport.planInvocation(method, AgentMgtServiceImpl.class);
        Assumptions.assumeTrue(plan.skipReason == null, plan.skipReason);

        Object result = assertDoesNotThrow(() -> ReflectionTestSupport.invoke(method, plan.target, plan.args));
        if (method.getReturnType() != void.class && !method.getReturnType().isPrimitive()) {
            assertNotNull(result);
        }
    }

}
