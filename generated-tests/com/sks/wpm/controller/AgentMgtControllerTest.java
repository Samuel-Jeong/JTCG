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

@WebMvcTest(controllers = AgentMgtController.class)
@AutoConfigureMockMvc(addFilters = false)
class AgentMgtControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private AgentMgtService agentMgtService;

    @Test
    void api_getContractInfo__2params__GET() throws Exception {
        String path = ControllerTestSupport.fillPathVariables("/agent/contract/{contractNo}");
        MockHttpServletRequestBuilder req = MockMvcRequestBuilders.get(path)
                .accept(MediaType.APPLICATION_JSON);
        mvc.perform(req)
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void api_getDeviceList__2params__GET() throws Exception {
        String path = ControllerTestSupport.fillPathVariables("/agent/contract/{contractNo}/device/list");
        MockHttpServletRequestBuilder req = MockMvcRequestBuilders.get(path)
                .accept(MediaType.APPLICATION_JSON);
        mvc.perform(req)
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void api_getDeviceInfo__2params__GET() throws Exception {
        String path = ControllerTestSupport.fillPathVariables("/agent/contract/{contractNo}/device/{deviceIdx}");
        MockHttpServletRequestBuilder req = MockMvcRequestBuilders.get(path)
                .accept(MediaType.APPLICATION_JSON);
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
