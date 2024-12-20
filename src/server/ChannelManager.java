//ChannelManager.java

package server;

import java.io.IOException;
import java.util.*;
import models.Channel;

public class ChannelManager {
    private static Map<String, Channel> channels = new HashMap<>();

    // 채널에 사용자 추가
    public static synchronized void joinChannel(String channelName, ClientHandler client) {
        Channel channel = channels.computeIfAbsent(channelName, name -> new Channel(name));
        channel.addClient(client);
    }

    // 채널에서 사용자 제거
    public static synchronized void leaveChannel(String channelName, ClientHandler client) {
        if (channels.containsKey(channelName)) {
            Channel channel = channels.get(channelName);
            channel.removeClient(client);
        }
    }

    // 채널 메시지 브로드캐스트
    public static synchronized void broadcast(String channelName, String message) throws IOException {
        if (channels.containsKey(channelName)) {
            Channel channel = channels.get(channelName);
            channel.broadcastMessage(message);
        }
    }

    // 새 채널 추가
    public static synchronized void addChannel(String channelName) {
        if (!channels.containsKey(channelName)) {
            Channel newChannel = new Channel(channelName);
            channels.put(channelName, newChannel);
        }
    }
}


