package com.doosik.jwtproject.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LogoutRequestDto {

	private String refreshToken;
}
