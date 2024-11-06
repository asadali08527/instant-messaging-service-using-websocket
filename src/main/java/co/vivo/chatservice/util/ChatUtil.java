package co.vivo.chatservice.util;

public class ChatUtil {
    public static String extractToken(String query) {
        if (query != null && !query.isEmpty()) {
            // Parse the token from the query string (e.g., ?token=abc123)
            for (String param : query.split("&")) {
                String[] keyValue = param.split("=");
                if (keyValue.length == 2 && "token".equals(keyValue[0])) {
                    return keyValue[1];
                }
            }
        }
        return null;
    }
}
