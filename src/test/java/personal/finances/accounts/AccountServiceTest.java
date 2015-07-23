package personal.finances.accounts;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import personal.spring.SpringDevConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles({ "dev" })
@ContextConfiguration(classes = { SpringDevConfig.class })
@WebAppConfiguration
public class AccountServiceTest {

    @Autowired
    private WebApplicationContext wac;

    @Test
    public void testAccounts() throws Exception {
        MockMvc mock = MockMvcBuilders.webAppContextSetup(wac).build();
        ResultActions result;
        Account account = createAccount(mock);
        String idStr = account.id.toString();
        Integer id = account.id;
        Long version = account.version;

        result = mock.perform(get("/account/list"));
        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$[0].id").value(id));
        result.andExpect(jsonPath("$[0].accountName").value("test"));
        result.andExpect(jsonPath("$[0].accountNumber").value("num"));



        result = mock.perform(post("/account/update")
                .param("id", idStr)
                .param("accountName", "test2")
                .param("accountNumber", "num2")
                .param("version", version.toString() ));
        result.andExpect(status().isOk());

        result = mock.perform(get("/account/list"));
        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$[0].id").value(id));
        result.andExpect(jsonPath("$[0].accountName").value("test2"));
        result.andExpect(jsonPath("$[0].accountNumber").value("num2"));

        removeAccount(mock, idStr, version + 1);

        result = mock.perform(get("/account/list"));
        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$[0]").doesNotExist());

    }

    public static void removeAccount(MockMvc mock, String idStr, Long version)
            throws Exception {
        ResultActions result;
        result = mock
                .perform(post("/account/remove")
                        .param("id", idStr)
                        .param("version", version.toString()));
        result.andExpect(status().isOk());
    }

    public static Account createAccount(MockMvc mock)
            throws Exception {
        ResultActions result;
        result = mock.perform(post("/account/create")
                .param("accountName", "test")
                .param("accountNumber", "num"));
        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$").exists());
        String jsonStr = result.andReturn().getResponse().getContentAsString();
        Account account = new ObjectMapper().readValue(jsonStr, Account.class);
        return account;
    }

    @Test
    public void testAccountsExceptions() throws Exception {
        MockMvc mock = MockMvcBuilders.webAppContextSetup(wac).build();
        ResultActions result;
        result = mock.perform(post("/account/update")
                .param("id", "-1")
                .param("accountName", "test2")
                .param("accountNumber", "num2")
                .param("version", "-1"));
        result.andExpect(status().isNotFound());

        result = mock.perform(post("/account/remove")
                .param("id", "-1")
                .param("version", "-1"));
        result.andExpect(status().isNotFound());
    }
}
