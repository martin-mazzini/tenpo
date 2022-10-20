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
	
	@NotNull(message="El primer nombre no puede ser vacío")
	@Size(min=2, message= "El primer nombre debe tener más de dos caracteres")
	@ApiModelProperty(value="Nombre de más de dos caracteres", example = "Martín", required = true)
	private String firstName;

	@NotNull(message="El apellido no puede ser vacío")
	@Size(min=2, message= "El apellido debe tener más de dos caracteres")
	@ApiModelProperty(value="Apellido de más de dos caracteres", example = "Mazzini", required = true)
	private String lastName;
	
	@NotNull(message="Password no puede ser vacía")
	@Size(min=8, max=16, message="La password debe tener entre 8 y 17 caracteres")
	@ApiModelProperty(value="Password entre 8 y 17 caracteres", example = "m123456789", required = true)
	private String password;
	
	@NotNull(message="El email no puede ser vacío")
	@Email
	@ApiModelProperty(value="Email con formato válido", example = "martinmazzinigeo@gmail.com",required = true)
	private String email;



	
}
