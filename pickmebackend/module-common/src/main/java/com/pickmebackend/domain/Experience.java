package com.pickmebackend.domain;

import lombok.*;

import javax.persistence.*;
import java.util.Date;

@Getter @Setter @EqualsAndHashCode(of = "id")
@NoArgsConstructor @AllArgsConstructor @Builder
@Entity
public class Experience {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String companyName;

    @Column
    private String position;

    @Column
    private Date joinedAt;

    @Column
    private Date retiredAt;

    @Column
    private String description;

    @ManyToOne
    private Account account;
}
