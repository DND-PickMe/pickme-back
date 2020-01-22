package com.pickmebackend.domain.dto;

import lombok.*;

import java.time.LocalDate;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class ExperienceDto {

    private String companyName;

    private String position;

    private LocalDate joinedAt;

    private LocalDate retiredAt;

    private String description;
}
