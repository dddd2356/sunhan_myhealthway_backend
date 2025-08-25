package com.example.HospitalSystemBackend.Config;

import com.example.HospitalSystemBackend.Util.MymdSeedCtrUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public MymdSeedCtrUtil mymdSeedCtrUtil() {
        return new MymdSeedCtrUtil();
    }
}
