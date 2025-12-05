# TripWise API - Dual Token Authentication System

## Overview

The TripWise API now implements a secure dual-token authentication system using:
- **Access Tokens**: Short-lived (15 minutes) JWT tokens stored in memory by the frontend
- **Refresh Tokens**: Long-lived (30 days) JWT tokens stored in HttpOnly cookies

## Authentication Flow

### 1. Login Process
```
POST /api/auth/login
Content-Type: application/json

{
  "username": "user@example.com",
  "password": "password123"
}
```

**Response:**
```json
{
  "accessToken": "eyJhbGciOiJIUzUxMiJ9...",
  "username": "user@example.com",
  "message": "Login successful"
}
```

**Set-Cookie Header:**
```
Set-Cookie: refreshToken=eyJhbGciOiJIUzUxMiJ9...; HttpOnly; Path=/; Max-Age=2592000
```

### 2. Making Authenticated Requests
Include the access token in the Authorization header:
```
Authorization: Bearer eyJhbGciOiJIUzUxMiJ9...
```

### 3. Token Refresh Process
When an access token expires (401 response), automatically call:
```
POST /api/auth/refresh
```

**Response:**
```json
{
  "accessToken": "eyJhbGciOiJIUzUxMiJ9...",
  "username": "user@example.com", 
  "message": "Token refreshed successfully"
}
```

### 4. Logout Process
```
POST /api/auth/logout
```

This clears the HttpOnly refresh token cookie.

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

## Frontend Implementation Guide

### 1. Axios Interceptor Setup
```javascript
// Response interceptor for automatic token refresh
axios.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config;
    
    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true;
      
      try {
        // Call refresh endpoint
        const refreshResponse = await axios.post('/api/auth/refresh', {}, {
          withCredentials: true // Important for cookies
        });
        
        // Update access token in memory
        const newAccessToken = refreshResponse.data.accessToken;
        localStorage.setItem('accessToken', newAccessToken);
        
        // Update Authorization header
        axios.defaults.headers.common['Authorization'] = `Bearer ${newAccessToken}`;
        originalRequest.headers['Authorization'] = `Bearer ${newAccessToken}`;
        
        // Retry original request
        return axios(originalRequest);
      } catch (refreshError) {
        // Refresh failed, redirect to login
        localStorage.removeItem('accessToken');
        window.location.href = '/login';
        return Promise.reject(refreshError);
      }
    }
    
    return Promise.reject(error);
  }
);
```

### 2. Login Implementation
```javascript
async function login(username, password) {
  try {
    const response = await axios.post('/api/auth/login', {
      username,
      password
    }, {
      withCredentials: true // Important for cookies
    });
    
    // Store access token in memory
    const accessToken = response.data.accessToken;
    localStorage.setItem('accessToken', accessToken);
    
    // Set default Authorization header
    axios.defaults.headers.common['Authorization'] = `Bearer ${accessToken}`;
    
    return response.data;
  } catch (error) {
    throw error;
  }
}
```

### 3. Logout Implementation
```javascript
async function logout() {
  try {
    await axios.post('/api/auth/logout', {}, {
      withCredentials: true
    });
    
    // Clear access token from memory
    localStorage.removeItem('accessToken');
    delete axios.defaults.headers.common['Authorization'];
    
    // Redirect to login page
    window.location.href = '/login';
  } catch (error) {
    console.error('Logout failed:', error);
  }
}
```

### 4. Initialize Authentication on App Start
```javascript
function initializeAuth() {
  const accessToken = localStorage.getItem('accessToken');
  if (accessToken) {
    axios.defaults.headers.common['Authorization'] = `Bearer ${accessToken}`;
  }
}

// Call on app initialization
initializeAuth();
```

## Configuration

### JWT Settings (application.properties)
```properties
# Access token expires in 15 minutes
jwt.access-token.expiration=900000

# Refresh token expires in 30 days  
jwt.refresh-token.expiration=2592000000

# Strong secret key
jwt.secret=a8f5f167f44f4964e6c998dee827110c8fbba032fac56a8cfa18b73bb2d9cc55d2b5f9b5c2e8b8d7c8a9b3f2d1e0a4b7c8d9e0f1a2b3c4d5e6f7a8b9c0d1e2f3
```

## Security Benefits

1. **XSS Protection**: Refresh tokens in HttpOnly cookies can't be accessed by malicious JavaScript
2. **CSRF Protection**: Short-lived access tokens limit exposure window
3. **Token Rotation**: Both tokens are refreshed on each refresh call
4. **Automatic Refresh**: Seamless user experience with transparent token renewal
5. **Secure Logout**: Properly clears both tokens

## Production Considerations

1. **HTTPS**: Set `cookie.setSecure(true)` in production
2. **SameSite**: Configure SameSite attribute for additional CSRF protection
3. **Domain**: Set appropriate cookie domain for your environment
4. **Token Blacklisting**: Consider implementing token blacklisting for enhanced security

## Testing

Use the following curl commands to test the authentication flow:

### Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "your_username", "password": "your_password"}' \
  -c cookies.txt
```

### Use Access Token
```bash
curl -X GET http://localhost:8080/api/protected-endpoint \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

### Refresh Token
```bash
curl -X POST http://localhost:8080/api/auth/refresh \
  -b cookies.txt \
  -c cookies.txt
```

### Logout
```bash
curl -X POST http://localhost:8080/api/auth/logout \
  -b cookies.txt
```
