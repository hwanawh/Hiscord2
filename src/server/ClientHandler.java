//ClientHandler.java

package server;

import models.Channel;
import models.User;
import models.UserManager;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ClientHandler implements Runnable {
    private DataInputStream din;
    private DataOutputStream dout;
    private String currentChannel;
    private User loggedUser;

    public ClientHandler(DataInputStream din, DataOutputStream dout) throws IOException {
        this.din = din;
        this.dout = dout;
    }

    @Override
    public void run() {
        try {
            String message;
            while ((message = din.readUTF()) != null) {
                if (message.startsWith("/"))  //핸들 메시지
                    handleCommand(message);
                else
                    System.out.println("din unknown message : "+message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            cleanup();
        }
    }
    //커맨드 관리
    private void handleCommand(String message) throws IOException {
        String command = message.split(" ", 2)[0].trim();
        String argument = message.substring(command.length()).trim();

        switch (command) {
            case "/join":
                handleJoin(argument);
                sendInfo(argument);
                break;
            case "/login":
                handleLogin(argument);
                break;
            case "/signup":
                handleSignup(argument);
                break;
            case "/info":
                handleInfo(argument);
                break;
            case "/updateUserInfo":
                handleUpdateUserInfo(argument);  // 새 명령어 처리
                break;
            case "/addchannel":
                handleAddChannel(argument);
                break;
            case "/leave":
                handleLeave(argument);
                break;
            case "/message":
                handleMessage(argument);
                break;
            case "/chatload":
                chatLoad(argument);
            case "/memberLoad":
                memberLoad();
            default:
                dout.writeUTF("Unknown command: " + command);
        }
    }

    //공지 수정 및 저장
    public void handleInfo(String argument) throws IOException {

        String[] parts = argument.split(" ", 2);

        // 첫 번째 부분은 채널명, 나머지는 내용
        String channelName = parts[0];
        String content = parts.length > 1 ? parts[1] : "";

        System.out.println("Channel Name: " + channelName);
        System.out.println("Content: " + content);
        // .txt 파일로 저장 (덮어쓰는 방식)
        try (FileWriter writer = new FileWriter(System.getProperty("user.dir") + "/resources/channel/" + channelName + "/info.txt")) {
            writer.write(content);
            System.out.println("Content saved to " + channelName + "/info.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }

        // broadcast
        ChannelManager.broadcastInfo(currentChannel, argument);
    }

    //수정된 공지 보내기
    public void sendInfo(String argument) throws IOException {
        String filePath = System.getProperty("user.dir") + "/resources/channel/" + currentChannel + "/info.txt";
        StringBuilder infoMessage = new StringBuilder("/info ").append(argument).append("\n");
        // 파일 내용 읽기
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                infoMessage.append(line).append("\n");
            }
        } catch (IOException e) {
            infoMessage.append("");
        }

        // UTF-8 텍스트 메시지 전송
        dout.writeUTF(infoMessage.toString().trim());
    }

    //접속 멤버 관리
    public void memberLoad() throws IOException {
        String profileUrl = loggedUser.getProfileUrl();
        String name = loggedUser.getName();
        String message = profileUrl+","+name;
        ChannelManager.broadcastMember(currentChannel,message);
    }
    //멤버 전송
    public void sendMember(String message) throws IOException {
        dout.writeUTF("/memberLoad "+message);
    }
    //채널의 채팅 내역 불러오기
    public void chatLoad(String channelName) throws IOException {
        String chatPath = System.getProperty("user.dir") + "/resources/channel/" + channelName + "/chats.txt";
        List<String[]> chatMessages = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(chatPath))) {
            String line;

            while ((line = reader.readLine()) != null) {
                System.out.println("Reading line: " + line); // 디버깅 출력
                String[] parts = line.split(",", -1); // -1 옵션으로 빈 항목도 배열에 포함


                if (parts.length < 4) {
                    System.out.println("Warning: Unexpected line format. Skipping this line.");
                    continue; // 배열 크기가 부족한 경우 무시
                }

                String filePathImage = parts[3].isEmpty() ? null : parts[3]; // 빈 값은 null 처리
                chatMessages.add(new String[]{
                        parts[0].trim(), // ID
                        parts[1].trim(), // Timestamp
                        parts[2].trim(), // Text message
                        filePathImage // File path or null
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (String[] message : chatMessages) {
            String id = message[0];
            System.out.println("Processing ID: " + id); // 디버깅 출력
            User user = UserManager.getUserById(id);

            if (user == null) {
                System.out.println("Warning: User not found for ID: " + id);
                continue; // 유효하지 않은 ID는 무시
            }

            String profileUrl = user.getProfileUrl();
            String senderName = user.getName();
            String timestamp = message[1];
            String textMessage = message[2];
            String filePathImage = message[3]; // 이미 null 처리됨

            String formattedMessage = "/message " + profileUrl + "," + senderName + "," + timestamp + "," +
                    (textMessage.isEmpty() ? "null" : textMessage) + "," +
                    (filePathImage == null ? "null" : filePathImage);
            System.out.println("Formatted message: " + formattedMessage);
            sendMessage(formattedMessage);
        }
    }



    //메시지 관리 서버 업로드 및 브로드캐스트
    public void handleMessage(String argument) throws IOException {
        String message = loggedUser.getProfileUrl()+","+loggedUser.getName()+","+argument;
        String chatMessage = message.substring(9).trim();
        String[] parts = chatMessage.split(",");
        String timestamp = parts[2];
        String greeting = parts[3];
        String filename = parts.length > 4 ? parts[4] : null;
        String savedMessage = loggedUser.getId()+","+timestamp+","+greeting+","+filename;
        // 파일에 savedMessage 저장
        String filePath = System.getProperty("user.dir") + "/resources/channel/" + currentChannel + "/chats.txt";// 저장할 파일 경로
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            writer.write(savedMessage);
            writer.newLine(); // 메시지 뒤에 줄 바꿈 추가
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(filename!=null){
            FileServer.uploadFileToServer(din,currentChannel,filename);
        }
        message = "/message "+message;
        //message example hiscord.png,황준선,12:06,안녕하세요,filename(nullable)
        ChannelManager.broadcast(currentChannel,message);
        //broadcast;
        //FileServer.handleFileUpload(din,dout,currentChannel,message);
    }
    //메시지 전송 사진,이모지,채팅
    public void sendMessage(String message) throws IOException {
        // \profiles\Hiscord.png,주환수씨,2024-12-21 22:42:46,dd
        String chatMessage = message.substring(9).trim();
        String[] parts = chatMessage.split(",");
        String filename = parts.length > 4 ? parts[4] : null;

        if(filename!=null && !filename.equals("null")){
            File file = new File(System.getProperty("user.dir")+"/resources/channel/"+currentChannel+"/channel_files/"+filename);
            FileServer.distributeFile(dout,message,file);
            System.out.println(message);
            return;
        }
        System.out.println("서버가 클라이언트에게 전송"+message);
        dout.writeUTF(message);
    }
    //로그인관리
    private void handleJoin(String newChannel) throws IOException {
        ChannelManager.leaveChannel(currentChannel, this);
        currentChannel = newChannel;
        ChannelManager.joinChannel(currentChannel, this);
        dout.writeUTF("/join " + currentChannel);
    }

    private void handleLogin(String argument) throws IOException {
        String[] credentials = argument.split("/", 2);
        String id;
        if (credentials.length == 2) {
            id = credentials[0].trim();
            String password = credentials[1].trim();

            if (UserManager.authenticateUser(id, password)) {
                loggedUser = UserManager.getUserById(id);
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
                loggedUser=UserManager.getUserById(id);
            } else {
                dout.writeUTF("이미 사용중인 아이디입니다");
            }
        } else {
            dout.writeUTF("올바르게 입력하세요");
        }
    }


    private void handleUpdateUserInfo(String argument) throws IOException {
        String[] updatedInfo = argument.split("/", 3); // 세 개로 나눔
        if (updatedInfo.length == 3) {
            String newName = updatedInfo[0].trim();
            String newPassword = updatedInfo[1].trim();
            String newProfileUrl = updatedInfo[2].trim();
            String oldName = loggedUser.getName();
            String oldProfileUrl = loggedUser.getProfileUrl();


            newProfileUrl = "\\" + newProfileUrl;

            if (loggedUser != null) {
                loggedUser.setName(newName);
                loggedUser.setPassword(newPassword);
                loggedUser.setProfileUrl(newProfileUrl); // 프로필 URL 업데이트

                // 수정된 사용자 정보를 UserManager에 저장
                UserManager.saveUsersToFile(System.getProperty("user.dir") + "/resources/user.txt");

                dout.writeUTF("/delete "+oldName);
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

    private void handleLeave(String channelName) throws IOException {
        if (currentChannel.equals(channelName)) {
            ChannelManager.leaveChannel(currentChannel, this);
            dout.writeUTF("/leave " + channelName);

            // 채널 디렉토리 삭제
            String projectDir = System.getProperty("user.dir");
            String path = projectDir + "/resources/channel/" + channelName;
            File channelDir = new File(path);

            if (channelDir.exists()) {
                deleteDirectory(channelDir);
                dout.writeUTF("채널 '" + channelName + "'이 삭제되었습니다.");
            } else {
                dout.writeUTF("채널 '" + channelName + "'이 존재하지 않습니다.");
            }

            currentChannel = null; // 채널을 나갔으므로 현재 채널을 null로 설정
        } else {
            dout.writeUTF("현재 채널과 일치하지 않는 채널입니다.");
        }
    }

    private void cleanup() {
        ChannelManager.leaveChannel(currentChannel, this);
    }

    // 디렉토리와 그 안의 파일을 재귀적으로 삭제하는 메서드
    private void deleteDirectory(File dir) {
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteDirectory(file); // 재귀적으로 하위 디렉토리 삭제
                } else {
                    file.delete(); // 파일 삭제
                }
            }
        }
        dir.delete(); // 최종적으로 빈 디렉토리 삭제
    }
}
