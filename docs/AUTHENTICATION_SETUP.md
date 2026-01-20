# TripWise API - Dual Token Authentication System

## Overview

The TripWise API now implements a secure dual-token authentication system using:
- **Access Tokens**: Short-lived (15 minutes) JWT tokens stored in memory by the frontend
- **Refresh Tokens**: Long-lived (30 days) JWT tokens stored in HttpOnly cookies

## Authentication Flow (Simple Explanation)

1. **Login**
   - User sends username and password to `/api/auth/login`.
   - If successful, the server returns an access token and sets a refresh token in a secure cookie.

2. **Making Requests**
   - For each API request, include the access token in the `Authorization` header:
     ```
     Authorization: Bearer <accessToken>
     ```

3. **Refreshing the Token**
   - When the access token expires, call `/api/auth/refresh`.
   - The server checks the refresh token (from the cookie) and, if valid, returns a new access token.

4. **Logout**
   - Call `/api/auth/logout` to clear the refresh token cookie and log out.

## API Endpoints

### Authentication Endpoints

| Method | Endpoint | Description | Request Body | Response |
|--------|----------|-------------|--------------|----------|
| POST | `/api/auth/login` | User login | `AuthRequest` | Access token + HttpOnly refresh cookie |
| POST | `/api/auth/register` | User registration | `RegisterRequest` | Access token + HttpOnly refresh cookie |
| POST | `/api/auth/refresh` | Refresh access token | None (uses cookie) | New access token + new refresh cookie |
| POST | `/api/auth/logout` | User logout | None | Clears refresh cookie |
| GET | `/api/auth/validate` | Validate access token | None (uses header) | Token validation status |

## Security Features

### Access Token
- **Expiration**: 15 minutes
- **Storage**: Frontend memory (localStorage/sessionStorage NOT recommended)
- **Usage**: Authorization header for API requests
- **Type**: JWT with `"type": "access"` claim

### Refresh Token
- **Expiration**: 30 days
- **Storage**: HttpOnly cookie (secure, not accessible via JavaScript)
- **Usage**: Automatic refresh when access token expires
- **Type**: JWT with `"type": "refresh"` claim


## Configuration

### JWT Settings (application.properties)
```properties
# Access token expires in 15 minutes
jwt.access-token.expiration=900000

# Refresh token expires in 30 days  
jwt.refresh-token.expiration=2592000000

```

## Security Benefits

1. **XSS Protection**: Refresh tokens in HttpOnly cookies can't be accessed by malicious JavaScript
2. **CSRF Protection**: Short-lived access tokens limit exposure window
3. **Token Rotation**: Both tokens are refreshed on each refresh call
4. **Automatic Refresh**: Seamless user experience with transparent token renewal
5. **Secure Logout**: Properly clears both tokens

## Production Considerations

1. **HTTPS**: Set `cookie.setSecure(true)` in production
   - Configurable via `cookie.secure` property
   - Default: `false` (development), `true` (production profile)
   - Environment variable: `COOKIE_SECURE`

2. **SameSite**: Configure SameSite attribute for additional CSRF protection 
   - Configurable via `cookie.same-site` property  
   - Default: `Lax` (development), `Strict` (production profile)
   - Environment variable: `COOKIE_SAME_SITE`
   - Options: `None`, `Lax`, `Strict`

3. **Domain**: Set appropriate cookie domain for your environment 
   - Configurable via `cookie.domain` property
   - Default: empty (uses request domain)
   - Environment variable: `COOKIE_DOMAIN`
   - Example: `your-domain.com`

### Environment Configuration

**Development (application.properties):**
```properties
cookie.secure=false
cookie.same-site=Lax
cookie.domain=
```

**Production (application-prod.properties):**
```properties
cookie.secure=true
cookie.same-site=Strict
cookie.domain=your-domain.com
```

### Deployment

To run with production profile:
```bash
java -jar tripwise-api.jar --spring.profiles.active=prod
```

Or set environment variable:
```bash
export SPRING_PROFILES_ACTIVE=prod
```

## Forgot Password & Password Reset

TripWise API provides a secure password reset flow for users who have forgotten their password. The process uses time-limited tokens and email verification to ensure security.

### Password Reset Flow
1. **Request Password Reset**
   - User submits their username and email to `/api/auth/forgot-password`.
   - If valid, the backend generates a password reset token (valid for 1 hour) and (optionally) sends it to the user's email.
   - For security, the response does not reveal whether the user exists.

2. **Validate Reset Token**
   - Frontend can verify a token's validity by calling `/api/auth/validate-reset-token?token=...`.
   - Returns whether the token is valid or expired.

3. **Reset Password**
   - User submits the token, new password, and confirm password to `/api/auth/reset-password`.
   - If the token is valid and passwords match, the password is updated and the token is marked as used.

### API Endpoints
| Method | Endpoint                        | Description                                 | Request Body                | Response |
|--------|----------------------------------|---------------------------------------------|-----------------------------|----------|
| POST   | `/api/auth/forgot-password`      | Request password reset                      | `{ "username", "email" }` | Success message (no user info) |
| GET    | `/api/auth/validate-reset-token` | Validate password reset token               | Query param `token`         | Valid/invalid message |
| POST   | `/api/auth/reset-password`       | Reset password using token                  | `ResetPasswordRequest`      | Success/error message |


### Security Features
- **Token Expiry**: Reset tokens expire after 1 hour.
- **Single Use**: Tokens are marked as used after a successful reset.
- **No User Disclosure**: The API does not reveal whether a username/email exists.
- **Password Encoding**: New passwords are securely encoded before saving.

### Configuration
- Token expiry is set to 1 hour in the backend (`PasswordResetService`).
- Email delivery (if implemented) should use a secure provider and template.

### Notes
- For production, ensure email delivery is enabled and secure.
- Consider rate limiting password reset requests to prevent abuse.
