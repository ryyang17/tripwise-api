package com.example.tripwise_api.service;

import com.example.tripwise_api.exception.CurrencyNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CurrencyServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private CurrencyService currencyService;

    @BeforeEach
    void setUp() {
        // Use reflection to inject the mocked RestTemplate
        try {
            var field = CurrencyService.class.getDeclaredField("restTemplate");
            field.setAccessible(true);
            field.set(currencyService, restTemplate);
        } catch (Exception e) {
            // If reflection fails, tests will use the real RestTemplate
        }
    }

    @Test
    void getExchangeRate_WithSameCurrency_ShouldReturnOne() {
        // Act
        BigDecimal result = currencyService.getExchangeRate("USD", "USD");

        // Assert
        assertEquals(BigDecimal.ONE, result);
        verify(restTemplate, never()).getForObject(any(String.class), any(Class.class));
    }

    @Test
    void getExchangeRate_WithValidCurrencies_ShouldReturnApiRate() {
        // Arrange
        Map<String, Object> mockResponse = Map.of(
            "rates", Map.of("EUR", 0.85)
        );
        when(restTemplate.getForObject(eq("https://api.exchangerate-api.com/v4/latest/USD"), eq(Map.class)))
            .thenReturn(mockResponse);

        // Act
        BigDecimal result = currencyService.getExchangeRate("USD", "EUR");

        // Assert
        assertEquals(new BigDecimal("0.850000"), result);
        verify(restTemplate, times(1)).getForObject(eq("https://api.exchangerate-api.com/v4/latest/USD"), eq(Map.class));
    }

    @Test
    void getExchangeRate_WhenApiThrowsException_ShouldUseFallbackRate() {
        // Arrange
        when(restTemplate.getForObject(any(String.class), eq(Map.class)))
            .thenThrow(new RestClientException("API unavailable"));

        // Act
        BigDecimal result = currencyService.getExchangeRate("USD", "EUR");

        // Assert
        assertEquals(new BigDecimal("0.850000"), result);
        verify(restTemplate, times(1)).getForObject(any(String.class), eq(Map.class));
    }

    @Test
    void getExchangeRate_WhenCurrencyNotFoundInApi_ShouldUseFallbackRate() {
        // Arrange
        Map<String, Object> mockResponse = Map.of(
            "rates", Map.of("GBP", 0.73) // EUR not included
        );
        when(restTemplate.getForObject(any(String.class), eq(Map.class)))
            .thenReturn(mockResponse);

        // Act
        BigDecimal result = currencyService.getExchangeRate("USD", "EUR");

        // Assert
        assertEquals(new BigDecimal("0.850000"), result);
        verify(restTemplate, times(1)).getForObject(any(String.class), eq(Map.class));
    }

    @Test
    void getExchangeRate_WithNullApiResponse_ShouldUseFallbackRate() {
        // Arrange
        when(restTemplate.getForObject(any(String.class), eq(Map.class)))
            .thenReturn(null);

        // Act
        BigDecimal result = currencyService.getExchangeRate("USD", "EUR");

        // Assert
        assertEquals(new BigDecimal("0.850000"), result);
        verify(restTemplate, times(1)).getForObject(any(String.class), eq(Map.class));
    }

    @Test
    void getExchangeRate_WithApiResponseMissingRates_ShouldUseFallbackRate() {
        // Arrange
        Map<String, Object> mockResponse = Map.of(
            "base", "USD"
            // missing "rates" key
        );
        when(restTemplate.getForObject(any(String.class), eq(Map.class)))
            .thenReturn(mockResponse);

        // Act
        BigDecimal result = currencyService.getExchangeRate("USD", "EUR");

        // Assert
        assertEquals(new BigDecimal("0.850000"), result);
        verify(restTemplate, times(1)).getForObject(any(String.class), eq(Map.class));
    }

    @Test
    void getExchangeRate_WithUnsupportedCurrencyInFallback_ShouldReturnOne() {
        // Arrange
        when(restTemplate.getForObject(any(String.class), eq(Map.class)))
            .thenThrow(new RestClientException("API unavailable"));

        // Act
        BigDecimal result = currencyService.getExchangeRate("XYZ", "ABC");

        // Assert
        assertEquals(new BigDecimal("1.000000"), result); // Changed from BigDecimal.ONE to match the actual precision
        verify(restTemplate, times(1)).getForObject(any(String.class), eq(Map.class));
    }

    @Test
    void convertCurrency_WithValidAmount_ShouldReturnConvertedAmount() {
        // Arrange
        when(restTemplate.getForObject(any(String.class), eq(Map.class)))
            .thenThrow(new RestClientException("Use fallback"));

        // Act
        BigDecimal result = currencyService.convertCurrency(new BigDecimal("100"), "USD", "EUR");

        // Assert
        assertEquals(new BigDecimal("85.00"), result);
        verify(restTemplate, times(1)).getForObject(any(String.class), eq(Map.class));
    }

    @Test
    void convertCurrency_WithZeroAmount_ShouldReturnZero() {
        // Act
        BigDecimal result = currencyService.convertCurrency(BigDecimal.ZERO, "USD", "EUR");

        // Assert
        assertEquals(new BigDecimal("0.00"), result);
    }

    @Test
    void convertCurrency_WithSameCurrency_ShouldReturnOriginalAmount() {
        // Act
        BigDecimal result = currencyService.convertCurrency(new BigDecimal("100"), "USD", "USD");

        // Assert
        assertEquals(new BigDecimal("100.00"), result);
        verify(restTemplate, never()).getForObject(any(String.class), any(Class.class));
    }

    @Test
    void convertCurrency_WithComplexAmount_ShouldRoundCorrectly() {
        // Arrange
        when(restTemplate.getForObject(any(String.class), eq(Map.class)))
            .thenThrow(new RestClientException("Use fallback"));

        // Act
        BigDecimal result = currencyService.convertCurrency(new BigDecimal("123.456"), "USD", "EUR");

        // Assert
        assertEquals(new BigDecimal("104.94"), result); // 123.456 * 0.85 = 104.9376, rounded to 104.94
        verify(restTemplate, times(1)).getForObject(any(String.class), eq(Map.class));
    }

    @Test
    void getSupportedCurrencies_ShouldReturnAllSupportedCurrencies() {
        // Act
        Map<String, String> result = currencyService.getSupportedCurrencies();

        // Assert
        assertNotNull(result);
        assertTrue(result.containsKey("USD"));
        assertTrue(result.containsKey("EUR"));
        assertTrue(result.containsKey("GBP"));
        assertTrue(result.containsKey("JPY"));
        assertTrue(result.containsKey("AUD"));
        assertTrue(result.containsKey("CAD"));
        assertTrue(result.containsKey("CHF"));
        assertTrue(result.containsKey("CNY"));
        assertTrue(result.containsKey("INR"));
        assertTrue(result.containsKey("KRW"));

        assertEquals("US Dollar", result.get("USD"));
        assertEquals("Euro", result.get("EUR"));
        assertEquals("British Pound", result.get("GBP"));
    }

    @Test
    void isCurrencySupported_WithSupportedCurrency_ShouldReturnTrue() {
        // Act & Assert
        assertTrue(currencyService.isCurrencySupported("USD"));
        assertTrue(currencyService.isCurrencySupported("EUR"));
        assertTrue(currencyService.isCurrencySupported("GBP"));
        assertTrue(currencyService.isCurrencySupported("JPY"));
    }

    @Test
    void isCurrencySupported_WithLowercaseCurrency_ShouldReturnTrue() {
        // Act & Assert
        assertTrue(currencyService.isCurrencySupported("usd"));
        assertTrue(currencyService.isCurrencySupported("eur"));
        assertTrue(currencyService.isCurrencySupported("gbp"));
    }

    @Test
    void isCurrencySupported_WithUnsupportedCurrency_ShouldReturnFalse() {
        // Act & Assert
        assertFalse(currencyService.isCurrencySupported("XYZ"));
        assertFalse(currencyService.isCurrencySupported("ABC"));
        assertFalse(currencyService.isCurrencySupported(""));
    }

    @Test
    void getExchangeRate_FallbackCalculation_ShouldCalculateCorrectly() {
        // Test the fallback rate calculation: EUR to GBP
        // EUR = 0.85, GBP = 0.73
        // Rate should be 0.73/0.85 = 0.858824

        // Arrange
        when(restTemplate.getForObject(any(String.class), eq(Map.class)))
            .thenThrow(new RestClientException("Use fallback"));

        // Act
        BigDecimal result = currencyService.getExchangeRate("EUR", "GBP");

        // Assert
        assertEquals(new BigDecimal("0.858824"), result);
        verify(restTemplate, times(1)).getForObject(any(String.class), eq(Map.class));
    }

    @Test
    void getExchangeRate_FallbackFromUSDtoJPY_ShouldReturnCorrectRate() {
        // USD = 1.0, JPY = 110.0
        // Rate should be 110.0/1.0 = 110.0

        // Arrange
        when(restTemplate.getForObject(any(String.class), eq(Map.class)))
            .thenThrow(new RestClientException("Use fallback"));

        // Act
        BigDecimal result = currencyService.getExchangeRate("USD", "JPY");

        // Assert
        assertEquals(new BigDecimal("110.000000"), result);
        verify(restTemplate, times(1)).getForObject(any(String.class), eq(Map.class));
    }
}
