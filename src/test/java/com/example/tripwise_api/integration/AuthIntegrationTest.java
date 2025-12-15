package com.example.tripwise_api.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("integration-test")
public class AuthIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void loginAndAccessProtectedEndpoint() throws Exception {
        // Create test user via registration endpoint
        Map<String, String> registerData = Map.of(
            "username", "testuser",
            "email", "test@example.com",
            "password", "testpassword123"
        );
        String registerJson = objectMapper.writeValueAsString(registerData);

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(registerJson))
                .andExpect(status().isCreated());

        // Now login with the created user
        Map<String, String> login = Map.of("username", "testuser", "password", "testpassword123");
        String loginJson = objectMapper.writeValueAsString(login);

        String loginResponse = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode node = objectMapper.readTree(loginResponse);
        String token = node.get("accessToken").asText();

        // Test protected endpoint with Bearer token
        mockMvc.perform(get("/api/users/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    public void accessProtectedWithoutToken_shouldReturnForbidden() throws Exception {
        mockMvc.perform(get("/api/users/me"))
                .andExpect(status().isForbidden());
    }

    @Test
    public void loginWithInvalidCredentials_shouldReturnUnauthorized() throws Exception {
        Map<String, String> login = Map.of("username", "invaliduser", "password", "wrongpassword");
        String loginJson = objectMapper.writeValueAsString(login);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void validateTokenEndpoint() throws Exception {
        // Register and login to get a valid token
        Map<String, String> registerData = Map.of(
            "username", "tokenuser",
            "email", "tokenuser@example.com",
            "password", "tokenpassword123"
        );
        String registerJson = objectMapper.writeValueAsString(registerData);

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(registerJson))
                .andExpect(status().isCreated());

        Map<String, String> login = Map.of("username", "tokenuser", "password", "tokenpassword123");
        String loginJson = objectMapper.writeValueAsString(login);

        String loginResponse = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode node = objectMapper.readTree(loginResponse);
        String token = node.get("accessToken").asText();

        // Test token validation endpoint
        mockMvc.perform(get("/api/auth/validate")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }
}
