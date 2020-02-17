package com.pickmebackend.domain.dto.prize;

import lombok.*;
import java.time.LocalDate;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class PrizeRequestDto {

    private String competition;

    private String name;

    private LocalDate issuedDate;

    private String description;
}
