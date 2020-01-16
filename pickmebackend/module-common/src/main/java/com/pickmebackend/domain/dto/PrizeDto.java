package com.pickmebackend.domain.dto;

import lombok.*;

import javax.persistence.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class PrizeDto {

    private String competition;

    private String name;

    private String issuedDate;

    private String description;
}
