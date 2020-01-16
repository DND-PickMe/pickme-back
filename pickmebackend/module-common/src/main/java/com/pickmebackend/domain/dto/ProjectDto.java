package com.pickmebackend.domain.dto;

import lombok.*;

import javax.persistence.*;
import java.util.Date;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class ProjectDto {

    private String name;

    private String role;

    private String description;

    private Date startedAt;

    private Date endedAt;

    private String projectLink;
}
