package server;

import java.io.*;
import java.net.Socket;

public class FileServer {
    private static final String FILE_DIRECTORY = System.getProperty("user.dir")+"/resources/channel/";  // 파일을 저장할 디렉토리

    public static void uploadFileToServer(DataInputStream din, String channelName, String fileName) throws IOException {
        // 파일 크기 받기
        long fileSize = din.readLong();
        System.out.println("파일 업로드 시작: " + fileName);
        System.out.println("파일 크기: " + fileSize + " bytes");

        // 파일 크기가 유효하지 않으면 종료
        if (fileSize <= 0) {
            System.out.println("파일 크기가 0보다 작거나 같음. 전송된 데이터 확인 필요.");
            return;
        }

        // 파일 저장 경로 설정
        File file = new File(FILE_DIRECTORY + channelName + "/channel_files", fileName);
        File parentDir = file.getParentFile();
        if (!parentDir.exists()) {
            parentDir.mkdirs();  // 디렉토리가 없으면 생성
        }

        // 파일 저장
        try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            long bytesRemaining = fileSize;
            long totalBytesReceived = 0;

            // 파일 내용 수신
            while (bytesRemaining > 0 && (bytesRead = din.read(buffer, 0, (int) Math.min(buffer.length, bytesRemaining))) != -1) {
                fileOutputStream.write(buffer, 0, bytesRead);
                bytesRemaining -= bytesRead;
                totalBytesReceived += bytesRead;
                System.out.println("현재까지 수신된 바이트: " + totalBytesReceived + " / " + fileSize);
            }

            if (bytesRemaining > 0) {
                System.out.println("파일 업로드 중 문제가 발생했습니다. 남은 바이트: " + bytesRemaining);

            } else {
                System.out.println("파일 업로드 완료: " + fileName);

            }
        }

    }

    public static void distributeFile(DataOutputStream dout, String message, File file) throws IOException {
        // 업로드 완료 후 파일 전송
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            dout.writeUTF(message);
            System.out.println("클라이언트에게 메시지 전송: " + message);

            dout.writeLong(file.length());
            System.out.println("파일 크기 전송 완료: " + file.length() + " bytes");

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                dout.write(buffer, 0, bytesRead);

            }

            dout.flush();
            System.out.println("파일 전송 완료: " + file.getName());
        }
    }
}
