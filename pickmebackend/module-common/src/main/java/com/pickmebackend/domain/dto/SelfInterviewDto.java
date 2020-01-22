package com.pickmebackend.domain.dto;

import lombok.*;

import javax.persistence.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class SelfInterviewDto {

    private String title;

    private String content;
}
