package models;

import server.ClientHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Channel {
    private String name;
    private List<ClientHandler> clients;

    public Channel(String name) {
        this.name = name;
        this.clients = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void addClient(ClientHandler client) {
        clients.add(client);
    }

    public void removeClient(ClientHandler client) {
        clients.remove(client);
    }

    public void broadcastMessage(String message) throws IOException {

        for (ClientHandler client : clients) {
            client.sendMessage(message);
        }
    }

    public void broadcastMember(String message) throws IOException{
        for (ClientHandler client : clients) {
            client.sendMember(message);
        }
    }

    public void broadcastInfo(String message) throws IOException{
        for (ClientHandler client : clients) {
            client.sendInfo(message);
        }
    }



    public List<ClientHandler> getClients() {
        return clients;
    }
}
