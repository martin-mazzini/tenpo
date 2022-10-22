package com.example.tenpo.testutils;

import com.example.tenpo.domain.UserEntity;

public class UserUtils {

    public static UserEntity createUser() {
        return UserEntity.builder()
                .email("martinmazzinigeo@gmail.com")
                .encryptedPassword("83hed8334")
                .firstName("m")
                .lastName("m")
                .userId("aa")
                .build();
    }


}
