package co.vivo.chatservice.wrapper;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Message {
    private String sender;
    private String content;
    private String mediaUrl;

    public Message(String sender, String content, String mediaUrl) {
        this.sender = sender;
        this.content = content;
        this.mediaUrl = mediaUrl;
    }

    public String getSender() {
        return sender;
    }

    public String getContent() {
        return content;
    }

    public String getMediaUrl() {
        return mediaUrl;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setMediaUrl(String mediaUrl) {
        this.mediaUrl = mediaUrl;
    }

    @Override
    public String toString() {
        return "Message [sender=" + sender + ", content=" + content + ", mediaUrl=" + mediaUrl + "]";
    }
}
