package com.example.tenpo.service;



import com.example.tenpo.domain.UserEntity;
import com.example.tenpo.exceptions.exceptions.NotUniqueException;
import com.example.tenpo.repo.UserRepository;
import com.example.tenpo.service.dto.UserDTO;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.UUID;

@Service
public class UsersServiceImpl implements UsersService {
	
	UserRepository usersRepository;
	BCryptPasswordEncoder bCryptPasswordEncoder;
	Environment environment;
	private ModelMapper modelMapper;


	
	@Autowired
	public UsersServiceImpl(UserRepository usersRepository,
			BCryptPasswordEncoder bCryptPasswordEncoder,
			Environment environment, ModelMapper modelMapper)
	{
		this.usersRepository = usersRepository;
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
		this.environment = environment;
		this.modelMapper = modelMapper;
	}
 
	@Override
	public UserDTO createUser(UserDTO userDto) {

		if (!validateUniqueMail(userDto.getEmail())){
			throw new NotUniqueException("Ya existe un usuario con el mismo email");
		}

		userDto.setUserId(UUID.randomUUID().toString());
		userDto.setEncryptedPassword(bCryptPasswordEncoder.encode(userDto.getPassword()));
		UserEntity userEntity = modelMapper.map(userDto, UserEntity.class);
		usersRepository.save(userEntity);
		UserDTO returnValue = modelMapper.map(userEntity, UserDTO.class);
		return returnValue;
	}

	private boolean validateUniqueMail(String email) {
		return  usersRepository.findByEmail(email) == null;
	}

	@Override public UserEntity findByEmail(String email) {

		return usersRepository.findByEmail(email);
	}

	@Override public void save(UserEntity user) {
		usersRepository.save(user);
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		UserEntity userEntity = usersRepository.findByEmail(username);

		if(userEntity == null) throw new UsernameNotFoundException(username);

		return new User(userEntity.getEmail(), userEntity.getEncryptedPassword(),  new HashSet<>());
	}






	
	

}
