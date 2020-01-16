package com.pickmebackend.domain;

import lombok.*;

import javax.persistence.*;

@Getter @Setter @EqualsAndHashCode(of = "id")
@NoArgsConstructor @AllArgsConstructor @Builder
@Entity
public class Prize {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String competition;

    @Column
    private String name;

    @Column
    private String issuedDate;

    @Column
    private String description;
}
