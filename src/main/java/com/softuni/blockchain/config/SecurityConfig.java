package com.softuni.blockchain.config;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.context.annotation.Configuration;

import java.security.Security;

@Configuration
public class SecurityConfig {

    public SecurityConfig() {
        Security.addProvider(new BouncyCastleProvider());
    }
}
