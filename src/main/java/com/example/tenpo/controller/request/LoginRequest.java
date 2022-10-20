package com.example.tenpo.controller.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
public class LoginRequest {


	@NotNull(message="El email no puede ser vacío")
	@Email
	@ApiModelProperty(value="Email con formato válido", example = "A@A.com", required = true)
	private String email;


	@NotNull(message="Password no puede ser vacía")
	@Size(min=8, max=16, message="La password debe tener entre 8 y 17 caracteres")
	@ApiModelProperty(value="Password entre 8 y 17 caracteres", example = "argentina123", required = true)
	private String password;



}
