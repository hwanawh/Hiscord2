package server;

import java.io.*;
import java.net.Socket;

public class FileServer implements Runnable {
    private static final String FILE_DIRECTORY = "server_files/";  // 파일을 저장할 디렉토리
    private Socket clientSocket;
    private DataInputStream din;
    private DataOutputStream dout;

    public FileServer(DataInputStream din,DataOutputStream dout) throws IOException {
        this.clientSocket = clientSocket;
        this.din=din;
        this.dout=dout;
    }

    @Override
    public void run() {
        try {
            String request;
            while ((request = din.readUTF()) != null) {  // 클라이언트의 요청 처리
                if (request.startsWith("UPLOAD")) {
                    handleFileUpload();
                } else if (request.startsWith("DOWNLOAD")) {
                    handleFileDownload();
                } else {
                    dout.writeUTF("Unknown command: " + request);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleFileUpload() throws IOException {
        try {
            // 파일 이름 받기
            String fileName = din.readUTF();
            long fileSize = din.readLong();  // 파일 크기 받기

            // 디버그: 파일 이름과 크기 출력
            System.out.println("파일 업로드 시작: " + fileName);
            System.out.println("파일 크기: " + fileSize + " bytes");

            File file = new File(FILE_DIRECTORY, fileName);

            try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                long bytesRemaining = fileSize;
                long totalBytesReceived = 0;  // 수신된 총 바이트

                while (bytesRemaining > 0 && (bytesRead = din.read(buffer, 0, (int) Math.min(buffer.length, bytesRemaining))) != -1) {
                    fileOutputStream.write(buffer, 0, bytesRead);
                    bytesRemaining -= bytesRead;
                    totalBytesReceived += bytesRead;

                    // 디버그: 전송된 바이트 수 출력
                    System.out.println("현재까지 수신된 바이트: " + totalBytesReceived + " / " + fileSize);
                }

                // 디버그: 업로드 완료 메시지
                System.out.println("파일 업로드 완료: " + fileName);
                dout.writeUTF("UPLOAD SUCCESS: File uploaded successfully.");
            }
        } catch (IOException e) {
            // 디버그: 에러 메시지
            System.err.println("파일 업로드 실패: " + e.getMessage());
            e.printStackTrace();
            dout.writeUTF("UPLOAD FAILED: Error during file upload.");
        }
    }


    // 파일 다운로드 처리
    private void handleFileDownload() throws IOException {
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
