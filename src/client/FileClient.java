package client;

import java.io.*;
import java.net.Socket;

public class FileClient {
    private static Socket socket;
    private static DataInputStream dataIn;
    private static DataOutputStream dataOut;

    // 소켓 설정 메서드
    public static void setSocket(Socket sharedSocket) throws IOException {
        socket = sharedSocket;
        dataIn = new DataInputStream(socket.getInputStream());
        dataOut = new DataOutputStream(socket.getOutputStream());
    }

    public static void uploadFile(String filePath) {
        if (socket == null || dataOut == null) {
            System.err.println("소켓이 초기화되지 않았습니다. setSocket()을 호출하세요.");
            return;
        }

        File file = new File(filePath);

        try (FileInputStream fileInput = new FileInputStream(file)) {

            // 파일 전송 요청
            dataOut.writeUTF("UPLOAD " + file.getName());
            dataOut.writeLong(file.length());

            // 파일 데이터 전송
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = fileInput.read(buffer)) != -1) {
                dataOut.write(buffer, 0, bytesRead);
            }
            dataOut.flush();
            System.out.println("파일 업로드 완료: " + file.getName());

        } catch (IOException e) {
            System.err.println("파일 업로드 실패: " + e.getMessage());
        }
    }

    public static void downloadFile(String fileName, String savePath) {
        if (socket == null || dataIn == null || dataOut == null) {
            System.err.println("소켓이 초기화되지 않았습니다. setSocket()을 호출하세요.");
            return;
        }

        try (FileOutputStream fileOut = new FileOutputStream(savePath)) {

            // 다운로드 요청
            dataOut.writeUTF("DOWNLOAD " + fileName);

            // 파일 수신
            long fileSize = dataIn.readLong();
            byte[] buffer = new byte[4096];
            int bytesRead;
            long totalRead = 0;

            while ((bytesRead = dataIn.read(buffer)) != -1) {
                fileOut.write(buffer, 0, bytesRead);
                totalRead += bytesRead;
                if (totalRead >= fileSize) break;
            }

            System.out.println("파일 다운로드 완료: " + fileName);

        } catch (IOException e) {
            System.err.println("파일 다운로드 실패: " + e.getMessage());
        }
    }

    // 소켓 및 스트림 닫기 메서드
    public static void close() {
        try {
            if (dataIn != null) dataIn.close();
            if (dataOut != null) dataOut.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            System.err.println("자원 해제 실패: " + e.getMessage());
        }
    }
}
