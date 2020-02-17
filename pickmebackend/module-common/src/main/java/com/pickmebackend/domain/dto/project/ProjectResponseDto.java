package com.pickmebackend.domain.dto.project;

import com.pickmebackend.domain.Account;
import lombok.*;
import java.time.LocalDate;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class ProjectResponseDto {

    private Long id;

    private String name;

    private String role;

    private String description;

    private LocalDate startedAt;

    private LocalDate endedAt;

    private String projectLink;

    private Account account;

}
