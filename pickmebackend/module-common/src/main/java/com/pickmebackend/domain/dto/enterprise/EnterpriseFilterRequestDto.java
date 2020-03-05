package com.pickmebackend.domain.dto.enterprise;

import org.springframework.lang.Nullable;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class EnterpriseFilterRequestDto {

    @Nullable
    private String name;

    @Nullable
    private String address;

}
