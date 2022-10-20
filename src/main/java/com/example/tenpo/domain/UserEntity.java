package com.example.tenpo.domain;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

@Entity
@Table(name="users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserEntity implements Serializable {


	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable=false, length=50)
	private String firstName;
	
	@Column(nullable=false, length=50)
	private String lastName;
	
	@Column(nullable=false, length=120, unique=true)
	private String email; /** EL email es el atributo que identifica univocamente a un usuario**/
	
	@Column(nullable=false, unique=true)
	private String userId;
	
	@Column(nullable=false, unique=true)
	private String encryptedPassword;

	@Override public String toString() {
		return email;
	}
}
