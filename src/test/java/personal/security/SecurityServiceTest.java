package personal.security;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashSet;
import java.util.Set;

import org.junit.Ignore;
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

import personal.spring.SpringConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { SpringConfig.class })
@WebAppConfiguration
public class SecurityServiceTest {

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private MockHttpSession session;

    @Ignore
    @Test
    public void testSecurity() throws Exception {

        MockMvc mock = MockMvcBuilders.webAppContextSetup(wac).build();

        ResultActions result;
        result = mock.perform(get("/security/passport").session(session));
        result.andExpect(status().isForbidden());

        

        result = mock
                .perform(post("/security/signin").param("password", "admin1")
                        .param("username", "admin").session(session));
        result.andExpect(status().isForbidden());
        
        signin(mock,session);

        result = mock.perform(get("/security/passport").session(session));
        result.andExpect(status().isOk());

        // result = mock.perform(post("/security/change").session(session)
        // .param("newPass", "admin1").param("oldPass", "admin"));
        // result.andExpect(status().isOk());
        //
        // result = mock.perform(post("/security/change").session(session)
        // .param("newPass", "admin1").param("oldPass", "admin"));
        // result.andExpect(status().isForbidden());
        //
        // result = mock.perform(post("/security/change").session(session)
        // .param("newPass", "admin").param("oldPass", "admin1"));
        // result.andExpect(status().isOk());

        result = mock.perform(post("/security/signout").session(session));
        result.andExpect(status().isOk());

        result = mock.perform(get("/security/passport").session(session));
        result.andExpect(status().isForbidden());
    }

    //private static final Set<MockHttpSession> SESSIONS=new HashSet<>();
    
    
    public static void signin(MockMvc mock, MockHttpSession session)
            throws Exception {
        ResultActions result;
        result = mock
                .perform(post("/security/signin").param("password", "admin")
                        .param("username", "admin").session(session));
        result.andExpect(status().isOk());
    }
}
