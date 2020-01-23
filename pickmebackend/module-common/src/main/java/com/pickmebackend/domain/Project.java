package com.pickmebackend.domain;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;

@Getter @Setter @EqualsAndHashCode(of = "id")
@NoArgsConstructor @AllArgsConstructor @Builder
@Entity
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;

    @Column
    private String role;

    @Column
    private String description;

    @Column
    private LocalDate startedAt;

    @Column
    private LocalDate endedAt;

    @Column
    private String projectLink;

    @ManyToOne
    private Account account;
}
