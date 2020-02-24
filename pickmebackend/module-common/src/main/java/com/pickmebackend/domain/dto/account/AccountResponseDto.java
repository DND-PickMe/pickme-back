package com.pickmebackend.domain.dto.account;

import com.pickmebackend.domain.*;
import lombok.*;
import java.util.List;
import java.util.Set;

@Getter @Setter @AllArgsConstructor @Builder @NoArgsConstructor
public class AccountResponseDto {

    private Long id;

    private String email;

    private String nickName;

    private List<String> technology;

    private Integer favoriteCount;

    private String oneLineIntroduce;

    private String image;

    private Set<Experience> experiences;

    private Set<License> licenses;

    private Set<Prize> prizes;

    private Set<Project> projects;

    private Set<SelfInterview> selfInterviews;

    public AccountResponseDto (Account account) {
        this.id = account.getId();
        this.email = account.getEmail();
        this.nickName = account.getNickName();
        this.technology = account.getTechnology();
        this.favoriteCount = account.getFavorite().size();
        this.oneLineIntroduce = account.getOneLineIntroduce();
        this.image = account.getImage();
        this.experiences = account.getExperiences();
        this.licenses = account.getLicenses();
        this.prizes = account.getPrizes();
        this.projects = account.getProjects();
        this.selfInterviews = account.getSelfInterviews();
    }
}
