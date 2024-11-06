package co.vivo.chatservice.dto;
/**
 * DTO for updating user preferences.
 */
public class UserPreferencesDto {

    private String readReceipt;
    private boolean muteNotifications;
    private boolean hideSeenStatus;

    public UserPreferencesDto() {
    }

    public String getReadReceipt() {
        return readReceipt;
    }

    public void setReadReceipt(String readReceipt) {
        this.readReceipt = readReceipt;
    }

    public boolean isMuteNotifications() {
        return muteNotifications;
    }

    public void setMuteNotifications(boolean muteNotifications) {
        this.muteNotifications = muteNotifications;
    }

    public boolean isHideSeenStatus() {
        return hideSeenStatus;
    }

    public void setHideSeenStatus(boolean hideSeenStatus) {
        this.hideSeenStatus = hideSeenStatus;
    }
}
