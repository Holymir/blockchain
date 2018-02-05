package com.softuni.blockchain.config;


import com.softuni.blockchain.node.socket.SocketHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Bean
    public SocketHandler myMessageHandler() {
        return new SocketHandler();
    }


    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry webSocketHandlerRegistry) {
        webSocketHandlerRegistry.addHandler(myMessageHandler(), "/")
                .setAllowedOrigins("*");
    }
}
