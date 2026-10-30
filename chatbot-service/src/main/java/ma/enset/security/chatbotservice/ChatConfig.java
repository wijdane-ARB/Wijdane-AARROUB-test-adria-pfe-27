package ma.enset.security.chatbotservice;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatConfig {

    @Bean
    public ChatClient chatClient(ChatClient.Builder builder, ToolCallbackProvider mcpTools) {
        return builder
                .defaultSystem("""
                        Tu es un assistant bancaire. Tu aides les utilisateurs a consulter les comptes,
                        enregistrer des transactions et effectuer des virements.
                        Utilise TOUJOURS les outils disponibles pour obtenir ou modifier les donnees bancaires.
                        N'invente jamais un identifiant, un solde ou un montant.
                        Si un identifiant de compte (UUID) ou un montant manque, demande-le a l'utilisateur.
                        Reponds en francais, de maniere concise.
                        """)
                .defaultToolCallbacks(mcpTools)   // outils exposes par les serveurs MCP des 2 micro-services
                .build();
    }
}
