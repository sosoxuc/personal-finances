package personal.finances.projects;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.UnsupportedEncodingException;

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

import personal.spring.SpringDevConfig;

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
        Project project = createProject(mock);
        String idStr = project.id.toString();
        Integer id = project.id;
        Long version = project.version;

        result = mock.perform(get("/project/list"));
        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$[0].id").value(id));
        result.andExpect(jsonPath("$[0].projectName").value("test"));

        result = mock
                .perform(post("/project/update")
                        .param("id", idStr)
                        .param("projectName", "test2")
                        .param("version", version.toString()));
        result.andExpect(status().isOk());

        result = mock.perform(get("/project/list"));
        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$[0].id").value(id));
        result.andExpect(jsonPath("$[0].projectName").value("test2"));

        removeProject(mock, idStr, version + 1);

        result = mock.perform(get("/project/list"));
        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$[0]").doesNotExist());

    }

    public static void removeProject(MockMvc mock, String idStr, Long version)
            throws Exception {
        ResultActions result;
        result = mock
                .perform(post("/project/remove")
                        .param("id", idStr)
                        .param("version", version.toString()));
        result.andExpect(status().isOk());
    }

    public static Project createProject(MockMvc mock)
            throws Exception {
        ResultActions result;
        result = mock
                .perform(post("/project/create").param("projectName", "test"));
        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$").exists());
        String jsonStr = result.andReturn().getResponse().getContentAsString();
        Project project = new ObjectMapper().readValue(jsonStr, Project.class);
        return project;
    }

    @Test
    public void testProjectsExceptions() throws Exception {
        MockMvc mock = MockMvcBuilders.webAppContextSetup(wac).build();
        ResultActions result;
        result = mock.perform(post("/project/update")
                .param("id", "-1")
                .param("projectName", "test2")
                .param("version", "-1"));
        result.andExpect(status().isNotFound());

        result = mock.perform(post("/project/remove")
                .param("id", "-1")
                .param("version", "-1"));
        result.andExpect(status().isNotFound());

        result = mock
                .perform(post("/project/create").param("projectName", "test5"));
        result.andExpect(status().isOk());
        String jsonStr = result.andReturn().getResponse().getContentAsString();
        Project project1= new ObjectMapper().readValue(jsonStr, Project.class);
        String idStr=project1.id.toString();
        Long version = project1.version;
        result = mock
                .perform(post("/project/create").param("projectName", "test5"));
        result.andExpect(status().isConflict());

        result = mock
                .perform(post("/project/create").param("projectName", "test6"));
        result.andExpect(status().isOk());
        String jsonStr2 = result.andReturn().getResponse().getContentAsString();
        Project project2 = new ObjectMapper().readValue(jsonStr2, Project.class);
        String idStr2 = project2.id.toString();
        Long version2 = project2.version;

        result = mock.perform(post("/project/update")
                .param("id", idStr2)
                .param("projectName", "test5")
                .param("version", version2.toString()));
        result.andExpect(status().isConflict());

        removeProject(mock, idStr, version);

        removeProject(mock, idStr2, version);

    }
}
