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

@WebMvcTest(controllers = GroupController.class)
@AutoConfigureMockMvc(addFilters = false)
class GroupControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private GroupService groupService;

    @Test
    void api_getGroupList__3params__GET() throws Exception {
        String path = ControllerTestSupport.fillPathVariables("/agent/contract/{contractNo}/group/list");
        MockHttpServletRequestBuilder req = MockMvcRequestBuilders.get(path)
                .accept(MediaType.APPLICATION_JSON);
        mvc.perform(req)
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void api_getGroupInfo__3params__GET() throws Exception {
        String path = ControllerTestSupport.fillPathVariables("/agent/contract/{contractNo}/group/{groupIdx}");
        MockHttpServletRequestBuilder req = MockMvcRequestBuilders.get(path)
                .accept(MediaType.APPLICATION_JSON);
        mvc.perform(req)
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void api_addGroup__3params__POST() throws Exception {
        String path = ControllerTestSupport.fillPathVariables("/agent/contract/{contractNo}/group");
        MockHttpServletRequestBuilder req = MockMvcRequestBuilders.post(path)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"groupName\":\"\",\"groupType\":\"\"}");
        mvc.perform(req)
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void api_modGroup__4params__POST() throws Exception {
        String path = ControllerTestSupport.fillPathVariables("/agent/contract/{contractNo}/group/{groupIdx}");
        MockHttpServletRequestBuilder req = MockMvcRequestBuilders.post(path)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"groupName\":\"\",\"groupType\":\"\"}");
        mvc.perform(req)
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void api_deleteGroup__3params__POST() throws Exception {
        String path = ControllerTestSupport.fillPathVariables("/agent/contract/{contractNo}/group/{groupIdx}/delete");
        MockHttpServletRequestBuilder req = MockMvcRequestBuilders.post(path)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}");
        mvc.perform(req)
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void api_addDeviceToGroup__4params__POST() throws Exception {
        String path = ControllerTestSupport.fillPathVariables("/agent/contract/{contractNo}/group/{groupIdx}/add");
        MockHttpServletRequestBuilder req = MockMvcRequestBuilders.post(path)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"deviceIdxList\":[]}");
        mvc.perform(req)
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void api_removeDeviceFromGroup__4params__POST() throws Exception {
        String path = ControllerTestSupport.fillPathVariables("/agent/contract/{contractNo}/group/{groupIdx}/remove");
        MockHttpServletRequestBuilder req = MockMvcRequestBuilders.post(path)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"deviceIdxList\":[]}");
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
