//ClientHandler.java

package server;

import models.Channel;
import models.User;
import models.UserManager;

import java.io.*;

public class ClientHandler implements Runnable {
    private DataInputStream din;
    private DataOutputStream dout;
    private String currentChannel;
    private User loggedUser;

    public ClientHandler(DataInputStream din, DataOutputStream dout) throws IOException {
        this.din = din;
        this.dout = dout;
        handleJoin("channel1");
    }

    @Override
    public void run() {
        try {
            String message;
            while ((message = din.readUTF()) != null) {
                if (message.startsWith("/")) { //핸들 메시지
                    handleCommand(message);
                } else if (message.startsWith(":")) { //File처리
                    handleFileCommand(message);
                } else {
                    // 일반 메시지 처리
                    ChannelManager.broadcast(currentChannel, ": " + message);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            cleanup();
        }
    }

    private void handleCommand(String message) throws IOException {
        String command = message.split(" ", 2)[0].trim();
        String argument = message.substring(command.length()).trim();

        switch (command) {
            case "/join":
                handleJoin(argument);
                break;
            case "/login":
                handleLogin(argument);
                break;
            case "/signup":
                handleSignup(argument);
                break;
            case "/updateUserInfo":
                handleUpdateUserInfo(argument);  // 새 명령어 처리
                break;
            case "/addchannel":
                handleAddChannel(argument);
                break;
            case "/updateNotice":
                handleUpdateNotice(argument);
                break;
            case "/message":
                handleMessage(argument);
                break;
            default:
                dout.writeUTF("Unknown command: " + command);
        }
    }

    public void handleMessage(String argument) throws IOException {
        String message = loggedUser.getProfileUrl()+","+loggedUser.getName()+","+argument;
        //message example hiscord.png , 황준선 , 12:06 , 안녕하세요
        ChannelManager.broadcast(currentChannel, message);
        //broadcast;
        FileServer.handleFileUpload(din,dout,currentChannel,message);
    }

    private void handleFileCommand(String message) throws IOException {
        String command = message.split(" ", 2)[0].trim();
        String argument = message.substring(command.length()).trim();

        switch (command) {
            case ":UPLOAD_PROFILE":

                break;
            case ":UPLOAD_IMAGE":
                FileServer.handleFileUpload(din,dout,currentChannel,argument);
                //ChannelManager.broadcastImage(currentChannel,argument);
                break;
            case ":UPLOAD_FILE":

                break;
            case ":DOWNLOAD":

                break;
            case ":UPLOAD_INFO":

                break;

            default:
                dout.writeUTF("Unknown command: " + command);
        }
    }
    public void sendMessage(String message) throws IOException {
        System.out.println(message);
        dout.writeUTF("/message " + message); //message =
        //dout.writelong();
    }

    private void handleJoin(String newChannel) throws IOException {
        ChannelManager.leaveChannel(currentChannel, this);
        currentChannel = newChannel;
        ChannelManager.joinChannel(currentChannel, this);
        dout.writeUTF("/join " + currentChannel);
    }

    private void handleLogin(String argument) throws IOException {
        String[] credentials = argument.split("/", 2);
        if (credentials.length == 2) {
            String id = credentials[0].trim();
            String password = credentials[1].trim();

            if (UserManager.authenticateUser(id, password)) {
                this.loggedUser = UserManager.getUserById(id);
                dout.writeUTF(loggedUser != null ? loggedUser.getName() : "없는 아이디입니다");
            } else {
                dout.writeUTF("Login Failed: Invalid credentials");
            }
        } else {
            dout.writeUTF("Login Failed: Incorrect command format");
        }
    }

    private void handleSignup(String argument) throws IOException {
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
    }

    private void handleUpdateUserInfo(String argument) throws IOException {
        String[] updatedInfo = argument.split("/", 2);
        if (updatedInfo.length == 2) {
            String newName = updatedInfo[0].trim();
            String newPassword = updatedInfo[1].trim();

            if (loggedUser != null) {
                loggedUser.setName(newName);
                loggedUser.setPassword(newPassword);

                // 수정된 사용자 정보를 UserManager에 저장
                UserManager.saveUsersToFile(System.getProperty("user.dir") + "/resources/user.txt");

                dout.writeUTF("정보가 성공적으로 수정되었습니다.");
            } else {
                dout.writeUTF("유저 정보 수정 실패: 로그인된 사용자가 없습니다.");
            }
        } else {
            dout.writeUTF("잘못된 정보 형식입니다.");
        }
    }

    private void handleAddChannel(String channelName) throws IOException {
        channelName = channelName.trim();
        if (!channelName.isEmpty()) {
            String projectDir = System.getProperty("user.dir");
            String path = projectDir + "/resources/channel/" + channelName;
            File channelDir = new File(path);

            if (!channelDir.exists() && channelDir.mkdir()) {
                ChannelManager.addChannel(channelName);
                dout.writeUTF("채널 '" + channelName + "'이 생성되었습니다.");
            } else {
                dout.writeUTF(channelDir.exists() ? "이미 존재하는 채널입니다." : "채널 생성 실패.");
            }
        } else {
            dout.writeUTF("채널 이름을 입력하세요.");
        }
    }

    private void handleUpdateNotice(String newNotice) throws IOException {
        newNotice = newNotice.trim();
        if (!newNotice.isEmpty()) {
            InfoManager infoManager = new InfoManager();
            infoManager.updateNotice(currentChannel, newNotice);
            dout.writeUTF("공지사항이 업데이트되었습니다.");
        } else {
            dout.writeUTF("새로운 공지사항을 입력하세요.");
        }
    }

    private void cleanup() {
        ChannelManager.leaveChannel(currentChannel, this);
    }
}
