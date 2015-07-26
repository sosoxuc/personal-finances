package personal.finances.currency;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

import com.fasterxml.jackson.databind.ObjectMapper;

import personal.spring.SpringDevConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles({ "dev" })
@ContextConfiguration(classes = { SpringDevConfig.class })
@WebAppConfiguration

public class CurrencyServiceTest {

    @Autowired
    private WebApplicationContext wac;

    @Test
    public void testCurrencies() throws Exception {
        MockMvc mock = MockMvcBuilders.webAppContextSetup(wac).build();
        ResultActions result;
        Currency currency = createCurrency(mock);
        String idStr = currency.id.toString();
        Integer id = currency.id;
        Long version = currency.version;

        result = mock.perform(get("/currency/list"));
        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$[0].id").value(id));
        result.andExpect(jsonPath("$[0].currencyName").value("test"));
        result.andExpect(jsonPath("$[0].currencyCode").value("code"));
        result.andExpect(jsonPath("$[0].version").value(version.intValue()));
        
        result = mock.perform(post("/currency/update")
                .param("id", idStr)
                .param("currencyName", "test2")
                .param("currencyCode", "code2")
                .param("version", version.toString()));
        result.andExpect(status().isOk());

        result = mock.perform(get("/currency/list"));
        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$[0].id").value(id));
        result.andExpect(jsonPath("$[0].currencyName").value("test2"));
        result.andExpect(jsonPath("$[0].currencyCode").value("code2"));
        currency.version = currency.version + 1;
        removeCurrency(mock, currency);

        result = mock.perform(get("/currency/list"));
        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$[0]").doesNotExist());
    }

    public static void removeCurrency(MockMvc mock, Currency currency) throws Exception {
        ResultActions result;
        result = mock.perform(post("/currency/remove")
                .param("id", currency.id.toString())
                .param("version", currency.version.toString()));
        result.andExpect(status().isOk());
    }

    public static Currency createCurrency(MockMvc mock)
            throws Exception {
        ResultActions result;
        result = mock.perform(post("/currency/create")
                .param("currencyName", "test").param("currencyCode", "code"));
        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$").exists());
        String jsonStr = result.andReturn().getResponse().getContentAsString();
        Currency currency = new ObjectMapper().readValue(jsonStr,
                Currency.class);
        return currency;
    }

    @Test
    public void testCurrenciesExceptions() throws Exception {
        MockMvc mock = MockMvcBuilders.webAppContextSetup(wac).build();
        ResultActions result;
        result = mock.perform(post("/currency/update")
                .param("id", "-1")
                .param("currencyName", "test2")
                .param("currencyCode", "num2")
                .param("version", "-1"));
        result.andExpect(status().isNotFound());

        result = mock.perform(post("/currency/remove")
                .param("id", "-1")
                .param("version", "-1"));
        result.andExpect(status().isNotFound());
    }
}
