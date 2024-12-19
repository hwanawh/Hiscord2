package models;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class UserManager {

    private static List<User> users = new ArrayList<>(); // static으로 관리

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
                users.add(new User(name, id, password, profileUrl));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 사용자 정보를 파일에 저장
    private static void saveUsersToFile(String filePath) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
            for (User user : users) {
                bw.write(user.getName() + "," + user.getId() + "," + user.getPassword() +
                        (user.getProfileUrl() != null ? "," + user.getProfileUrl() : ""));
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 로그인 인증 처리
    public static boolean authenticateUser(String id, String password) {
        for (User user : users) {
            if (user.getId().equals(id) && user.getPassword().equals(password)) {
                return true; // 인증 성공
            }
        }
        return false; // 인증 실패
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

    // id 중복 체크
    public static boolean isUserIdDuplicated(String id) {
        for (User user : users) {
            if (user.getId().equals(id)) {
                return true; // 중복됨
            }
        }
        return false; // 중복되지 않음
    }

    // 사용자 추가
    public static boolean addUser(String name, String id, String password, String profileUrl) {
        if (isUserIdDuplicated(id)) {
            return false; // 중복된 ID
        }

        // 새로운 사용자 추가
        User newUser = new User(name, id, password, profileUrl);
        users.add(newUser);

        // 파일에 저장
        String projectDir = System.getProperty("user.dir");
        saveUsersToFile(projectDir + "/resources/user.txt");

        return true;
    }

    // 사용자 목록 반환
    public static List<User> getUsers() {
        return users;
    }
}
