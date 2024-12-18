package client;

import client.ui.LoginFrame;
import client.ui.MainFrame;

import java.io.IOException;

public class ChatClient {
    public static void main(String[] args) throws IOException {
        //new MainFrame("admin");
        try {
            new LoginFrame();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    } //login 화면
}
