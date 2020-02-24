package com.pickmebackend.domain.dto.jwt;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
public class JwtResponseDto {

    private String jwt;
}
