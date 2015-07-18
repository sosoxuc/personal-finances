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

    private String projectId = null;
    private String accountId = null;
    private String currencyId = null;

    @Before
    public void prepareLibs() throws Exception {
        MockMvc mock = MockMvcBuilders.webAppContextSetup(wac).build();

        Account account = AccountServiceTest.createAccount(mock);
        accountId = account.id.toString();

        Currency currency = CurrencyServiceTest.createCurrency(mock);
        currencyId = currency.id.toString();

        Project project = ProjectServiceTest.createProject(mock);
        projectId = project.id.toString();
    }

    @Test
    public void testTransactionInsert() throws Exception {
        MockMvc mock = MockMvcBuilders.webAppContextSetup(wac).build();
        ResultActions result;
        result = mock.perform(post("/transaction/create").param("amount", "10")
                .param("projectId", projectId).param("accountId", accountId)
                .param("currencyId", currencyId).param("direction", "1")
                .param("date", "2015-07-20").param("note", "test"));
        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$").exists());
        String idStr = result.andReturn().getResponse().getContentAsString();
        Integer id = Integer.valueOf(idStr);

        result = mock.perform(get("/transaction/search"));
        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.list[0].id").value(id));
    }

    @After
    public void cleanupLibs() throws Exception {

        MockMvc mock = MockMvcBuilders.webAppContextSetup(wac).build();

        AccountServiceTest.removeAccount(mock, accountId);
        CurrencyServiceTest.removeCurrency(mock, currencyId);
        ProjectServiceTest.removeProject(mock, projectId);
    }
}
