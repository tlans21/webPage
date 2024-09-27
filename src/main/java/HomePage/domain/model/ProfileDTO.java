package HomePage.domain.model;

import java.sql.Timestamp;

public class ProfileDTO {
    Long userId;
    String username;
    String nickname;
    String email;

    Timestamp createdAt;
    String role;

    public ProfileDTO(Long userId, String username, String nickname, String email, Timestamp createdAt, String role) {
        this.userId = userId;
        this.username = username;
        this.nickname = nickname;
        this.email = email;
        this.createdAt = createdAt;
        this.role = role;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
