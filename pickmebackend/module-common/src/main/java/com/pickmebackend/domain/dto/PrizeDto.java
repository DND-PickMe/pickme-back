package com.pickmebackend.domain.dto;

import lombok.*;
import java.time.LocalDate;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class PrizeDto {

    private String competition;

    private String name;

    private LocalDate issuedDate;

    private String description;
}
