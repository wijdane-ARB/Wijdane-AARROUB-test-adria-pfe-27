package ma.enset.security.chatbotservice;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    public record ChatRequest(String message) {}
    public record ChatResponse(String reply) {}

    private final ChatClient chatClient;

    public ChatController(ChatClient chatClient) { this.chatClient = chatClient; }

    @PostMapping
    public ChatResponse chat(@RequestBody ChatRequest request) {
        String reply = chatClient.prompt().user(request.message()).call().content();
        return new ChatResponse(reply);
    }
}
