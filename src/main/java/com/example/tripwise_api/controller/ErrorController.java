package com.example.tripwise_api.controller;

import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.ServletWebRequest;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
public class ErrorController implements org.springframework.boot.web.servlet.error.ErrorController {

	private final ErrorAttributes errorAttributes;

	public ErrorController(ErrorAttributes errorAttributes) {
		this.errorAttributes = errorAttributes;
	}

	@RequestMapping("/error")
	public ResponseEntity<Map<String, Object>> handleError(HttpServletRequest request) {
		ServletWebRequest webRequest = new ServletWebRequest(request);
		ErrorAttributeOptions options = ErrorAttributeOptions.defaults().including(ErrorAttributeOptions.Include.MESSAGE);
		Map<String, Object> attrs = errorAttributes.getErrorAttributes(webRequest, options);

		int status = 500;
		Object statusObj = attrs.get("status");
		if (statusObj instanceof Integer) {
			status = (Integer) statusObj;
		} else if (statusObj instanceof Number) {
			status = ((Number) statusObj).intValue();
		}

		Map<String, Object> body = new HashMap<>();
		body.put("timestamp", attrs.get("timestamp"));
		body.put("status", status);
		body.put("error", attrs.getOrDefault("error", "N/A"));
		body.put("message", attrs.getOrDefault("message", "No message available"));
		body.put("path", attrs.getOrDefault("path", request.getRequestURI()));

		return ResponseEntity.status(status).body(body);
	}

}
