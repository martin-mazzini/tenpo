package com.example.tenpo.testutils;

import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Component
public class DatabaseTest {

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @AfterEach
    public void after(){
        this.resetDatabaseState();
    }

    public void resetDatabaseState() {
        ClassPathResource resource = new ClassPathResource("/clean.sql", DatabaseTest.class);
        try (InputStream inputStream = resource.getInputStream()) {
            String string = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            System.out.println(string);
            jdbcTemplate.execute(string);
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
