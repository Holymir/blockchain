package com.softuni.blockchain.node;

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
}
