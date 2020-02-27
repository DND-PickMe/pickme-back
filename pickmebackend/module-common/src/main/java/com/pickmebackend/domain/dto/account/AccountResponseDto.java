package com.pickmebackend.domain.dto.account;

import com.pickmebackend.domain.*;
import com.pickmebackend.domain.enums.UserRole;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter @Setter @AllArgsConstructor @Builder @NoArgsConstructor
public class AccountResponseDto {

    private Long id;

    private String email;

    private String nickName;

    private Integer favoriteCount;

    private String oneLineIntroduce;

    private String image;

    private UserRole userRole;

    private String socialLink;

    private String career;

    private LocalDateTime createdAt;

    private Set<Experience> experiences;

    private Set<License> licenses;

    private Set<Prize> prizes;

    private Set<Project> projects;

    private Set<SelfInterview> selfInterviews;

    private List<Technology> technologies = new ArrayList<>();

    private List<String> positions;

    public AccountResponseDto (Account account) {
        this.id = account.getId();
        this.email = account.getEmail();
        this.nickName = account.getNickName();
        this.favoriteCount = account.getFavorite().size();
        this.oneLineIntroduce = account.getOneLineIntroduce();
        this.image = account.getImage();
        this.userRole = account.getUserRole();
        this.experiences = account.getExperiences();
        this.licenses = account.getLicenses();
        this.prizes = account.getPrizes();
        this.projects = account.getProjects();
        this.selfInterviews = account.getSelfInterviews();
        this.createdAt = account.getCreatedAt();
        this.userRole = account.getUserRole();
    }

    public void toTech(Account account) {
        this.technologies = account.getAccountTechSet().stream().map(AccountTech::getTechnology).collect(Collectors.toList());
    }
}
