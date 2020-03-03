package com.pickmebackend.domain.dto.account;

import com.pickmebackend.domain.Technology;
import lombok.*;
import org.springframework.lang.Nullable;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class AccountRequestDto {

    @Email(message = "이메일 형식이 아닙니다.") @NotBlank(message = "이메일은 반드시 입력되어야 합니다.")
    private String email;

    @NotBlank(message = "이름은 반드시 입력되어야 합니다.")
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
