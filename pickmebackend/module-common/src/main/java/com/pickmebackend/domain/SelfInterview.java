package com.pickmebackend.domain;

import lombok.*;
import javax.persistence.*;

@Getter @Setter @EqualsAndHashCode(of = "id")
@NoArgsConstructor @AllArgsConstructor @Builder
@Entity
public class SelfInterview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String title;

    @Column
    private String content;

    @ManyToOne
    private Account author;
}
