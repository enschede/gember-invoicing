package app;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@SpringApplicationConfiguration(classes = App.class)
@ImportResource("classpath:application-*.xml")
@Ignore
public class AppTest {

    @Autowired
    private WebApplicationContext context;

    @Test
    public void shouldReceiveGreeting() throws Exception {
        // Given
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(context).build();

        // When
        MvcResult mvcResult =
                mockMvc.perform(get("/json").contentType(MediaType.TEXT_PLAIN))
                        .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

        // Then
        assertThat(mvcResult.getResponse().getContentAsString(), containsString("Hallo!"));
    }

}