package com.example.tenpo.repository;

import com.example.tenpo.domain.UserEntity;
import com.example.tenpo.testutils.DatabaseTest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

@SpringBootTest
class UserEntityRepositoryTest extends DatabaseTest {

    @Autowired
    private UserRepository userEntityRepository;

    @Test
    public void testSave(){
        UserEntity user = buildUser();
        UserEntity saved = userEntityRepository.save(user);
        Optional<UserEntity> found = userEntityRepository.findById(saved.getId());
        Assertions.assertThat(found.isPresent());
        Assertions.assertThat(found.get().getId() == 1L);
    }


    @Test
    public void testFindByEmail(){
        UserEntity user = buildUser();
        UserEntity saved = userEntityRepository.save(user);
        UserEntity found = userEntityRepository.findByEmail("email");
        Assertions.assertThat(found != null);

    }

    private UserEntity buildUser() {
        return UserEntity.builder()
                .email("email")
                .encryptedPassword("83hed83")
                .firstName("m")
                .lastName("m")
                .userId("aa")
                .build();
    }

}
