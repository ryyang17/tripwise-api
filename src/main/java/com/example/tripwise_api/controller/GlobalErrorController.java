package com.example.tripwise_api.controller;

import org.springframework.beans.factory.annotation.Value;
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
public class GlobalErrorController implements org.springframework.boot.web.servlet.error.ErrorController {

	private final ErrorAttributes errorAttributes;

	@Value("${spring.profiles.active:default}")
	private String activeProfile;

	public GlobalErrorController(ErrorAttributes errorAttributes) {
		this.errorAttributes = errorAttributes;
	}

	@RequestMapping("/error")
	public ResponseEntity<Map<String, Object>> handleError(HttpServletRequest request) {
		ServletWebRequest webRequest = new ServletWebRequest(request);

		// Only include message in non-production environments, exclude stack trace for security
		ErrorAttributeOptions.Include[] includes = isProductionEnvironment()
			? new ErrorAttributeOptions.Include[]{}
			: new ErrorAttributeOptions.Include[]{ErrorAttributeOptions.Include.MESSAGE};

		ErrorAttributeOptions options = ErrorAttributeOptions.defaults().including(includes);
		Map<String, Object> attrs = errorAttributes.getErrorAttributes(webRequest, options);

		int status = 500;
		Object statusObj = attrs.get("status");
		if (statusObj instanceof Integer) {
			status = (Integer) statusObj;
		} else if (statusObj instanceof Number) {
			status = ((Number) statusObj).intValue();
		}

		// Build user-friendly messages for common errors
		String path = (String) attrs.getOrDefault("path", request.getRequestURI());
		String originalMessage = String.valueOf(attrs.getOrDefault("message", ""));
		String friendlyMessage = getFriendlyMessage(status, path, originalMessage);

		Map<String, Object> body = new HashMap<>();
		body.put("timestamp", attrs.get("timestamp"));
		body.put("status", status);
		body.put("error", attrs.getOrDefault("error", "Error"));
		body.put("message", friendlyMessage);
		body.put("path", sanitizePath(path));

		return ResponseEntity.status(status).body(body);
	}

	private boolean isProductionEnvironment() {
		return "prod".equals(activeProfile) || "production".equals(activeProfile);
	}

	/**
	 * Sanitize path to prevent information disclosure
	 */
	private String sanitizePath(String path) {
		if (path == null) {
			return "/";
		}
		// Remove query parameters and fragments that might contain sensitive data
		int queryIndex = path.indexOf('?');
		if (queryIndex > 0) {
			path = path.substring(0, queryIndex);
		}
		return path;
	}

	private String getFriendlyMessage(int status, String path, String originalMessage) {
        return switch (status) {
            case 401 -> {
                if (originalMessage.toLowerCase().contains("bad credentials") ||
                        originalMessage.toLowerCase().contains("invalid username or password")) {
                    yield "Invalid username or password. Please check and try again.";
                }
                yield "Authentication required. Please log in with valid credentials.";
            }
            case 403 -> "Access denied. Please make sure you're logged in and have permission to access this resource.";
            case 404 -> {
                if (path != null && path.contains("/api/auth/")) {
                    yield "Authentication endpoint not found. Please check the URL and use the correct HTTP method.";
                }
                yield String.format("The requested resource '%s' was not found. Please check the URL.", path);
            }
            case 400 -> {
                if (originalMessage.toLowerCase().contains("validation")) {
                    yield "Invalid input data. Please check your request and try again.";
                }
                yield "Bad request. Please check your input and try again.";
            }
            case 500 -> "An internal server error occurred. Please try again later or contact support.";
            default -> originalMessage != null && !originalMessage.isEmpty() ?
                    originalMessage : "An unexpected error occurred. Please try again.";
        };
	}
}
