package com.example.AIR_backend.Controller;

import com.example.AIR_backend.model.ChatResponse;
import com.example.AIR_backend.service.LangflowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/langflow")
@CrossOrigin(origins = "https://bot-y109.onrender.com", allowCredentials = "true")
public class LangflowController {

    @Autowired
    private LangflowService langflowService;

    @PostMapping("/run-flow")
    public ResponseEntity<ChatResponse> runFlow(@RequestBody Map<String, Object> requestBody) {
        try {
            String inputValue = (String) requestBody.get("inputValue");
            String inputType = (String) requestBody.getOrDefault("inputType", "chat");
            String outputType = (String) requestBody.getOrDefault("outputType", "chat");
            boolean stream = (boolean) requestBody.getOrDefault("stream", false);
            Map<String, Object> tweaks = (Map<String, Object>) requestBody.getOrDefault("tweaks", Map.of());

            ChatResponse response = langflowService.runLangflow(inputValue, inputType, outputType, tweaks, stream);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            ChatResponse errorResponse = new ChatResponse();
            errorResponse.setError(true);
            errorResponse.setMessage(e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
}