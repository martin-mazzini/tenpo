package com.example.tenpo.controller.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Getter
@Setter
public class CreateUserRequest {
	
	@NotNull(message="First name can´t be empty")
	@Size(min=2, message= "First name has to have more than 2 characters")
	@ApiModelProperty(value="First name with more than 2 characters", example = "Martín", required = true)
	private String firstName;

	@NotNull(message="Last name can´t be empty")
	@Size(min=2, message= "Last name has to have more than 2 characters")
	@ApiModelProperty(value="Last name with more than 2 characters", example = "Mazzini", required = true)
	private String lastName;
	
	@NotNull(message="Password  can´t be empty")
	@Size(min=8, max=16, message="Password has to have between 8 y 17 caracteres")
	@ApiModelProperty(value="Password with between 8 y 17 caracteres", example = "m123456789", required = true)
	private String password;
	
	@NotNull(message="Email can´t be empty")
	@Email
	@ApiModelProperty(value="Valid email", example = "martinmazzinigeo@gmail.com",required = true)
	private String email;



	
}
