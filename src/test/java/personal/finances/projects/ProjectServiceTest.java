package personal.finances.projects;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

import personal.security.SecurityServiceTest;
import personal.spring.SpringConfig;

/**
 * Created by Niko on 7/10/15.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { SpringConfig.class })
@WebAppConfiguration
public class ProjectServiceTest {

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private MockHttpSession session;

    @Test
    public void testProjects() throws Exception {
        MockMvc mock = MockMvcBuilders.webAppContextSetup(wac).build();
        SecurityServiceTest.signin(mock, session);

        ResultActions result;
        Project project = createProject(mock, session);
        String idStr = project.id.toString();
        Integer id = project.id;
        Long version = project.version;

        result = mock.perform(get("/project/list").session(session));
        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$[0].id").value(id));
        result.andExpect(jsonPath("$[0].projectName").value("test"));

        result = mock.perform(post("/project/update").session(session)
                .param("id", idStr).param("projectName", "test2")
                .param("version", version.toString()));
        result.andExpect(status().isOk());

        result = mock.perform(get("/project/list").session(session));
        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$[0].id").value(id));
        result.andExpect(jsonPath("$[0].projectName").value("test2"));

        project.version = project.version + 1;
        removeProject(mock, project, session);

        result = mock.perform(get("/project/list").session(session));
        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$[0]").doesNotExist());

    }

    public static void removeProject(MockMvc mock, Project project,
            MockHttpSession session) throws Exception {
        ResultActions result;
        result = mock.perform(post("/project/remove").session(session)
                .param("id", project.id.toString())
                .param("version", project.version.toString()));
        result.andExpect(status().isOk());
    }

    public static Project createProject(MockMvc mock, MockHttpSession session)
            throws Exception {
        ResultActions result;
        result = mock.perform(post("/project/create").session(session)
                .param("projectName", "test"));
        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$").exists());
        String jsonStr = result.andReturn().getResponse().getContentAsString();
        Project project = new ObjectMapper().readValue(jsonStr, Project.class);
        return project;
    }

    @Test
    public void testProjectsExceptions() throws Exception {
        MockMvc mock = MockMvcBuilders.webAppContextSetup(wac).build();
        SecurityServiceTest.signin(mock, session);

        ResultActions result;
        result = mock.perform(
                post("/project/update").session(session).param("id", "-1")
                        .param("projectName", "test2").param("version", "-1"));
        result.andExpect(status().isNotFound());

        result = mock.perform(post("/project/remove").session(session)
                .param("id", "-1").param("version", "-1"));
        result.andExpect(status().isNotFound());

        result = mock.perform(post("/project/create").session(session)
                .param("projectName", "test5"));
        result.andExpect(status().isOk());
        String jsonStr = result.andReturn().getResponse().getContentAsString();
        Project project1 = new ObjectMapper().readValue(jsonStr, Project.class);
        String idStr = project1.id.toString();
        Long version = project1.version;
        result = mock.perform(post("/project/create").session(session)
                .param("projectName", "test5"));
        result.andExpect(status().isConflict());

        result = mock.perform(post("/project/create").session(session)
                .param("projectName", "test6"));
        result.andExpect(status().isOk());
        String jsonStr2 = result.andReturn().getResponse().getContentAsString();
        Project project2 = new ObjectMapper().readValue(jsonStr2,
                Project.class);
        String idStr2 = project2.id.toString();
        Long version2 = project2.version;

        result = mock.perform(post("/project/update").session(session)
                .param("id", idStr2).param("projectName", "test5")
                .param("version", version2.toString()));
        result.andExpect(status().isConflict());

        removeProject(mock, project1, session);

        removeProject(mock, project2, session);

    }
}
