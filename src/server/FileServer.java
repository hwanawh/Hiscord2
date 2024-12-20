package server;

import java.io.*;
import java.net.Socket;

public class FileServer {
    private static String FILE_DIRECTORY = System.getProperty("user.dir")+"/resources/channel/";  // 파일을 저장할 디렉토리


    public static void handleFileUpload(DataInputStream din, DataOutputStream dout, String channelName, String fileName) throws IOException {
        FILE_DIRECTORY = FILE_DIRECTORY + channelName + "/channel_files";

        try {
            // 파일 크기 받기
            long fileSize = din.readLong();
            System.out.println("파일 업로드 시작: " + fileName);
            System.out.println("파일 크기: " + fileSize + " bytes");

            File file = new File(FILE_DIRECTORY, fileName);

            // 디렉토리 생성(존재하지 않을 경우)
            file.getParentFile().mkdirs();

            // 파일 저장
            try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                long bytesRemaining = fileSize;
                long totalBytesReceived = 0;

                while (bytesRemaining > 0 && (bytesRead = din.read(buffer, 0, (int) Math.min(buffer.length, bytesRemaining))) != -1) {
                    fileOutputStream.write(buffer, 0, bytesRead);
                    bytesRemaining -= bytesRead;
                    totalBytesReceived += bytesRead;
                    System.out.println("현재까지 수신된 바이트: " + totalBytesReceived + " / " + fileSize);
                }
            }

            System.out.println("파일 업로드 완료: " + fileName);

            // 업로드 완료 후 파일 전송
            try (FileInputStream fileInputStream = new FileInputStream(file)) {
                dout.writeUTF("/image " + fileName);  // 클라이언트에게 이미지 전송 시작 알림
                dout.writeLong(file.length());       // 파일 크기 전송

                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                    dout.write(buffer, 0, bytesRead);
                }

                dout.flush();
                System.out.println("파일 전송 완료: " + fileName);
            }
        } catch (IOException e) {
            System.err.println("파일 업로드 실패: " + e.getMessage());
            dout.writeUTF("UPLOAD FAILED: Error during file upload.");
        }
    }

    // 파일 다운로드 처리
    public static void handleFileDownload(DataInputStream din,DataOutputStream dout) throws IOException {
        try {
            // 다운로드할 파일 이름 받기
            String fileName = din.readUTF();
            File file = new File(FILE_DIRECTORY, fileName);

            if (file.exists()) {
                dout.writeUTF("DOWNLOAD SUCCESS");
                dout.writeLong(file.length());  // 파일 크기 전송

                try (FileInputStream fileInputStream = new FileInputStream(file)) {
                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                        dout.write(buffer, 0, bytesRead);
                    }
                }
            } else {
                dout.writeUTF("DOWNLOAD FAILED: File not found.");
            }
        } catch (IOException e) {
            dout.writeUTF("DOWNLOAD FAILED: Error during file download.");
            e.printStackTrace();
        }
    }
}
