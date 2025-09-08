package com.example.knowledgebot;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class KnowledgeBot {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Usage: java -jar knowledgebot.jar \"Your question\"");
            return;
        }

        String userQuestion = String.join(" ", args).trim();

        try {
            String policiesJson = readResourceFile("policies.json");
            String systemContext = "You are an assistant that answers questions using only the following company HR policies. " + policiesJson;

            String baseUrl = System.getenv().getOrDefault("OLLAMA_BASE_URL", "http://localhost:11434");
            String model = System.getenv().getOrDefault("OLLAMA_MODEL", "llama3");

            OllamaClient client = new OllamaClient(baseUrl, model);
            String answer = client.chatWithContext(systemContext, userQuestion);

            System.out.println("Bot: " + answer.trim());
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static String readResourceFile(String name) throws IOException {
        try (InputStream in = KnowledgeBot.class.getClassLoader().getResourceAsStream(name)) {
            if (in == null) throw new IOException("Resource not found: " + name);
            try (BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
                return br.lines().collect(Collectors.joining("\n"));
            }
        }
    }
}
