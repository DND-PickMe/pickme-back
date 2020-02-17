package com.pickmebackend.domain.dto.license;

import lombok.*;
import java.time.LocalDate;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class LicenseRequestDto {

    private String name;

    private String institution;

    private LocalDate issuedDate;

    private String description;
}
