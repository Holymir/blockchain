package com.softuni.blockchain.config;


import com.softuni.blockchain.node.NodeController;
import com.softuni.blockchain.node.PeerController;
import com.softuni.blockchain.node.socket.SocketHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final PeerController peerController;
    private final NodeController nodeController;

    @Autowired
    public WebSocketConfig(PeerController peerController, NodeController nodeController) {
        this.peerController = peerController;
        this.nodeController = nodeController;
    }

    @Bean
    public SocketHandler myMessageHandler() {
        return new SocketHandler(this.peerController, this.nodeController);
    }


    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry webSocketHandlerRegistry) {
        webSocketHandlerRegistry.addHandler(myMessageHandler(), "/")
                .setAllowedOrigins("*");
    }
}
