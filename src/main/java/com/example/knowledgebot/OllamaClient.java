package com.example.knowledgebot;

import java.io.IOException;
import java.net.URI;
import java.net.http.*;
import java.time.Duration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.*;

public class OllamaClient {
    private final HttpClient http;
    private final ObjectMapper mapper = new ObjectMapper();
    private final String baseUrl;
    private final String model;

    public OllamaClient(String baseUrl, String model) {
        this.baseUrl = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
        this.model = model;
        this.http = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();
    }

    public String chatWithContext(String systemContext, String userMessage) throws IOException, InterruptedException {
        ObjectNode root = mapper.createObjectNode();
        root.put("model", model);
        root.put("stream", false);

        ArrayNode messages = mapper.createArrayNode();
        messages.add(mapper.createObjectNode()
                .put("role", "system")
                .put("content", systemContext));
        messages.add(mapper.createObjectNode()
                .put("role", "user")
                .put("content", userMessage));
        root.set("messages", messages);

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/chat"))
                .timeout(Duration.ofSeconds(120))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(root)))
                .build();

        HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());
        if (resp.statusCode() / 100 != 2) {
            throw new IOException("HTTP " + resp.statusCode() + ": " + resp.body());
        }

        JsonNode json = mapper.readTree(resp.body());

        JsonNode message = json.path("message");
        if (!message.isMissingNode() && message.has("content")) {
            return message.get("content").asText();
        }

        JsonNode response = json.path("response");
        if (!response.isMissingNode()) {
            return response.asText();
        }

        JsonNode choices = json.path("choices");
        if (choices.isArray() && choices.size() > 0) {
            JsonNode cmsg = choices.get(0).path("message").path("content");
            if (!cmsg.isMissingNode()) return cmsg.asText();
        }

        return resp.body();
    }
}
