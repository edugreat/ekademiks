package com.edugreat.akademiksresource.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import com.edugreat.akademiksresource.enums.Roles;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class UserRoles {

	@Enumerated(EnumType.STRING)
	@Column(length = 16)
	private Roles role;

}
