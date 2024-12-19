package server;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FileServer {
    private static final String FILE_DIRECTORY = "server_files/";  // 파일을 저장할 디렉토리
    private static ExecutorService pool = Executors.newFixedThreadPool(10);  // 클라이언트 처리 스레드 풀
    private static Socket clientSocket;  // 클라이언트 소켓

    // setSocket 메서드를 통해 클라이언트의 소켓을 설정
    public static void setSocket(Socket socket) {
        clientSocket = socket;
    }

    // 클라이언트로부터 파일을 처리하는 메서드
    public static void handleFileRequest() {
        if (clientSocket == null) {
            System.out.println("Client socket is not set.");
            return;
        }

        try (InputStream inputStream = clientSocket.getInputStream();
             OutputStream outputStream = clientSocket.getOutputStream()) {

            // 데이터 스트림을 사용하여 클라이언트와 데이터를 주고받기
            DataInputStream dataInputStream = new DataInputStream(inputStream);
            String request = dataInputStream.readUTF();  // 클라이언트의 요청 확인 (업로드/다운로드)

            if (request.equals("UPLOAD")) {
                handleFileUpload(dataInputStream);
            } else if (request.equals("DOWNLOAD")) {
                handleFileDownload(outputStream, dataInputStream);
            } else {
                System.out.println("Invalid request: " + request);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 파일 업로드 처리
    private static void handleFileUpload(DataInputStream dataInputStream) {
        try {
            String fileName = dataInputStream.readUTF();  // 파일 이름 받기
            long fileSize = dataInputStream.readLong();  // 파일 크기 받기

            File file = new File(FILE_DIRECTORY, fileName);  // 저장할 파일 객체 생성
            try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                long bytesRemaining = fileSize;
                while (bytesRemaining > 0 && (bytesRead = dataInputStream.read(buffer, 0, (int) Math.min(buffer.length, bytesRemaining))) != -1) {
                    fileOutputStream.write(buffer, 0, bytesRead);
                    bytesRemaining -= bytesRead;
                }
                System.out.println("File uploaded successfully: " + fileName);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 파일 다운로드 처리
    private static void handleFileDownload(OutputStream outputStream, DataInputStream dataInputStream) {
        try {
            String fileName = dataInputStream.readUTF();  // 다운로드할 파일 이름 받기
            File file = new File(FILE_DIRECTORY, fileName);

            if (file.exists()) {
                DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
                dataOutputStream.writeUTF("SUCCESS");
                dataOutputStream.writeLong(file.length());

                try (FileInputStream fileInputStream = new FileInputStream(file)) {
                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }
                }
                System.out.println("File downloaded successfully: " + fileName);
            } else {
                DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
                dataOutputStream.writeUTF("FILE_NOT_FOUND");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
