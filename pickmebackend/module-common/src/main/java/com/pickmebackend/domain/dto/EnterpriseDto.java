package com.pickmebackend.domain.dto;

import lombok.*;

import javax.persistence.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class EnterpriseDto {

    private String registrationNumber;

    private String name;

    private String address;

    private String ceoName;
}
