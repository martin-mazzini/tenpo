package com.example.tenpo.service;



import com.example.tenpo.controller.dto.UserDTO;
import com.example.tenpo.domain.UserEntity;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UsersService extends UserDetailsService {
	UserDTO createUser(UserDTO userDetails);

	UserEntity findByEmail(String email);


	void save(UserEntity user);
}
