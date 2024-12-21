package client;

import java.io.*;

public class FileClient {
    private static DataInputStream din;
    private static DataOutputStream dout;

    // 소켓 설정 메서드
    public static void setSocket(DataInputStream din,DataOutputStream dout) throws IOException {
        FileClient.din = din;
        FileClient.dout =dout;
    }



    public static void uploadFile(String formattedMessage,File file) {
        if (dout == null) {
            System.err.println("소켓이 초기화되지 않았습니다. setSocket()을 호출하세요.");
            return;
        }

        if (file == null || !file.exists()) {
            System.err.println("파일이 존재하지 않거나 null입니다: " + file);
            return;
        }

        try (FileInputStream fileInput = new FileInputStream(file)) {
            formattedMessage = formattedMessage + "," + file.getName();
            // 파일 전송 요청
            System.out.println("C1)업로드 시작: " + file.getName());
            dout.writeUTF(formattedMessage);  // 파일 이름 전송
            dout.writeLong(file.length());  // 파일 크기 전송
            System.out.println("C2)파일 이름과 크기 전송 완료.");

            // 파일 데이터 전송
            byte[] buffer = new byte[4096];
            int bytesRead;
            long totalBytesSent = 0;  // 전송된 총 바이트
            while ((bytesRead = fileInput.read(buffer)) != -1) {
                dout.write(buffer, 0, bytesRead);
                totalBytesSent += bytesRead;

                // 디버그: 현재까지 전송된 바이트 출력
                System.out.println("C3)전송된 바이트: " + totalBytesSent + " / " + file.length());
            }

            dout.flush();
            System.out.println("C4)파일 업로드 완료: " + file.getName());
        } catch (IOException e) {
            System.err.println("파일 업로드 실패: " + e.getMessage());
        }
    }



    public static void downloadFile(String fileName, String savePath) {
        if (din == null || dout == null) {
            System.err.println("소켓이 초기화되지 않았습니다. setSocket()을 호출하세요.");
            return;
        }

        try (FileOutputStream fileOut = new FileOutputStream(savePath)) {

            // 다운로드 요청
            dout.writeUTF(":DOWNLOAD " + fileName);

            // 파일 수신
            long fileSize = din.readLong();
            byte[] buffer = new byte[4096];
            int bytesRead;
            long totalRead = 0;

            while ((bytesRead = din.read(buffer)) != -1) {
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
            if (din != null) din.close();
            if (dout != null) dout.close();
        } catch (IOException e) {
            System.err.println("자원 해제 실패: " + e.getMessage());
        }
    }
}
