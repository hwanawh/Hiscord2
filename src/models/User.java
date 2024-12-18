package models;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class User {
    private String name;
    private String id;
    private String password;
    private String profileUrl; // 프로필 사진을 위한 필드 추가

    // profileUrl을 선택적 인자로 변경
    public User(String name, String id, String password) {
        this.name = name;
        this.id = id;
        this.password = password;
        this.profileUrl = null; // profileUrl 기본값을 null로 설정
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

    // 유저 정보를 확인하는 메서드는 별도의 방식으로 처리할 수 있습니다.
    public static boolean isValidUser(String id, String password) {
        // 파일에서 사용자 정보 확인 로직을 제거하고, 다른 방식으로 유효성 검사를 할 수 있도록 변경 필요
        // 예: 데이터베이스에서 확인하거나 다른 방식으로 사용자 검증을 할 수 있음.
        return false; // 현재는 검증 로직이 없으므로 false 반환
    }
}
