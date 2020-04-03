package transaction;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.context.support.GenericWebApplicationContext;

import com.n26.Application;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@WebAppConfiguration
public class TransactionResourcesAllLayersShould
{
    @Autowired
    private GenericWebApplicationContext webApplicationContext;

    private MockMvc mockMvc;


    @Before
    public void setUp()
    {
        mockMvc = webAppContextSetup(webApplicationContext).build();
        assertNotNull(mockMvc);
    }


    @Test
    public void store_a_new_transaction() throws JSONException, Exception
    {
        ResultActions result =
            mockMvc.perform(
                post("/transactions")
                    .content(new JSONObject().put("timestamp", Instant.now().toString()).put("amount", new BigDecimal("150.353")).toString())
                    .contentType(MediaType.APPLICATION_JSON_UTF8));

        MockHttpServletResponse response =
            result
                .andReturn()
                .getResponse();

        assertThat(response, is(notNullValue()));
        assertThat(HttpStatus.valueOf(response.getStatus()), is(equalTo(HttpStatus.CREATED)));
    }


    @Test
    public void delete_all_existing_transactions() throws JSONException, Exception
    {
        ResultActions result =
            mockMvc.perform(
                delete("/transactions"));

        MockHttpServletResponse response =
            result
                .andReturn()
                .getResponse();

        assertThat(response, is(notNullValue()));
        assertThat(HttpStatus.valueOf(response.getStatus()), is(equalTo(HttpStatus.OK)));
    }


    @Test
    public void return_204_when_transaction_is_older_than_60_seconds() throws JSONException, Exception
    {
        ResultActions result =
            mockMvc.perform(
                post("/transactions")
                    .content(new JSONObject().put("timestamp", Instant.now().minus(70, ChronoUnit.SECONDS).toString()).put("amount", new BigDecimal("150.353")).toString())
                    .contentType(MediaType.APPLICATION_JSON_UTF8));

        MockHttpServletResponse response =
            result
                .andReturn()
                .getResponse();

        assertThat(response, is(notNullValue()));
        assertThat(HttpStatus.valueOf(response.getStatus()), is(equalTo(HttpStatus.NO_CONTENT)));
    }


    @Test
    public void return_422_when_transaction_is_in_the_furure() throws JSONException, Exception
    {
        ResultActions result =
            mockMvc.perform(
                post("/transactions")
                    .content(new JSONObject().put("timestamp", Instant.now().plus(70, ChronoUnit.SECONDS).toString()).put("amount", new BigDecimal("150.353")).toString())
                    .contentType(MediaType.APPLICATION_JSON_UTF8));

        MockHttpServletResponse response =
            result
                .andReturn()
                .getResponse();

        assertThat(response, is(notNullValue()));
        assertThat(HttpStatus.valueOf(response.getStatus()), is(equalTo(HttpStatus.UNPROCESSABLE_ENTITY)));
    }


    @Test
    public void return_422_when_ammount_is_not_parseable() throws JSONException, Exception
    {
        ResultActions result =
            mockMvc.perform(
                post("/transactions")
                    .content(new JSONObject().put("timestamp", Instant.now().plus(70, ChronoUnit.SECONDS).toString()).put("amount", "A lot of money").toString())
                    .contentType(MediaType.APPLICATION_JSON_UTF8));

        MockHttpServletResponse response =
            result
                .andReturn()
                .getResponse();

        assertThat(response, is(notNullValue()));
        assertThat(HttpStatus.valueOf(response.getStatus()), is(equalTo(HttpStatus.UNPROCESSABLE_ENTITY)));
    }
}
