package models;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class User {
    private String name;
    private String id;
    private String password;
    private String profileUrl; // 프로필 사진

    // profileUrl을 선택적 인자로 변경
    public User(String name, String id, String password) {
        this.name = name;
        this.id = id;
        this.password = password;
        this.profileUrl ="\\client_resources\\default.png"; // profileUrl 기본값을 null로 설정
    }

    // profileUrl을 포함한 생성자 (선택적)
    public User(String name, String id, String password, String profileUrl) {
        this.name = name;
        this.id = id;
        this.password = password;
        this.profileUrl = profileUrl;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public void setName(String name) { this.name = name; }

    public void setPassword(String password) { this.password = password; }

    public String getPassword() {
        return password;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    // 프로필 사진 URL을 변경할 때 사용하는 메서드
    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }

}
