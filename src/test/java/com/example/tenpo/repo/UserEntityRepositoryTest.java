package com.example.tenpo.repo;

import com.example.tenpo.domain.UserEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class UserEntityRepositoryTest {

    @Autowired
    private UserEntityRepository userEntityRepository;

    @Test
    public void test(){
        UserEntity build = UserEntity.builder().build();
        UserEntity save = userEntityRepository.save(build);
        userEntityRepository.findById(save.getId());

    }

}
