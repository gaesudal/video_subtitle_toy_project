package com.sudal.video.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author SUDAL
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

        @Value("${file.upload-dir}")
        private String uploadDir;

        @Override
        public void addResourceHandlers(ResourceHandlerRegistry registry) {

            registry.addResourceHandler("/video/**")
                    .addResourceLocations("file:///" + uploadDir + "/video/");
            registry.addResourceHandler("/thumbnail/**")
                    .addResourceLocations("file:///" + uploadDir + "/thumbnail/");
            registry.addResourceHandler("/subtitle/**")
                    .addResourceLocations("file:///" + uploadDir + "/subtitle/");
        }
}
