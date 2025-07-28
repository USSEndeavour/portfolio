package com.crypto.portfolio.web;

import com.crypto.portfolio.services.CurrencyService;
import com.crypto.portfolio.utils.currencies.CurrencyTicker;
import org.hamcrest.Matchers;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.crypto.portfolio.entities.Currency;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@SpringBootTest(webEnvironment = WebEnvironment.MOCK)
@AutoConfigureMockMvc
public class CurrencyControllerTest {

    @MockitoBean
    private CurrencyService service;

    @Autowired
    private MockMvc mvc;

    private Currency currencyEur = new Currency();
    private Currency currencyUsd = new Currency();

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        currencyEur.setTicker(CurrencyTicker.valueOf("EUR"));
        currencyEur.setId(9);
        when(service.addCurrency(currencyEur)).thenReturn(currencyEur);
        when(service.getCurrencyById(9)).thenReturn(Optional.of(currencyEur));

        currencyUsd.setTicker(CurrencyTicker.valueOf("USD"));
        currencyUsd.setId(2);
        when(service.getCurrencyByTicker(CurrencyTicker.valueOf("USD"))).thenReturn(Optional.of(currencyUsd));

        List<Currency> currencies = new ArrayList<>();
        currencies.add(currencyEur);
        currencies.add(currencyUsd);

        when(service.getCurrencyById(1)).thenReturn(Optional.empty());

        when(service.getAllCurrencies()).thenReturn(currencies);
    }

    @Test
    public void testAddNewCurrency() throws Exception {
        mvc.perform(post("/currencies/add")
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(currencyEur))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    public void testGetCurrencyById() throws Exception {
        mvc.perform(get("/currencies/{id}", 9))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("{\"ticker\":\"EUR\"}"));
    }

    @Test
    public void testGetCurrencyByTicker() throws Exception {
        mvc.perform(get("/currencies/ticker/{ticker}", "USD"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("{\"ticker\":\"USD\"}"))
                .andExpect(jsonPath("$.ticker", Matchers.is("USD")));
    }

    @Test
    public void testGetAllCurrencies() throws Exception {
        mvc.perform(get("/currencies"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("[{\"ticker\":\"EUR\"}, {\"ticker\":\"USD\"}]"));
    }

    @Test
    public void testDeleteCurrencyById() throws Exception {
        mvc.perform(delete("/currencies/delete/{id}", 9))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testDeleteCurrencyByIdNotExist() throws Exception {
        mvc.perform(delete("/currencies/delete/{id}", 3))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetCurrencyByIdNotExist() throws Exception {
        mvc.perform(get("/currencies/{id}", 1))
                .andExpect(status().isNotFound())
                .andExpect(content().string("CurrencyNotFound"));
    }

    @Test
    public void testGetCurrencyByTickerNotExist() throws Exception {
        mvc.perform(get("/currencies/ticker/{ticker}", "UAH"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("CurrencyNotFound"));
    }

    @Test
    public void testGetCurrencyWrongUrl() throws Exception {
        mvc.perform(get("/currenCIES/{id}", 1))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void testPostCurrencyTickerAlreadyExists() throws Exception {
        when(service.getCurrencyByTicker(CurrencyTicker.valueOf("EUR"))).thenReturn(Optional.of(currencyEur));
        mvc.perform(post("/currencies/add")
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(currencyEur))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());
    }
}
