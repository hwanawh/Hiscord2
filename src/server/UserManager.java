package server;

import models.User;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class UserManager {

    private static List<User> users = new ArrayList<>(); // static으로 변경하여 클래스 변수로 관리

    static {
        // 정적 초기화 블록에서 사용자 정보 로드
        String projectDir = System.getProperty("user.dir");
        loadUsersFromFile(projectDir + "/resources/user.txt");
    }

    // 사용자 목록을 파일에서 읽어오기
    private static void loadUsersFromFile(String filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] userInfo = line.split(",");
                String name = userInfo[0].trim();
                String id = userInfo[1].trim();
                String password = userInfo[2].trim();
                String profileUrl = (userInfo.length > 3) ? userInfo[3].trim() : null;

                // User 객체 생성 후 리스트에 추가
                User user = new User(name, id, password, profileUrl);
                users.add(user);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 로그인 인증 처리
    public static User authenticateUser(String id, String password) {
        for (User user : users) {
            if (user.getId().equals(id) && user.getPassword().equals(password)) {
                return user; // 인증 성공
            }
        }
        return null; // 인증 실패
    }

    // 사용자 목록 반환
    public static List<User> getUsers() {
        return users;
    }

    // id로 사용자 검색
    public static User getUserById(String id) {
        for (User user : users) {
            if (user.getId().equals(id)) {
                return user;
            }
        }
        return null; // 사용자 없음
    }
}
