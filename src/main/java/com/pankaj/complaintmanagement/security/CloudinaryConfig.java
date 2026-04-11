package com.pankaj.complaintmanagement.security;

import com.cloudinary.Cloudinary;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class CloudinaryConfig {

    @Bean
    public Cloudinary cloudinary(){
        Map<String, String> config = new HashMap<>();
        config.put("cloud_name", "dw9w2nxvt");
        config.put("api_key", "321665277384972");
        config.put("api_secret", "Kg3Xfi-6M5csN9YU8vuo4HHMnMY");
        return new Cloudinary(config);
    }
}
