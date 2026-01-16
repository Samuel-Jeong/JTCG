package com.sks.wpm.controller;

import com.jtcg.generated.support.ControllerTestSupport;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class DeviceControllerTest {

    private MockMvc mockMvc() {
        return ControllerTestSupport.mockMvcFor(DeviceController.class);
    }

    @Test
    void api_registerDevice__2params__POST() throws Exception {
        MockMvc mvc = mockMvc();
        String path = ControllerTestSupport.fillPathVariables("/agent/device/registration/iot");
        MockHttpServletRequestBuilder req = MockMvcRequestBuilders.post(path)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}");
        mvc.perform(req)
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void api_checkDeviceRegistration__2params__GET() throws Exception {
        MockMvc mvc = mockMvc();
        String path = ControllerTestSupport.fillPathVariables("/agent/device/{deviceIdx}/check");
        MockHttpServletRequestBuilder req = MockMvcRequestBuilders.get(path)
                .accept(MediaType.APPLICATION_JSON);
        mvc.perform(req)
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void api_getDeviceNetwork__2params__GET() throws Exception {
        MockMvc mvc = mockMvc();
        String path = ControllerTestSupport.fillPathVariables("/agent/device/{deviceIdx}/network");
        MockHttpServletRequestBuilder req = MockMvcRequestBuilders.get(path)
                .accept(MediaType.APPLICATION_JSON);
        mvc.perform(req)
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void api_getDeviceAction__2params__GET() throws Exception {
        MockMvc mvc = mockMvc();
        String path = ControllerTestSupport.fillPathVariables("/agent/device/{deviceIdx}/status");
        MockHttpServletRequestBuilder req = MockMvcRequestBuilders.get(path)
                .accept(MediaType.APPLICATION_JSON);
        mvc.perform(req)
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void api_controlDevice__3params__POST() throws Exception {
        MockMvc mvc = mockMvc();
        String path = ControllerTestSupport.fillPathVariables("/agent/device/{deviceIdx}/control");
        MockHttpServletRequestBuilder req = MockMvcRequestBuilders.post(path)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}");
        mvc.perform(req)
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void api_completeDevice__2params__POST() throws Exception {
        MockMvc mvc = mockMvc();
        String path = ControllerTestSupport.fillPathVariables("/agent/device/{deviceIdx}/update");
        MockHttpServletRequestBuilder req = MockMvcRequestBuilders.post(path)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}");
        mvc.perform(req)
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void api_scanDevice__2params__POST() throws Exception {
        MockMvc mvc = mockMvc();
        String path = ControllerTestSupport.fillPathVariables("/agent/device/{deviceIdx}/scan");
        MockHttpServletRequestBuilder req = MockMvcRequestBuilders.post(path)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}");
        mvc.perform(req)
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void api_scanCheckDevice__2params__GET() throws Exception {
        MockMvc mvc = mockMvc();
        String path = ControllerTestSupport.fillPathVariables("/agent/device/{deviceIdx}/scan/check");
        MockHttpServletRequestBuilder req = MockMvcRequestBuilders.get(path)
                .accept(MediaType.APPLICATION_JSON);
        mvc.perform(req)
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void api_deleteDevice__3params__POST() throws Exception {
        MockMvc mvc = mockMvc();
        String path = ControllerTestSupport.fillPathVariables("/agent/device/{deviceIdx}/delete");
        MockHttpServletRequestBuilder req = MockMvcRequestBuilders.post(path)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}");
        mvc.perform(req)
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void api_deleteDeviceByMac__3params__POST() throws Exception {
        MockMvc mvc = mockMvc();
        String path = ControllerTestSupport.fillPathVariables("/agent/device/{macAddress}/mac/delete");
        MockHttpServletRequestBuilder req = MockMvcRequestBuilders.post(path)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}");
        mvc.perform(req)
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void api_updateWifi__3params__POST() throws Exception {
        MockMvc mvc = mockMvc();
        String path = ControllerTestSupport.fillPathVariables("/agent/device/{deviceIdx}/wifi/update");
        MockHttpServletRequestBuilder req = MockMvcRequestBuilders.post(path)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}");
        mvc.perform(req)
                .andExpect(status().is2xxSuccessful());
    }

    private static String toMockMvcBuilderMethod(String httpMethod) {
        if (httpMethod == null) return "get";
        return switch (httpMethod.toUpperCase()) {
            case "POST" -> "post";
            case "PUT" -> "put";
            case "DELETE" -> "delete";
            case "PATCH" -> "patch";
            default -> "get";
        };
    }

}
