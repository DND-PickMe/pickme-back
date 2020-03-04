package com.pickmebackend.domain.dto.account;

import lombok.*;
import org.springframework.lang.Nullable;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class AccountFilteringRequestDto {

    @Nullable
    private String nickName;

    @Nullable
    private String oneLineIntroduce;

    @Nullable
    private String career;

    @Nullable
    private String positions;

    @Nullable
    private String technology;

    @Nullable
    private String orderBy;

}
