package com.pickmebackend.domain.dto.enterprise;

import lombok.*;
import org.springframework.lang.Nullable;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class EnterpriseFilterRequestDto {

    @Nullable
    private String name;

    @Nullable
    private String address;

}
