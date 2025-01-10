package com.example.AIR_backend.service;

import com.example.AIR_backend.model.ChatResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Map;

@Service
public class LangflowService {
    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    @Value("${langflow.flow-id}")
    private String flowIdOrName;

    @Value("${langflow.langflow-id}")
    private String langflowId;

    public LangflowService(WebClient webClient, ObjectMapper objectMapper) {
        this.webClient = webClient;
        this.objectMapper = objectMapper;
    }

    // Single implementation of runLangflow
    public ChatResponse runLangflow(String inputValue, String inputType, String outputType, Map<String, Object> tweaks, boolean stream) {
        String endpoint = String.format("/lf/%s/api/v1/run/%s?stream=%s", langflowId, flowIdOrName, stream);

        Map<String, Object> requestBody = Map.of(
                "input_value", inputValue,
                "input_type", inputType,
                "output_type", outputType,
                "tweaks", tweaks
        );

        try {
            String response = webClient.post()
                    .uri(endpoint)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            JsonNode rootNode = objectMapper.readTree(response);
            JsonNode messageNode = rootNode
                    .path("outputs")
                    .path(0)
                    .path("outputs")
                    .path(0)
                    .path("results")
                    .path("message")
                    .path("data");

            ChatResponse cleanResponse = new ChatResponse();
            cleanResponse.setMessage(messageNode.path("text").asText());
            cleanResponse.setTimestamp(messageNode.path("timestamp").asText());
            cleanResponse.setSender(messageNode.path("sender").asText());
            cleanResponse.setError(messageNode.path("error").asBoolean());

            return cleanResponse;

        } catch (WebClientResponseException e) {
            ChatResponse errorResponse = new ChatResponse();
            errorResponse.setError(true);
            errorResponse.setMessage("Error response from Langflow API: " + e.getResponseBodyAsString());
            return errorResponse;
        } catch (Exception e) {
            ChatResponse errorResponse = new ChatResponse();
            errorResponse.setError(true);
            errorResponse.setMessage("Error connecting to Langflow API: " + e.getMessage());
            return errorResponse;
        }
    }
}