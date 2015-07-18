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
import personal.spring.SpringDevConfig;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by Niko on 7/10/15.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles({"dev"})
@ContextConfiguration(classes = {SpringDevConfig.class })
@WebAppConfiguration
@FixMethodOrder(MethodSorters.JVM)
public class ProjectServiceTest {

    @Autowired
    private WebApplicationContext wac;

    private String projectName = "a";
    private static Integer id;

    @Test
    public void testProjectInsert() throws Exception {
        MockMvc mock = MockMvcBuilders.webAppContextSetup(wac).build();
        ResultActions result;
        result = mock
                .perform(post("/project/create").param("projectName", projectName));
        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$").exists());
        String idStr = result.andReturn().getResponse().getContentAsString();
        id = Integer.valueOf(idStr);

        result = mock.perform(get("/project/list"));
        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$[0].id").value(id));

        result = mock.perform(get("/project/get/" + projectName));
        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$id").value(id));
    }

    @Test
    public void testProjectUpdate() throws Exception {
//        projectName = "b";
//        MockMvc mock = MockMvcBuilders.webAppContextSetup(wac).build();
//        ResultActions result;
//        result = mock
//                .perform(post("/project/update")
//                .param("id", id.toString())
//                .param("projectName", projectName));
//        result.andExpect(status().isOk());
//        result.andExpect(jsonPath("$").exists());
//        result.andExpect(jsonPath("$projectName").value(projectName));
    }

   //@Test
    public void testProjectRemove() throws Exception {
//        MockMvc mock = MockMvcBuilders.webAppContextSetup(wac).build();
//        ResultActions result;
//        result = mock
//                .perform(post("/project/remove")
//                        .param("id", id.toString()));
//        result.andExpect(status().isOk());
//        result.andExpect(jsonPath("$").exists());
//        result.andExpect(jsonPath("$isActive").value(0));
    }
}
