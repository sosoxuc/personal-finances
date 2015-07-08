package personal.finances.transactions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import personal.spring.SpringConfig;
import personal.spring.SpringDevConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles({"dev"})
@ContextConfiguration(classes = {SpringDevConfig.class })
@WebAppConfiguration
public class TransactionServiceTest {

    @Autowired
    private WebApplicationContext wac;

    @Test
    public void testTransactionInsert() throws Exception {
        MockMvc mock = MockMvcBuilders.webAppContextSetup(wac).build();
        ResultActions result;
        result = mock
                .perform(post("/transaction/create").param("amount", "10"));
        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$").exists());
        String idStr = result.andReturn().getResponse().getContentAsString();
        Integer id = Integer.valueOf(idStr);

        result = mock.perform(get("/transaction/search"));
        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$[0].id").value(id));
    }
}
