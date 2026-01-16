package com.sks.wpm.controller;

import com.jtcg.generated.support.ControllerTestSupport;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = DeviceController.class)
@AutoConfigureMockMvc(addFilters = false)
class DeviceControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private IotDeviceService iotDeviceService;

    @Test
    void api_registerDevice__2params__POST() throws Exception {
        String path = ControllerTestSupport.fillPathVariables("/agent/device/registration/iot");
        MockHttpServletRequestBuilder req = MockMvcRequestBuilders.post(path)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"contractNo\":\"\",\"deviceType\":\"\",\"macAddr\":\"\",\"creatorId\":\"\",\"creatorName\":\"\",\"hubDeviceIdx\":0,\"transactionId\":\"\"}");
        mvc.perform(req)
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void api_checkDeviceRegistration__2params__GET() throws Exception {
        String path = ControllerTestSupport.fillPathVariables("/agent/device/{deviceIdx}/check");
        MockHttpServletRequestBuilder req = MockMvcRequestBuilders.get(path)
                .accept(MediaType.APPLICATION_JSON);
        mvc.perform(req)
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void api_getDeviceNetwork__2params__GET() throws Exception {
        String path = ControllerTestSupport.fillPathVariables("/agent/device/{deviceIdx}/network");
        MockHttpServletRequestBuilder req = MockMvcRequestBuilders.get(path)
                .accept(MediaType.APPLICATION_JSON);
        mvc.perform(req)
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void api_getDeviceAction__2params__GET() throws Exception {
        String path = ControllerTestSupport.fillPathVariables("/agent/device/{deviceIdx}/status");
        MockHttpServletRequestBuilder req = MockMvcRequestBuilders.get(path)
                .accept(MediaType.APPLICATION_JSON);
        mvc.perform(req)
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void api_controlDevice__3params__POST() throws Exception {
        String path = ControllerTestSupport.fillPathVariables("/agent/device/{deviceIdx}/control");
        MockHttpServletRequestBuilder req = MockMvcRequestBuilders.post(path)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"restartStatus\":0,\"powerStatus\":0,\"controlBrand\":\"\",\"controlType\":\"\",\"desiredTemp\":0,\"mode\":0,\"fanSpeed\":0,\"transactionId\":\"\"}");
        mvc.perform(req)
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void api_completeDevice__2params__POST() throws Exception {
        String path = ControllerTestSupport.fillPathVariables("/agent/device/{deviceIdx}/update");
        MockHttpServletRequestBuilder req = MockMvcRequestBuilders.post(path)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"deviceName\":\"\",\"groupIdx\":0,\"linkedMainDeviceIdx\":0,\"deviceMode\":0,\"controlBrand\":\"\",\"controlType\":\"\",\"transactionId\":\"\"}");
        mvc.perform(req)
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void api_scanDevice__2params__POST() throws Exception {
        String path = ControllerTestSupport.fillPathVariables("/agent/device/{deviceIdx}/scan");
        MockHttpServletRequestBuilder req = MockMvcRequestBuilders.post(path)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"deviceType\":\"\",\"transactionId\":\"\"}");
        mvc.perform(req)
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void api_scanCheckDevice__2params__GET() throws Exception {
        String path = ControllerTestSupport.fillPathVariables("/agent/device/{deviceIdx}/scan/check");
        MockHttpServletRequestBuilder req = MockMvcRequestBuilders.get(path)
                .accept(MediaType.APPLICATION_JSON);
        mvc.perform(req)
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void api_deleteDevice__3params__POST() throws Exception {
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
