package com.pickmebackend.domain.dto.license;

import com.pickmebackend.domain.Account;
import lombok.*;
import java.time.LocalDate;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class LicenseResponseDto {

    private Long id;

    private String name;

    private String institution;

    private LocalDate issuedDate;

    private String description;

    private Account account;
}
