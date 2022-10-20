package com.example.tenpo.repo;

import com.example.tenpo.domain.UserEntity;
import com.example.tenpo.testutils.DatabaseTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

@SpringBootTest
class UserEntityRepositoryTest extends DatabaseTest {

    @Autowired
    private UserEntityRepository userEntityRepository;

    @Test
    public void test1(){
        UserEntity build = UserEntity.builder().build();
        UserEntity save = userEntityRepository.save(build);
        Optional<UserEntity> byId = userEntityRepository.findById(save.getId());
        System.out.println("ID ES" + byId.get().getId());

    }


    @Test
    public void test2(){
        UserEntity build = UserEntity.builder().build();
        UserEntity save = userEntityRepository.save(build);
        Optional<UserEntity> byId = userEntityRepository.findById(save.getId());
        System.out.println("ID ES" + byId.get().getId());

    }

}
