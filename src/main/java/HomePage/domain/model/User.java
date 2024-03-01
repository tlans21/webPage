package HomePage.domain.model;

import lombok.Builder;

import java.sql.Timestamp;


public class User {

    private Long id;

    private String username;

    private String password;

    private String email;
    private String role;

    private String phoneNumber;

    private Timestamp createDate;

    private Timestamp loginDate;

    private String provider;

    private String providerId;

    public User(){

    }

    @Builder
    public User(String username, String password, String email, String role, String provider, String providerId, Timestamp createDate){
       this.username = username;
       this.password = password;
       this.email = email;
       this.role = role;
       this.provider = provider;
       this.providerId = providerId;
       this.createDate = createDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Timestamp getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Timestamp createDate) {
        this.createDate = createDate;
    }

    public Timestamp getLoginDate() {
        return loginDate;
    }

    public void setLoginDate(Timestamp loginDate) {
        this.loginDate = loginDate;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getProviderId() {
        return providerId;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }



}
