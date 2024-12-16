package models;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class User {
    private String name;
    private String id;
    private String password;

    public User(String name, String id, String password) {
        this.name = name;
        this.id = id;
        this.password = password;
    }

    public String getId() {
        return id;
    }

    public String getPassword() {
        return password;
    }

    public static boolean isValidUser(String id, String password) {
        try (BufferedReader br = new BufferedReader(new FileReader("resources/user.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] userInfo = line.split(",");
                String savedName = userInfo[0].trim();
                String savedId = userInfo[1].trim();
                String savedPassword = userInfo[2].trim();

                if (savedId.equals(id) && savedPassword.equals(password)) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
