package com.pickmebackend.domain.dto.account;

import com.pickmebackend.domain.Technology;
import lombok.*;
import org.springframework.lang.Nullable;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class AccountRequestDto {

    @NotBlank
    private String email;

    @NotBlank
    private String nickName;

    @Nullable
    private String oneLineIntroduce;

    @Nullable
    private String socialLink;

    @Nullable
    private String career;

    @Nullable
    private Set<String> positions;

    @Nullable
    private List<Technology> technologies = new ArrayList<>();
}
