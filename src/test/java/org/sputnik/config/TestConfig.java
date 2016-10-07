package org.sputnik.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.sputnik.SputnikApplication;
import org.sputnik.context.Launcher;

@Import(SputnikApplication.class)
public class TestConfig {

    @Bean
    public Launcher launcher() {
        //disable application launcher during unit testing
        return null;
    }

}
