package com.example.tripwise_api.controller;

import com.example.tripwise_api.service.CurrencyService;
import com.example.tripwise_api.util.JwtUtil;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CurrencyController.class)
@AutoConfigureMockMvc(addFilters = false)
class CurrencyControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CurrencyService currencyService;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private UserDetailsService userDetailsService;

    @Test
    void getExchangeRate_success() throws Exception {
        Mockito.when(currencyService.getExchangeRate("USD", "EUR")).thenReturn(BigDecimal.valueOf(0.9));
        mockMvc.perform(get("/api/currency/exchange-rate?from=USD&to=EUR"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.from").value("USD"))
                .andExpect(jsonPath("$.to").value("EUR"))
                .andExpect(jsonPath("$.rate").value(0.9))
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void getExchangeRate_invalidCurrency() throws Exception {
        Mockito.when(currencyService.getExchangeRate(anyString(), anyString())).thenThrow(new RuntimeException("Invalid currency"));
        mockMvc.perform(get("/api/currency/exchange-rate?from=XXX&to=YYY"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void convertCurrency_success() throws Exception {
        Mockito.when(currencyService.convertCurrency(BigDecimal.valueOf(100), "USD", "EUR"))
                .thenReturn(BigDecimal.valueOf(90));
        Mockito.when(currencyService.getExchangeRate("USD", "EUR"))
                .thenReturn(BigDecimal.valueOf(0.9));
        String requestBody = "{" +
                "\"amount\":100," +
                "\"from\":\"USD\"," +
                "\"to\":\"EUR\"}";
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/api/currency/convert")
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.originalAmount").value(100))
                .andExpect(jsonPath("$.convertedAmount").value(90))
                .andExpect(jsonPath("$.fromCurrency").value("USD"))
                .andExpect(jsonPath("$.toCurrency").value("EUR"))
                .andExpect(jsonPath("$.exchangeRate").value(0.9))
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void convertCurrency_error() throws Exception {
        Mockito.when(currencyService.convertCurrency(Mockito.any(), Mockito.anyString(), Mockito.anyString()))
                .thenThrow(new RuntimeException("Conversion error"));
        String requestBody = "{" +
                "\"amount\":100," +
                "\"from\":\"XXX\"," +
                "\"to\":\"YYY\"}";
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/api/currency/convert")
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void getSupportedCurrencies_success() throws Exception {
        java.util.Map<String, String> currencies = new java.util.LinkedHashMap<>();
        currencies.put("USD", "US Dollar");
        currencies.put("EUR", "Euro");
        currencies.put("GBP", "British Pound");
        Mockito.when(currencyService.getSupportedCurrencies()).thenReturn(currencies);
        mockMvc.perform(get("/api/currency/supported"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currencies.USD").value("US Dollar"))
                .andExpect(jsonPath("$.currencies.EUR").value("Euro"))
                .andExpect(jsonPath("$.currencies.GBP").value("British Pound"))
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void validateCurrency_supported() throws Exception {
        Mockito.when(currencyService.isCurrencySupported("USD")).thenReturn(true);
        mockMvc.perform(get("/api/currency/validate/USD"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currencyCode").value("USD"))
                .andExpect(jsonPath("$.isValid").value(true))
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void validateCurrency_unsupported() throws Exception {
        Mockito.when(currencyService.isCurrencySupported("XXX")).thenReturn(false);
        mockMvc.perform(get("/api/currency/validate/XXX"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currencyCode").value("XXX"))
                .andExpect(jsonPath("$.isValid").value(false))
                .andExpect(jsonPath("$.success").value(true));
    }
}
