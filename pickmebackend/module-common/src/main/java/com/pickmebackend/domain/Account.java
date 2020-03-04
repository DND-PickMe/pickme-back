package com.pickmebackend.domain;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.pickmebackend.domain.enums.UserRole;
import lombok.*;
import org.springframework.web.util.UriComponentsBuilder;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.LinkedList;
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

    @ManyToMany(fetch = FetchType.EAGER)
    private List<Account> favorite = new LinkedList<>();

    @JsonIgnore
    private long favoriteCount;

    @ElementCollection(fetch = FetchType.EAGER)
    private Set<String> positions;

    @Column
    @Enumerated(EnumType.STRING)
    private UserRole userRole;

    @Column
    private String career;

    @Column
    private LocalDateTime createdAt;

    @Column
    private String oneLineIntroduce;

    @Column
    private String image;

    @Column
    private String socialLink;

    @Column
    private long hits;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "enterprise_id")
    private Enterprise enterprise;

    @OneToMany(mappedBy = "account", fetch = FetchType.EAGER)
    private Set<Experience> experiences = new HashSet<>();

    @OneToMany(mappedBy = "account", fetch = FetchType.EAGER)
    private Set<License> licenses = new HashSet<>();

    @OneToMany(mappedBy = "account", fetch = FetchType.EAGER)
    private Set<Prize> prizes = new HashSet<>();

    @OneToMany(mappedBy = "account", fetch = FetchType.EAGER)
    private Set<Project> projects = new HashSet<>();

    @OneToMany(mappedBy = "account", fetch = FetchType.EAGER)
    private Set<SelfInterview> selfInterviews = new HashSet<>();

    @OneToMany(mappedBy = "account")
    @JsonIgnore
    private Set<AccountTech> accountTechSet = new HashSet<>();

    public void addFavorite(Account currentUser) {
        if (this.getFavorite().contains(currentUser)) {
            this.getFavorite().remove(currentUser);
            this.favoriteCount--;
        } else {
            this.getFavorite().add(currentUser);
            this.favoriteCount++;
        }
    }

    public void setValue() {
        this.userRole = UserRole.USER;
        this.createdAt = LocalDateTime.now();
        this.image = defaultImage();
    }

    private String defaultImage() {
        final String USER_DEFAULT_IMG = "default_user.png";
        final String requestURI = "/api/images/";
        return UriComponentsBuilder.fromUriString("https://pickme-back.ga:8083").path(requestURI).path(USER_DEFAULT_IMG).toUriString();
    }
}
