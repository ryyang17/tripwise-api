package com.example.tripwise_api.controller;

import com.example.tripwise_api.service.CurrencyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/currency")
@CrossOrigin(origins = "*")
public class CurrencyController {
    private final CurrencyService currencyService;
    private static final String SUCCESS_KEY = "success";

    public CurrencyController(CurrencyService currencyService) {
        this.currencyService = currencyService;
    }


     // Get exchange rate between two currencies

    @GetMapping("/exchange-rate")
    public ResponseEntity<Map<String, Object>> getExchangeRate(
            @RequestParam String from,
            @RequestParam String to) {

        try {
            BigDecimal rate = currencyService.getExchangeRate(from.toUpperCase(), to.toUpperCase());

            Map<String, Object> response = new HashMap<>();
            response.put("from", from.toUpperCase());
            response.put("to", to.toUpperCase());
            response.put("rate", rate);
            response.put(SUCCESS_KEY, true);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put(SUCCESS_KEY, false);
            errorResponse.put("error", "Failed to get exchange rate: " + e.getMessage());

            return ResponseEntity.badRequest().body(errorResponse);
        }
    }


     // Convert amount between currencies

    @PostMapping("/convert")
    public ResponseEntity<Map<String, Object>> convertCurrency(@RequestBody Map<String, Object> request) {
        try {
            BigDecimal amount = new BigDecimal(request.get("amount").toString());
            String fromCurrency = request.get("from").toString().toUpperCase();
            String toCurrency = request.get("to").toString().toUpperCase();

            BigDecimal convertedAmount = currencyService.convertCurrency(amount, fromCurrency, toCurrency);
            BigDecimal exchangeRate = currencyService.getExchangeRate(fromCurrency, toCurrency);

            Map<String, Object> response = new HashMap<>();
            response.put("originalAmount", amount);
            response.put("convertedAmount", convertedAmount);
            response.put("fromCurrency", fromCurrency);
            response.put("toCurrency", toCurrency);
            response.put("exchangeRate", exchangeRate);
            response.put(SUCCESS_KEY, true);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put(SUCCESS_KEY, false);
            errorResponse.put("error", "Failed to convert currency: " + e.getMessage());

            return ResponseEntity.badRequest().body(errorResponse);
        }
    }


     // Get list of supported currencies

    @GetMapping("/supported")
    public ResponseEntity<Map<String, Object>> getSupportedCurrencies() {
        Map<String, Object> response = new HashMap<>();
        response.put("currencies", currencyService.getSupportedCurrencies());
        response.put(SUCCESS_KEY, true);

        return ResponseEntity.ok(response);
    }


     // Validate currency code

    @GetMapping("/validate/{currencyCode}")
    public ResponseEntity<Map<String, Object>> validateCurrency(@PathVariable String currencyCode) {
        Map<String, Object> response = new HashMap<>();
        boolean isValid = currencyService.isCurrencySupported(currencyCode);

        response.put("currencyCode", currencyCode.toUpperCase());
        response.put("isValid", isValid);
        response.put(SUCCESS_KEY, true);

        return ResponseEntity.ok(response);
    }
}
