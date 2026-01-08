# Security Fixes Documentation

## Overview
This document describes the security vulnerabilities identified by SonarQube and the fixes applied to resolve them.

## Issues Fixed

### 1. **CORS Configuration - Overly Permissive Origins**
**Severity:** High  
**Location:** `SecurityConfig.java` - `corsConfigurationSource()` method

**Problem:**
- Used `setAllowedOriginPatterns(List.of("*"))` which allows ALL origins with credentials
- This is a major security vulnerability that can lead to Cross-Origin attacks
- Allows any malicious website to make authenticated requests to your API

**Solution:**
- Implemented environment-specific CORS configuration
- Uses specific allowed origins from configuration properties
- Added `cors.allowed-origins` property to control allowed origins
- Test environments can use wildcard patterns for flexibility
- Production environments use explicit, whitelisted origins only

**Configuration:**
```properties
# Development/Default (application.properties)
cors.allowed-origins=http://localhost:3000,http://localhost:5173

# Production (application-prod.properties)
cors.allowed-origins=https://your-domain.com
```

---

### 2. **CSRF Protection Disabled**
**Severity:** High  
**Location:** `SecurityConfig.java` - `filterChain()` method

**Problem:**
- CSRF protection was completely disabled with `.csrf(AbstractHttpConfigurer::disable)`
- Makes the application vulnerable to Cross-Site Request Forgery attacks
- Attackers could execute unauthorized actions on behalf of authenticated users

**Solution:**
- Enabled CSRF protection with cookie-based token repository
- Configured `CookieCsrfTokenRepository.withHttpOnlyFalse()` for stateless JWT apps
- Added CSRF token to allowed/exposed headers: `X-XSRF-TOKEN`
- Ignored CSRF for authentication endpoints (login, register, refresh) as they don't require prior authentication
- Added configuration property `security.csrf.enabled` to disable in test environments only

**Implementation:**
```java
.csrf(csrf -> csrf
    .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
    .csrfTokenRequestHandler(requestHandler)
    .ignoringRequestMatchers("/api/auth/login", "/api/auth/register", "/api/auth/refresh")
)
```

**Frontend Integration Required:**
Your frontend must now include the CSRF token in requests:
1. Read the `XSRF-TOKEN` cookie
2. Include it in the `X-XSRF-TOKEN` header for state-changing requests (POST, PUT, DELETE, PATCH)

---

### 3. **Overly Permissive Authorization Rules**
**Severity:** Medium  
**Location:** `SecurityConfig.java` - `filterChain()` method

**Problem:**
- Had `.requestMatchers("/api/trips/**").permitAll()` which exposed all trip endpoints publicly
- Had `.requestMatchers("/api/auth/**").permitAll()` which exposed all auth endpoints
- Had `.requestMatchers("/actuator/**").permitAll()` which exposed all actuator endpoints

**Solution:**
- Restricted public access to only necessary authentication endpoints:
  - `/api/auth/login`
  - `/api/auth/register`
  - `/api/auth/refresh`
- Restricted actuator endpoints to only health check and info
- All other endpoints now require authentication
- Trip endpoints are protected and require valid JWT tokens

---

### 4. **Information Disclosure in Error Responses**
**Severity:** Medium  
**Location:** `GlobalErrorController.java`

**Problem:**
- Exposed detailed error messages in all environments
- Could leak sensitive information about the application structure
- Query parameters in error paths could expose sensitive data

**Solution:**
- Added environment-aware error attribute inclusion
- Production environments exclude detailed error messages
- Implemented path sanitization to remove query parameters
- Added `isProductionEnvironment()` check to control information disclosure

**Changes:**
- Only include error messages in non-production environments
- Sanitize paths to remove query parameters and fragments
- Provide user-friendly error messages without technical details

---

### 5. **Missing Security Headers**
**Severity:** Medium  
**Location:** `SecurityConfig.java` - `filterChain()` method

**Problem:**
- Missing security headers to protect against common web vulnerabilities

**Solution:**
Added comprehensive security headers:
- **Content Security Policy (CSP):** Prevents XSS attacks by controlling resource loading
- **X-Frame-Options:** Prevents clickjacking by denying iframe embedding
- **X-XSS-Protection:** Enables browser XSS filtering
- **Referrer-Policy:** Controls referrer information leakage
- **X-Content-Type-Options:** Prevents MIME-type sniffing (enabled by default)

---

## Configuration Properties Added

### application.properties
```properties
# CORS Configuration
cors.allowed-origins=${CORS_ALLOWED_ORIGINS:http://localhost:3000,http://localhost:5173}
```

### application-prod.properties
```properties
# CORS Configuration for Production - Replace with your actual frontend domain(s)
cors.allowed-origins=${CORS_ALLOWED_ORIGINS:https://your-domain.com}
```

### application-integration-test.properties
```properties
# Security & CORS configuration for tests
cors.allowed-origins=http://localhost:3000,http://localhost:5173
security.csrf.enabled=false
```

---

## Environment Variables for Production

When deploying to production, set these environment variables:

```bash
CORS_ALLOWED_ORIGINS=https://your-frontend-domain.com,https://www.your-frontend-domain.com
```

---

## Frontend Changes Required

### 1. CSRF Token Handling
Your frontend application must now handle CSRF tokens:

```javascript
// Example: Axios configuration
import axios from 'axios';

// Get CSRF token from cookie
function getCsrfToken() {
  const cookies = document.cookie.split(';');
  const csrfCookie = cookies.find(c => c.trim().startsWith('XSRF-TOKEN='));
  return csrfCookie ? csrfCookie.split('=')[1] : null;
}

// Add CSRF token to requests
axios.interceptors.request.use(config => {
  const token = getCsrfToken();
  if (token) {
    config.headers['X-XSRF-TOKEN'] = token;
  }
  return config;
});
```

### 2. Update CORS Origins
Ensure your frontend domain is included in the `cors.allowed-origins` configuration.

---

## Testing

### Run Tests
```bash
./gradlew test
```

### Run Integration Tests
```bash
./gradlew test --tests "*IntegrationTest"
```

### Verify Security Configuration
1. Check that CSRF tokens are being issued
2. Verify CORS headers are correct
3. Ensure unauthorized requests are blocked
4. Validate security headers are present in responses

---

## Additional Security Recommendations

### 1. JWT Secret Management
- Current JWT secret is hardcoded in `application.properties`
- **Recommendation:** Move to environment variable or secrets manager

```properties
jwt.secret=${JWT_SECRET:default-secret-for-dev-only}
```

### 2. Database Credentials
- Consider using environment variables for all database credentials
- Use secrets management service in production (AWS Secrets Manager, Azure Key Vault, etc.)

### 3. HTTPS Enforcement
- Ensure HTTPS is enforced in production
- Set `cookie.secure=true` in production (already configured)

### 4. Rate Limiting
- Consider implementing rate limiting for authentication endpoints
- Prevents brute force attacks

### 5. Security Auditing
- Enable security event logging
- Monitor failed authentication attempts
- Set up alerts for suspicious activity

---

## Compliance

These fixes address the following security standards:
- **OWASP Top 10:** A02:2025 - Security Misconfiguration
- **OWASP Top 10:** A01:2025 - Broken Access Control
- **OWASP Top 10:** A05:2025 - Injection (via CSP headers)
- **OWASP Top 10:** A07:2025 - Identification and Authentication Failures

---

## Version History

- **2026-01-07:** Initial security fixes applied
  - Fixed CORS configuration
  - Enabled CSRF protection
  - Restricted endpoint authorization
  - Improved error handling
  - Added security headers

