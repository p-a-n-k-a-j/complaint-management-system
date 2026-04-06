package com.pankaj.complaintmanagement.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Tumhara path: uploads/images
        String path = "uploads/images";
        String absolutePath = new File(path).getAbsolutePath();

        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:" + absolutePath + File.separator);
    }
}
