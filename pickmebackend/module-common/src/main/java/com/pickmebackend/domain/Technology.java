package com.pickmebackend.domain;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@Getter
@EqualsAndHashCode(of = "id")
@NoArgsConstructor @AllArgsConstructor
@Builder
@Entity
public class Technology {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;
}
