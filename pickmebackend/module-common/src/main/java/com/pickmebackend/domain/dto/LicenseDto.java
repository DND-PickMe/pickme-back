package com.pickmebackend.domain.dto;

import lombok.*;

import javax.persistence.*;
import java.util.Date;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class LicenseDto {

    private String name;

    private String institution;

    private Date issuedDate;

    private String description;
}
