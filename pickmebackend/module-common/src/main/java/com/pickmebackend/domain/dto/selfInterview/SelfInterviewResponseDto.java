package com.pickmebackend.domain.dto.selfInterview;

import com.pickmebackend.domain.Account;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class SelfInterviewResponseDto {

    private Long id;

    private String title;

    private String content;

    private Account account;
}
