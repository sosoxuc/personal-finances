package personal.finances.transactions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.After;
import org.junit.Before;
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

import personal.finances.accounts.Account;
import personal.finances.accounts.AccountServiceTest;
import personal.finances.currency.Currency;
import personal.finances.currency.CurrencyServiceTest;
import personal.finances.projects.Project;
import personal.finances.projects.ProjectServiceTest;
import personal.spring.SpringDevConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles({ "dev" })
@ContextConfiguration(classes = { SpringDevConfig.class })
@WebAppConfiguration
public class TransactionServiceTest {

    @Autowired
    private WebApplicationContext wac;

    private Project project = null;
    private Account account = null;
    private Currency currency = null;

    @Before
    public void prepareLibs() throws Exception {
        MockMvc mock = MockMvcBuilders.webAppContextSetup(wac).build();

        account = AccountServiceTest.createAccount(mock);

        currency = CurrencyServiceTest.createCurrency(mock);

        project = ProjectServiceTest.createProject(mock);
    }

    @Test
    public void testTransactionInsert() throws Exception {
        MockMvc mock = MockMvcBuilders.webAppContextSetup(wac).build();
        ResultActions result;
        result = mock.perform(post("/transaction/create").param("amount", "10")
                .param("projectId", project.id.toString()).param("accountId", account.id.toString())
                .param("currencyId", currency.id.toString()).param("direction", "1")
                .param("date", "2015-07-20").param("note", "test"));
        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$").exists());
        String idStr = result.andReturn().getResponse().getContentAsString();
        Integer id = Integer.valueOf(idStr);

        result = mock.perform(get("/transaction/search"));
        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.list[0].id").value(id));

        result = mock.perform(get("/transaction/rests/currencies"));
        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$[0]").exists());

    }

    @After
    public void cleanupLibs() throws Exception {

        MockMvc mock = MockMvcBuilders.webAppContextSetup(wac).build();

        AccountServiceTest.removeAccount(mock, account);
        CurrencyServiceTest.removeCurrency(mock, currency);
        ProjectServiceTest.removeProject(mock, project);
    }
}
