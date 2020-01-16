package com.pickmebackend.domain;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Getter @Setter @EqualsAndHashCode(of = "id")
@NoArgsConstructor @AllArgsConstructor @Builder
@Entity
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String email;

    @Column
    private String password;

    @Column
    private String nickName;

    @Column
    private List<String> technology = new ArrayList<>();

    @OneToMany(mappedBy = "account")
    private Set<Experience> experiences;

    @OneToMany(mappedBy = "account")
    private Set<License> licenses;

    @OneToMany(mappedBy = "account")
    private Set<Prize> prizes;

    @OneToMany(mappedBy = "account")
    private Set<Project> projects;

    @OneToMany(mappedBy = "author")
    private Set<SelfInterview> selfInterviews;
}
