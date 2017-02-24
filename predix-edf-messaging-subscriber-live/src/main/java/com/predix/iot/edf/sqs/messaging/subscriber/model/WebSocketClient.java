package com.predix.iot.edf.sqs.messaging.subscriber.model;

import javax.websocket.*;
import java.io.IOException;

public class WebSocketClient {
    protected Session userSession = null;

    public Session getSession() {
        return userSession;
    }

    public void connect(String server, String accessToken, String zoneId) throws IOException, DeploymentException {
        userSession = WebSocketUtils.openWebSocket(server, this, zoneId, accessToken);
    }

    public void sendMessage(String message) throws IOException {
        getSession().getBasicRemote().sendText(message);
    }

    public void disconnect() throws IOException {
        if (userSession != null) {
            userSession.close();
        }
    }

    public boolean isConnected() {
        return userSession != null;
    }

    @OnOpen
    public void onOpen(Session session) {
        System.out.println("Connected");
        session.setMaxIdleTimeout(2000000);
    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        System.out.println("Disconnected");
    }

    @OnMessage
    public void onMessage(Session session, String msg) {
        System.out.println(msg);
    }
}
