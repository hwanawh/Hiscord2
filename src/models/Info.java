package models;

public class Info {
    private String channelName;
    private String filePath;

    public Info(String channelName, String filePath) {
        this.channelName = channelName;
        this.filePath = filePath;
    }

    public String getChannelName() {
        return channelName;
    }

    public String getFilePath() {
        return filePath;
    }
}
