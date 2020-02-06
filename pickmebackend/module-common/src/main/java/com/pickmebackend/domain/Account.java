package com.pickmebackend.domain;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.*;
import org.springframework.security.access.prepost.PreAuthorize;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Getter @Setter @EqualsAndHashCode(of = "id")
@NoArgsConstructor @AllArgsConstructor @Builder @ToString
@Entity @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
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

    @Column
    private String image;

    @OneToMany(mappedBy = "account", fetch = FetchType.EAGER)
    private Set<Experience> experiences;

    @OneToMany(mappedBy = "account", fetch = FetchType.EAGER)
    private Set<License> licenses;

    @OneToMany(mappedBy = "account", fetch = FetchType.EAGER)
    private Set<Prize> prizes;

    @OneToMany(mappedBy = "account", fetch = FetchType.EAGER)
    private Set<Project> projects;

    @OneToMany(mappedBy = "account", fetch = FetchType.EAGER)
    private Set<SelfInterview> selfInterviews;
}
