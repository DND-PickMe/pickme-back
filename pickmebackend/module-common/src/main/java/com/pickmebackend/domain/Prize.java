package com.pickmebackend.domain;

import lombok.*;
import javax.persistence.*;
import java.time.LocalDate;

@Getter @Setter @EqualsAndHashCode(of = "id")
@NoArgsConstructor @AllArgsConstructor @Builder
@Entity
public class Prize {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String competition;

    @Column
    private String name;

    @Column
    private LocalDate issuedDate;

    @Column
    private String description;

    @ManyToOne
    private Account account;

    public void mapAccount(Account account) {
        if (this.account != null) {
            this.account.getPrizes().remove(this);
        }
        this.account = account;
        this.account.getPrizes().add(this);
    }
}
