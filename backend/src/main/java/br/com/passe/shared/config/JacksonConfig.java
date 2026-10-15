package br.com.passe.shared.config;

import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {

    @Bean
    public SimpleModule strictIntegerModule() {
        SimpleModule module = new SimpleModule();
        module.addDeserializer(Integer.class, new StrictIntegerDeserializer());
        module.addDeserializer(int.class, new StrictIntegerDeserializer());
        return module;
    }
}