package com.pickmebackend.domain;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;

@Getter @Setter @EqualsAndHashCode(of = "id")
@NoArgsConstructor @AllArgsConstructor @Builder
@Entity
public class License {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;

    @Column
    private String institution;

    @Column
    private LocalDate issuedDate;

    @Column
    private String description;

    @ManyToOne
    private Account account;
}
