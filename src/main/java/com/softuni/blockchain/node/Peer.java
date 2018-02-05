package com.softuni.blockchain.node;

import java.util.Objects;

public class Peer {

    private String url;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Peer peer = (Peer) o;
        return Objects.equals(url, peer.url);
    }

    @Override
    public int hashCode() {

        return Objects.hash(url);
    }
}
