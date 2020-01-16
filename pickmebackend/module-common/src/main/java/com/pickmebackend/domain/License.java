package com.pickmebackend.domain;

import lombok.*;

import javax.persistence.*;
import java.util.Date;

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
    private Date issuedDate;

    @Column
    private String description;

    @ManyToOne
    private Account account;
}
