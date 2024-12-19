package server;

import models.Info;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class InfoManager {
    private Map<String, Info> infoMap;

    public InfoManager() {
        infoMap = new HashMap<>();
        loadInfoData();
    }

    private void loadInfoData() {
        // 채널 정보 및 파일 경로 설정
        infoMap.put("channel1", new Info("Channel 1", "resources/channel/channel1/info/info1.txt"));
        infoMap.put("channel2", new Info("Channel 2", "resources/channel/channel2/info/info1.txt"));
    }

    public Info getInfoByChannelName(String channelName) {
        return infoMap.getOrDefault(channelName.toLowerCase(), null);
    }

    // 공지 수정 기능 추가
    public void updateNotice(String channelName, String newNotice) {
        Info info = getInfoByChannelName(channelName);
        if (info != null) {
            String filePath = info.getFilePath();
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
                writer.write(newNotice);
                writer.flush(); // 파일에 내용 저장
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
