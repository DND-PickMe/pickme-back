package com.pickmebackend.domain;

import lombok.*;

import javax.persistence.*;
import java.util.Date;

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
    private Date startedAt;

    @Column
    private Date endedAt;

    @Column
    private String projectLink;
}
