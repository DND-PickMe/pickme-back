package com.pickmebackend.domain.dto.project;

import lombok.*;
import java.time.LocalDate;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class ProjectRequestDto {

    private String name;

    private String role;

    private String description;

    private LocalDate startedAt;

    private LocalDate endedAt;

    private String projectLink;
}
