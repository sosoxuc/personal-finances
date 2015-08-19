package personal.finances.transactions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.After;
import org.junit.Before;
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

import personal.finances.accounts.Account;
import personal.finances.accounts.AccountServiceTest;
import personal.finances.currency.Currency;
import personal.finances.currency.CurrencyServiceTest;
import personal.finances.projects.Project;
import personal.finances.projects.ProjectServiceTest;
import personal.security.SecurityServiceTest;
import personal.spring.SpringConfig;

/**
 * Created by niko on 8/17/15.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { SpringConfig.class })
@WebAppConfiguration
public class MultipleTransactionTest {

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private MockHttpSession session;

    private Project project = null;
    private Account account = null;
    private Currency currencyGel = null;
    private Currency currencyUsd = null;

    @Before
    public void prepareLibs() throws Exception {
        MockMvc mock = MockMvcBuilders.webAppContextSetup(wac).build();
        SecurityServiceTest.signin(mock, session);

        account = AccountServiceTest.createAccount(mock, session);

        currencyGel = CurrencyServiceTest.createCurrency(mock, "GEL", session);

        currencyUsd = CurrencyServiceTest.createCurrency(mock, "USD", session);

        project = ProjectServiceTest.createProject(mock, session);
    }

    @Test
    public void testTransactionInsert() throws Exception {
        MockMvc mock = MockMvcBuilders.webAppContextSetup(wac).build();
        SecurityServiceTest.signin(mock, session);

        ResultActions result;
        result = mock.perform(post("/transaction/createMultiple").session(session)
                .param("amount", "10").param("projectId", project.id.toString())
                .param("accountId", account.id.toString())
                .param("currencyId", currencyGel.id.toString())
                .param("direction", "1").param("intervalType", "1")
                .param("intervalTypeValue", "5").param("dateFrom", "01-10-2015")
                .param("dateTo", "16-10-2015").param("note", "test"));
        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$").exists());
        String json = result.andReturn().getResponse().getContentAsString();
    }

    @After
    public void cleanupLibs() throws Exception {

        MockMvc mock = MockMvcBuilders.webAppContextSetup(wac).build();
        SecurityServiceTest.signin(mock, session);
        AccountServiceTest.removeAccount(mock, account, session);
        CurrencyServiceTest.removeCurrency(mock, currencyGel, session);
        CurrencyServiceTest.removeCurrency(mock, currencyUsd, session);
        ProjectServiceTest.removeProject(mock, project, session);
    }
}
