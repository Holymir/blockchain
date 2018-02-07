package com.softuni.blockchain.node;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.web.socket.WebSocketSession;

import java.util.Objects;

public class Peer {

    private String url;
    private WebSocketSession session;
    private String sessionId;
    private String uuid;

    public Peer() {
    }

    public Peer(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    @JsonIgnore
    public WebSocketSession getSession() {
        return session;
    }

    public void setSession(WebSocketSession session) {
        this.session = session;
    }

    @JsonIgnore
    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Peer peer = (Peer) o;
        return Objects.equals(url, peer.url) &&
                Objects.equals(sessionId, peer.sessionId) &&
                Objects.equals(uuid, peer.uuid);
    }

    @Override
    public int hashCode() {

        return Objects.hash(url, sessionId, uuid);
    }
}
