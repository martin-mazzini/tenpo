package com.example.tenpo.domain;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

@Entity
@Table(name="XYZ")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserEntity implements Serializable {


	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	protected Long id;


}
