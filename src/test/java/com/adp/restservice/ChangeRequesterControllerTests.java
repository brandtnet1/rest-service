package com.adp.restservice;

import com.adp.restservice.api.ChangeRequesterController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.hateoas.MediaTypes.HAL_JSON;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class ChangeRequesterControllerTests {

    @Autowired
    private ChangeRequesterController controller;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void contextLoads() {
        assertThat(controller).isNotNull();
    }

    @Test
    void getBankInfo() throws Exception {
        this.mockMvc
                .perform(MockMvcRequestBuilders.get("/bank"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(HAL_JSON))
                .andDo(log())
                .andExpect(jsonPath("$.pennies").value("100"))
                .andExpect(jsonPath("$.nickels").value("100"))
                .andExpect(jsonPath("$.dimes").value("100"))
                .andExpect(jsonPath("$.quarters").value("100"))
                .andExpect(jsonPath("$.totalValue").value("41.0"))
                .andExpect(jsonPath("$._links.self.href").value("http://localhost/bank"))
        ;
    }

    @Test
    void makeValidExchange() throws Exception {
        this.mockMvc
                .perform(MockMvcRequestBuilders.post("/bank/exchange;one=1;two=1;five=1;ten=1;twenty=1;fifty=0;hundred=0;"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(HAL_JSON))
                .andDo(log())
                .andExpect(jsonPath("$.success").value("true"))
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.coins.PENNY").value("0"))
                .andExpect(jsonPath("$.coins.NICKEL").value("60"))
                .andExpect(jsonPath("$.coins.DIME").value("100"))
                .andExpect(jsonPath("$.coins.QUARTER").value("100"))
                .andExpect(jsonPath("$._links.BankInfo.href").value("http://localhost/bank"))
                .andExpect(jsonPath("$._links.self.href").value("http://localhost/bank/exchange{;one}{;two}{;five}{;ten}{;twenty}{;fifty}{;hundred}"))
        ;
    }

    @Test
    void exchangeMoreThanBankHolds() throws Exception {
        this.mockMvc
                .perform(MockMvcRequestBuilders.post("/bank/exchange;one=1;two=1;five=1;ten=1;twenty=1;fifty=1;hundred=1;"))
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentTypeCompatibleWith(HAL_JSON))
                .andDo(log())
                .andExpect(jsonPath("$.success").value("false"))
                .andExpect(jsonPath("$.message").value("The Bank doesn't have enough coins to fulfil this request. The bank has: $41.0"))
                .andExpect(jsonPath("$._links.BankInfo.href").value("http://localhost/bank"))
                .andExpect(jsonPath("$._links.self.href").value("http://localhost/bank/exchange{;one}{;two}{;five}{;ten}{;twenty}{;fifty}{;hundred}"))
        ;
    }

    @Test
    void multipleRequests() throws Exception {
        this.mockMvc
                .perform(MockMvcRequestBuilders.post("/bank/exchange;one=1;two=1;five=1;ten=1;twenty=1;fifty=0;hundred=0;"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.coins.PENNY").value("0"))
                .andExpect(jsonPath("$.coins.NICKEL").value("60"))
                .andExpect(jsonPath("$.coins.DIME").value("100"))
                .andExpect(jsonPath("$.coins.QUARTER").value("100"))
        ;

        this.mockMvc
                .perform(MockMvcRequestBuilders.get("/bank"))
                .andExpect(jsonPath("$.pennies").value("100"))
                .andExpect(jsonPath("$.nickels").value("40"))
                .andExpect(jsonPath("$.dimes").value("0"))
                .andExpect(jsonPath("$.quarters").value("0"))
                .andExpect(jsonPath("$.totalValue").value("3.0"))
        ;

        this.mockMvc
                .perform(MockMvcRequestBuilders.post("/bank/exchange;one=1;two=1;five=0;ten=0;twenty=0;fifty=0;hundred=0;"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.coins.PENNY").value("100"))
                .andExpect(jsonPath("$.coins.NICKEL").value("40"))
                .andExpect(jsonPath("$.coins.DIME").value("0"))
                .andExpect(jsonPath("$.coins.QUARTER").value("0"))
        ;
    }
}
