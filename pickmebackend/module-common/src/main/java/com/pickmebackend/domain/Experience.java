package com.pickmebackend.domain;

import lombok.*;
import javax.persistence.*;
import java.time.LocalDate;

@Getter @Setter @EqualsAndHashCode(of = "id")
@NoArgsConstructor @AllArgsConstructor @Builder
@Entity
public class Experience {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String companyName;

    @Column
    private String position;

    @Column
    private LocalDate joinedAt;

    @Column
    private LocalDate retiredAt;

    @Column
    private String description;

    @ManyToOne
    private Account account;

    public void mapAccount(Account account) {
        if (this.account != null) {
            this.account.getExperiences().remove(this);
        }
        this.account = account;
        account.getExperiences().add(this);
    }
}
