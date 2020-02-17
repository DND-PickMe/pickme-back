package com.pickmebackend.domain.dto.prize;

import com.pickmebackend.domain.Account;
import lombok.*;
import java.time.LocalDate;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class PrizeResponseDto {

    private Long id;

    private String competition;

    private String name;

    private LocalDate issuedDate;

    private String description;

    private Account account;
}
