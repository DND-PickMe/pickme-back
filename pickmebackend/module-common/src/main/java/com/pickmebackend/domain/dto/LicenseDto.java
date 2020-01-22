package com.pickmebackend.domain.dto;

import lombok.*;

import java.time.LocalDate;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class LicenseDto {

    private String name;

    private String institution;

    private LocalDate issuedDate;

    private String description;
}
