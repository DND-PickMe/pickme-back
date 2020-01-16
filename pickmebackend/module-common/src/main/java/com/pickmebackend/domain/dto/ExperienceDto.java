package com.pickmebackend.domain.dto;

import lombok.*;

import javax.persistence.*;
import java.util.Date;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class ExperienceDto {

    private String companyName;

    private String position;

    private Date joinedAt;

    private Date retiredAt;

    private String description;
}
