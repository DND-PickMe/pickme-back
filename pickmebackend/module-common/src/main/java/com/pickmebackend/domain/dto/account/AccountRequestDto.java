package com.pickmebackend.domain.dto.account;

import com.pickmebackend.domain.Technology;
import lombok.*;
import org.springframework.lang.Nullable;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Null;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class AccountRequestDto {

    @Email @NotBlank
    private String email;

    @NotBlank @Size(min = 8, max = 20)
    private String password;

    @NotBlank
    private String nickName;

    @Nullable
    private String oneLineIntroduce;

    @Nullable
    private String socialLink;

    @Nullable
    private List<Technology> technologyList;
}
