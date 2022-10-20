package com.example.tenpo.controller;


import com.example.tenpo.controller.dto.UserDTO;
import com.example.tenpo.controller.request.CreateUserRequest;
import com.example.tenpo.controller.request.LoginRequest;
import com.example.tenpo.controller.response.CreateUserResponse;
import com.example.tenpo.security.AuthToken;
import com.example.tenpo.security.JWTTokenUtils;
import com.example.tenpo.service.UsersService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/users")
public class UsersController {


	private UsersService usersService;
	private AuthenticationManager authenticationManager;
	private JWTTokenUtils jwTokenUtils;
	private ModelMapper modelMapper;

	public UsersController(UsersService usersService, AuthenticationManager authenticationManager,
                           JWTTokenUtils jwTokenUtils, ModelMapper modelMapper) {
		this.usersService = usersService;
		this.authenticationManager = authenticationManager;
		this.jwTokenUtils = jwTokenUtils;
		this.modelMapper = modelMapper;
	}

	@PostMapping
	@ApiOperation(value="Crear un nuevo usuario",
			response = CreateUserResponse.class)
	@ApiResponses(value = {
			@ApiResponse(code = 422, message = "Ya existe otra usuario con el mismo email.")}
	)
	public ResponseEntity<CreateUserResponse> register(@Valid @RequestBody CreateUserRequest userDetails)
	{

		UserDTO userDto = modelMapper.map(userDetails, UserDTO.class);
		UserDTO createdUser = usersService.createUser(userDto);
		CreateUserResponse returnValue = modelMapper.map(createdUser, CreateUserResponse.class);
		return ResponseEntity.status(HttpStatus.OK).body(returnValue);
	}


	@PostMapping("/login")
	@ApiOperation(value="Loguearse a la aplicación.",
			response = AuthToken.class)
	public ResponseEntity<AuthToken> login(@RequestBody LoginRequest loginUser) {

		final Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(
						loginUser.getEmail(),
						loginUser.getPassword()
				)
		);
		SecurityContextHolder.getContext().setAuthentication(authentication);
		final String token = jwTokenUtils.generateToken(authentication);
		return ResponseEntity.ok(new AuthToken(token));
	}








}
