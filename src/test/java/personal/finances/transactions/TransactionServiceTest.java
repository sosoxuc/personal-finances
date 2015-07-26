package personal.finances.transactions;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.omg.CORBA.TRANSACTION_MODE;
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
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import personal.finances.accounts.Account;
import personal.finances.accounts.AccountServiceTest;
import personal.finances.currency.Currency;
import personal.finances.currency.CurrencyServiceTest;
import personal.finances.projects.Project;
import personal.finances.projects.ProjectServiceTest;
import personal.finances.transactions.rest.TransactionRest;
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
    private Currency currencyGel = null;
    private Currency currencyUsd = null;

    @Before
    public void prepareLibs() throws Exception {
        MockMvc mock = MockMvcBuilders.webAppContextSetup(wac).build();

        account = AccountServiceTest.createAccount(mock);

        currencyGel = CurrencyServiceTest.createCurrency(mock, "GEL");

        currencyUsd = CurrencyServiceTest.createCurrency(mock, "USD");

        project = ProjectServiceTest.createProject(mock);
    }

    @Test
    public void testReCalculate() throws Exception {
        MockMvc mock = MockMvcBuilders.webAppContextSetup(wac).build();

        String gel = currencyGel.id.toString();
        String usd = currencyUsd.id.toString();
        String json =

        "[  " + "   {  " + "      \"transactionDate\":\"03-07-2015\","
                + "      \"transactionAmount\":2240,"
                + "      \"transactionNote\":\"ვალუტის გაცვლა\","
                + "      \"currencyId\":" + gel + "," + "      \"direction\":1"
                + "   }," + "   {  "
                + "      \"transactionDate\":\"03-07-2015\","
                + "      \"transactionAmount\":1000,"
                + "      \"transactionNote\":\"ვალუტის გაცვლა\","
                + "      \"currencyId\":" + usd + "," + "      \"direction\":-1"
                + "   }," + "   {  "
                + "      \"transactionDate\":\"02-07-2015\","
                + "      \"transactionAmount\":1120,"
                + "      \"transactionNote\":\"ვალუტის გაცვლა\","
                + "      \"currencyId\":" + gel + "," + "      \"direction\":1"
                + "   }," + "   {  "
                + "      \"transactionDate\":\"02-07-2015\","
                + "      \"transactionAmount\":500,"
                + "      \"transactionNote\":\"ვალუტის გაცვლა\","
                + "      \"currencyId\":" + usd + "," + "      \"direction\":-1"
                + "   }," + "   {  "
                + "      \"transactionDate\":\"01-07-2015\","
                + "      \"transactionAmount\":1000,"
                + "      \"transactionNote\":\"კრედიტი\","
                + "      \"currencyId\":" + usd + "," + "      \"direction\":1"
                + "   }," + "   {  "
                + "      \"transactionDate\":\"01-07-2015\","
                + "      \"transactionAmount\":2000,"
                + "      \"transactionNote\":\"კრედიტი\","
                + "      \"currencyId\":" + usd + "," + "      \"direction\":1"
                + "   }" + "]";
        TypeReference<List<TransactionTestData>> ref = new TypeReference<List<TransactionTestData>>() {
        };
        ObjectMapper mapper = new ObjectMapper();
        List<TransactionTestData> data = mapper.readValue(json, ref);
        List<Transaction> trs = new ArrayList<>();
        ResultActions result;
        for (TransactionTestData item : data) {
            result = mock
                    .perform(
                            post("/transaction/create")
                                    .param("amount",
                                            item.transactionAmount.toString())
                                    .param("projectId", project.id.toString())
                                    .param("accountId", account.id.toString())
                                    .param("currencyId",
                                            item.currencyId.toString())
                    .param("direction", item.direction.toString())
                    .param("date", item.transactionDate)
                    .param("note", item.transactionNote));
            result.andExpect(status().isOk());
            result.andExpect(jsonPath("$").exists());
            String trJson = result.andReturn().getResponse()
                    .getContentAsString();
            Transaction tr = mapper.readValue(trJson, Transaction.class);
            trs.add(tr);
        }

        result = mock.perform(
                post("/transaction/rests/currencies"));
        result.andExpect(status().isOk());
        String rsJson = result.andReturn().getResponse().getContentAsString();
        TypeReference<List<TransactionRest>> rsRef = new TypeReference<List<TransactionRest>>(){}; 
        List<TransactionRest> rests = mapper.readValue(rsJson, rsRef);
        
        TransactionRest usdRest=null;
        for (TransactionRest rest : rests) {
            if (rest.resourceName.equals(currencyUsd.currencyCode)){
                usdRest=rest;
            }
        }
        
        assertNotNull(usdRest);
        assertEquals(usdRest.transactionRest.intValue(), 1500);
        
        
        
        result = mock.perform(
                post("/transaction/rests/calculate"));
        result.andExpect(status().isOk());

        result = mock.perform(
                post("/transaction/rests/currencies"));
        result.andExpect(status().isOk());
        rsJson = result.andReturn().getResponse().getContentAsString();
        rests = mapper.readValue(rsJson, rsRef);
        
        usdRest=null;
        for (TransactionRest rest : rests) {
            if (rest.resourceName.equals(currencyUsd.currencyCode)){
                usdRest=rest;
            }
        }
        
        
        // Cleanup
        for (Transaction tr : trs) {
            result = mock.perform(
                    post("/transaction/remove").param("id", tr.id.toString())
                            .param("version", ((Long)(tr.version+1)).toString()));
            result.andExpect(status().isOk());
        }
        
        assertNotNull(usdRest);
        assertEquals(usdRest.transactionRest.intValue(), 1500);
    }

    @Test
    public void testTransactionInsert() throws Exception {
        MockMvc mock = MockMvcBuilders.webAppContextSetup(wac).build();
        ResultActions result;
        result = mock.perform(post("/transaction/create").param("amount", "10")
                .param("projectId", project.id.toString())
                .param("accountId", account.id.toString())
                .param("currencyId", currencyGel.id.toString())
                .param("direction", "1").param("date", "2015-07-20")
                .param("note", "test"));
        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$").exists());
        String json = result.andReturn().getResponse().getContentAsString();
        Transaction tr = new ObjectMapper().readValue(json, Transaction.class);
        Integer id = Integer.valueOf(tr.id);

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
        CurrencyServiceTest.removeCurrency(mock, currencyGel);
        CurrencyServiceTest.removeCurrency(mock, currencyUsd);
        ProjectServiceTest.removeProject(mock, project);
    }
}

class TransactionTestData {
    public String transactionDate;
    public Float transactionAmount;
    public String transactionNote;
    public Integer projectId;
    public Integer accountId;
    public Integer currencyId;
    public Integer direction;
}
