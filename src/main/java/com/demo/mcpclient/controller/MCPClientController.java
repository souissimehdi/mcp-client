package com.demo.mcpclient.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;


@RestController
@RequestMapping("/api")
public class MCPClientController {

    private final ChatClient chatClient;

    public MCPClientController(ChatClient.Builder chatClientBuilder, ToolCallbackProvider toolCallbackProvider) {
        this.chatClient = chatClientBuilder.defaultTools(toolCallbackProvider)
                .defaultAdvisors(new SimpleLoggerAdvisor())
                .build();
    }

    @GetMapping("/chat")
    public String chat(@RequestHeader(value = "username", required = false) String username,
                       @RequestParam("message") String message) {
        return chatClient.prompt().user(message+ " My username is " + username)
                .toolContext(Map.of("progressToken", UUID.randomUUID().toString()))
                .call().content();
    }

    @GetMapping("/summarize-tickets")
    public String summarizeTickets(@RequestHeader("username") String username) {
        return chatClient.prompt()
                .system("""
                        You orchestrate the 'summarizeTickets' tool. The tool already returns a complete,
                        customer-ready summary that was generated for this exact request. Return that tool
                        output to the user EXACTLY as-is: do not rewrite, reformat, shorten, expand,
                        rephrase, or add any commentary of your own. Your reply must be the verbatim tool
                        response and nothing else.
                        """)
                .user("Summarize all of my support tickets. My username is " + username)
                .toolContext(Map.of("progressToken", UUID.randomUUID().toString()))
                .call().content();
    }

}