package com.pickmebackend.domain;

import lombok.*;

import javax.persistence.*;

@Getter @Setter @EqualsAndHashCode(of = "id")
@Entity @Table
@AllArgsConstructor @NoArgsConstructor @Builder
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column
    private String content;
}
