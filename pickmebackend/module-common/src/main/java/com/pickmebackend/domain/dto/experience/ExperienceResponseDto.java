package com.pickmebackend.domain.dto.experience;

import com.pickmebackend.domain.Account;
import lombok.*;

import java.time.LocalDate;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class ExperienceResponseDto {

    private Long id;

    private String companyName;

    private String position;

    private LocalDate joinedAt;

    private LocalDate retiredAt;

    private String description;

    private Account account;
}
