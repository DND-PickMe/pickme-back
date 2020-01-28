package com.pickmebackend.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import javax.persistence.*;
import java.time.LocalDateTime;
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
    @JsonIgnore
    private String password;

    @Column
    private String nickName;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> technology = new ArrayList<>();

    @Column
    private LocalDateTime createdAt;

    @Column
    private String oneLineIntroduce;

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
