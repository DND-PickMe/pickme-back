package com.pickmebackend.domain;

import lombok.*;
import javax.persistence.*;

@Getter @Setter @EqualsAndHashCode(of = "id")
@NoArgsConstructor @AllArgsConstructor @Builder
@Entity
public class Enterprise {

    @Id
    @Column(name = "enterprise_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String registrationNumber;

    @Column
    private String name;

    @Column
    private String address;

    @Column
    private String ceoName;

    @OneToOne(mappedBy = "enterprise")
//    @OneToOne
    private Account account;

}
