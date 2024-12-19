package server;

import models.User;

import java.io.*;
import java.net.Socket;
import java.util.*;

public class ClientHandler implements Runnable {
    private Socket socket;
    private DataInputStream din;
    private DataOutputStream dout;
    String username;
    private String currentChannel = "channel1";

    public ClientHandler(DataInputStream din, DataOutputStream dout) {
        this.din = din;
        this.dout = dout;
    }

    @Override
    public void run() {
        try {
            String message;
            while ((message = din.readUTF()) != null) { // join시
                if (message.startsWith("/")) {
                    // 명령어 추출 (첫 번째 단어)
                    String command = message.split(" ", 2)[0].trim();
                    String argument = message.substring(command.length()).trim(); // 명령어 이후의 내용

                    switch (command) {
                        case "/join":
                            String newChannel = argument; // '/join ' 이후의 내용
                            ChannelManager.leaveChannel(currentChannel, this);
                            currentChannel = newChannel;
                            ChannelManager.joinChannel(currentChannel, this);
                            dout.writeUTF("/join " + currentChannel);
                            break;

                        case "/login":
                            // argument는 "ID/PASSWORD" 형태
                            String[] credentials = argument.split("/", 2);
                            if (credentials.length == 2) {
                                String id = credentials[0].trim();
                                String password = credentials[1].trim();

                                // UserManager의 로그인 인증
                                if (UserManager.authenticateUser(id, password)) {
                                    User user = UserManager.getUserById(id); // User 객체 반환
                                    if (user != null) {
                                        // 인증 성공: 사용자 이름 반환
                                        dout.writeUTF(user.getName());
                                    } else {
                                        // 인증 실패: 사용자 정보 없음
                                        dout.writeUTF("없는 아이디입니다");
                                    }
                                } else {
                                    // 인증 실패
                                    dout.writeUTF("Login Failed: Invalid credentials");
                                }
                            } else {
                                // 잘못된 명령어 형식 처리
                                dout.writeUTF("Login Failed: Incorrect command format");
                            }
                            break;

                        case "/signup":
                            String[] userInfo = argument.split("/", 4);
                            if (userInfo.length == 4) {
                                String name = userInfo[0].trim();
                                String id = userInfo[1].trim();
                                String password = userInfo[2].trim();
                                String profileUrl = userInfo[3].trim();

                                if (UserManager.addUser(name, id, password, profileUrl)) {
                                    dout.writeUTF("회원가입 성공");
                                } else {
                                    dout.writeUTF("이미 사용중인 아이디입니다");
                                }
                            } else {
                                dout.writeUTF("올바르게 입력하세요");
                            }
                            break;

                        case "/addchannel":
                            String channelName = argument.trim();
                            if (!channelName.isEmpty()) {
                                // 폴더 생성
                                String projectDir = System.getProperty("user.dir");
                                String path = projectDir + "/resources/channel/" + channelName;
                                File channelDir = new File(path);

                                if (!channelDir.exists()) {
                                    if (channelDir.mkdir()) {
                                        // 채널 생성 성공
                                        ChannelManager.addChannel(channelName);
                                        dout.writeUTF("채널 '" + channelName + "'이 생성되었습니다.");
                                    } else {
                                        dout.writeUTF("채널 생성 실패.");
                                    }
                                } else {
                                    dout.writeUTF("이미 존재하는 채널입니다.");
                                }
                            } else {
                                dout.writeUTF("채널 이름을 입력하세요.");
                            }
                            break;

                        case "/UPLOAD":
                            FileServer.handleFileUpload(din, dout);
                            break;

                        case "/updateNotice":
                            // /updateNotice <newNotice>
                            String newNotice = argument.trim();
                            if (!newNotice.isEmpty()) {
                                // InfoManager를 통해 공지사항을 업데이트
                                InfoManager infoManager = new InfoManager();
                                infoManager.updateNotice(currentChannel, newNotice);
                                dout.writeUTF("공지사항이 업데이트되었습니다.");
                            } else {
                                dout.writeUTF("새로운 공지사항을 입력하세요.");
                            }
                            break;

                        default:
                            dout.writeUTF("Unknown command: " + command);
                            break;
                    }
                } else {
                    // 일반 메시지인 경우
                    ChannelManager.broadcast(currentChannel, username + ": " + message);
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
                ChannelManager.leaveChannel(currentChannel, this);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendMessage(String message) throws IOException {
        dout.writeUTF(message);
    }
}
