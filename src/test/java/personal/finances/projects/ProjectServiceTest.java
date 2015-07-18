package personal.finances.projects;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

import personal.finances.accounts.Account;
import personal.spring.SpringDevConfig;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.UnsupportedEncodingException;

/**
 * Created by Niko on 7/10/15.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles({ "dev" })
@ContextConfiguration(classes = { SpringDevConfig.class })
@WebAppConfiguration
@FixMethodOrder(MethodSorters.JVM)
public class ProjectServiceTest {

    @Autowired
    private WebApplicationContext wac;

    @Test
    public void testProjects() throws Exception {
        MockMvc mock = MockMvcBuilders.webAppContextSetup(wac).build();
        ResultActions result;
        String idStr = createProject(mock);
        Integer id = Integer.valueOf(idStr);

        result = mock.perform(get("/project/list"));
        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$[0].id").value(id));
        result.andExpect(jsonPath("$[0].projectName").value("test"));

        result = mock.perform(post("/project/update").param("id", idStr)
                .param("projectName", "test2"));
        result.andExpect(status().isOk());

        result = mock.perform(get("/project/list"));
        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$[0].id").value(id));
        result.andExpect(jsonPath("$[0].projectName").value("test2"));

        result = mock.perform(post("/project/remove").param("id", idStr));
        result.andExpect(status().isOk());

        result = mock.perform(get("/project/list"));
        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$[0]").doesNotExist());

    }

    public static String createProject(MockMvc mock)
            throws Exception, UnsupportedEncodingException {
        ResultActions result;
        result = mock
                .perform(post("/project/create").param("projectName", "test"));
        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$").exists());
        String idStr = result.andReturn().getResponse().getContentAsString();
        return idStr;
    }

    @Test
    public void testAccountsExceptions() throws Exception {
        MockMvc mock = MockMvcBuilders.webAppContextSetup(wac).build();
        ResultActions result;
        result = mock.perform(post("/project/update").param("id", "-1")
                .param("projectName", "test2"));
        result.andExpect(status().isNotFound());

        result = mock.perform(post("/project/remove").param("id", "-1"));
        result.andExpect(status().isNotFound());

        result = mock
                .perform(post("/project/create").param("projectName", "test5"));
        result.andExpect(status().isOk());
        String idStr = result.andReturn().getResponse().getContentAsString();

        result = mock
                .perform(post("/project/create").param("projectName", "test5"));
        result.andExpect(status().isConflict());

        result = mock
                .perform(post("/project/create").param("projectName", "test6"));
        result.andExpect(status().isOk());
        String idStr2 = result.andReturn().getResponse().getContentAsString();

        result = mock.perform(post("/project/update").param("id", idStr2)
                .param("projectName", "test5"));
        result.andExpect(status().isConflict());

        result = mock.perform(post("/project/remove").param("id", idStr));
        result.andExpect(status().isOk());

        result = mock.perform(post("/project/remove").param("id", idStr2));
        result.andExpect(status().isOk());

    }
}
