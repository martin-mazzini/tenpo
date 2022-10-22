package com.example.tenpo.controller.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {


	@NotNull(message="Email can´t be empty")
	@Email(message = "Not a valid email format")
	@ApiModelProperty(value="Valid email", example = "martinmazzinigeo@gmail.com", required = true)
	private String email;


	@NotNull(message="Password can´t be empty")
	@Size(min=8, max=16, message="Password size has to be between 8 and 17 characters")
	@ApiModelProperty(value="Password between 8 and 17 characters", example = "m123456789", required = true)
	private String password;



}
