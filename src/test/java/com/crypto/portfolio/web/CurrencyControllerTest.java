package com.crypto.portfolio.web;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.crypto.portfolio.repositories.CurrencyRepository;
import com.crypto.portfolio.entities.Currency;

import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest(webEnvironment = WebEnvironment.MOCK)
@AutoConfigureMockMvc
public class CurrencyControllerTest {

    @MockitoBean
    private CurrencyRepository repository;

    @Autowired
    private MockMvc mvc;

    private Currency currency = new Currency();

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setUp() {
        currency.setId(9);
        currency.setTicker("EUR");
    }

    @Test
    public void testCreateAndGetCurrency() throws Exception {
        mvc.perform(post("/currencies/add")
                .content(objectMapper.writeValueAsString(currency))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }
}
