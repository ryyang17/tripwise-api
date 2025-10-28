package com.example.tripwise_api.service;

import com.example.tripwise_api.exception.CurrencyNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.HashMap;

@Service
public class CurrencyService {

    private static final Logger logger = LoggerFactory.getLogger(CurrencyService.class);
    private final RestTemplate restTemplate = new RestTemplate();

    // Fallback exchange rates for when API is unavailable
    private final Map<String, BigDecimal> fallbackRates = Map.of(
        "USD", BigDecimal.ONE,
        "EUR", new BigDecimal("0.85"),
        "GBP", new BigDecimal("0.73"),
        "JPY", new BigDecimal("110.0"),
        "AUD", new BigDecimal("1.35"),
        "CAD", new BigDecimal("1.25")
    );

    public BigDecimal getExchangeRate(String fromCurrency, String toCurrency) {
        if (fromCurrency.equals(toCurrency)) {
            return BigDecimal.ONE;
        }

        try {
            // Try to get real-time rate from external API
            return getExchangeRateFromApi(fromCurrency, toCurrency);
        } catch (RestClientException e) {
            logger.warn("Failed to get exchange rate from API, using fallback rates: {}", e.getMessage());
            return getFallbackExchangeRate(fromCurrency, toCurrency);
        } catch (CurrencyNotFoundException e) {
            logger.warn("Currency not found in API response, using fallback rates: {}", e.getMessage());
            return getFallbackExchangeRate(fromCurrency, toCurrency);
        }
    }

    // Convert amount from one currency to another
    public BigDecimal convertCurrency(BigDecimal amount, String fromCurrency, String toCurrency) {
        BigDecimal exchangeRate = getExchangeRate(fromCurrency, toCurrency);
        return amount.multiply(exchangeRate).setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal getExchangeRateFromApi(String fromCurrency, String toCurrency) {
        // using external api
        String url = String.format("https://api.exchangerate-api.com/v4/latest/%s", fromCurrency);

        @SuppressWarnings("unchecked")
        Map<String, Object> response = restTemplate.getForObject(url, Map.class);

        if (response != null && response.containsKey("rates")) {
            @SuppressWarnings("unchecked")
            Map<String, Object> rates = (Map<String, Object>) response.get("rates");

            if (rates.containsKey(toCurrency)) {
                Number rate = (Number) rates.get(toCurrency);
                return new BigDecimal(rate.toString()).setScale(6, RoundingMode.HALF_UP);
            }
        }

        throw new CurrencyNotFoundException("Currency not found in API response: " + toCurrency);
    }

    // get fallback exchange rate when API is unavailable
    private BigDecimal getFallbackExchangeRate(String fromCurrency, String toCurrency) {
        BigDecimal fromRate = fallbackRates.getOrDefault(fromCurrency, BigDecimal.ONE);
        BigDecimal toRate = fallbackRates.getOrDefault(toCurrency, BigDecimal.ONE);

        // Convert through USD as base currency
        return toRate.divide(fromRate, 6, RoundingMode.HALF_UP);
    }

    // get all supported currencies
    public Map<String, String> getSupportedCurrencies() {
        Map<String, String> currencies = new HashMap<>();
        currencies.put("USD", "US Dollar");
        currencies.put("EUR", "Euro");
        currencies.put("GBP", "British Pound");
        currencies.put("JPY", "Japanese Yen");
        currencies.put("AUD", "Australian Dollar");
        currencies.put("CAD", "Canadian Dollar");
        currencies.put("CHF", "Swiss Franc");
        currencies.put("CNY", "Chinese Yuan");
        currencies.put("INR", "Indian Rupee");
        currencies.put("KRW", "South Korean Won");
        return currencies;
    }

    // validate if currency code is supported
    public boolean isCurrencySupported(String currencyCode) {
        return getSupportedCurrencies().containsKey(currencyCode.toUpperCase());
    }
}
