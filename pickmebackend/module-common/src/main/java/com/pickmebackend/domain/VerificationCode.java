package com.pickmebackend.domain;

import lombok.*;
import javax.persistence.*;

@Entity @EqualsAndHashCode(of = "id")
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class VerificationCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String email;

    @Column
    private String code;

    @Column
    private boolean isVerified = false;

}
