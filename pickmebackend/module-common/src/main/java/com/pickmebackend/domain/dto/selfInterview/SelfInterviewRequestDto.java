package com.pickmebackend.domain.dto.selfInterview;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class SelfInterviewRequestDto {

    private String title;

    private String content;
}
