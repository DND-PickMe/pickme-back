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
    private Account account;

    public void mapAccount(Account account) {
        if (this.account != null) {
            this.account.getSelfInterviews().remove(this);
        }
        this.account = account;
        this.account.getSelfInterviews().add(this);
    }
}
