package com.pickmebackend.impl;

import com.pickmebackend.domain.Account;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Getter
class UserDetailsImpl extends User {

    private Account account;

    UserDetailsImpl(Account account) {
        super(account.getEmail(), account.getPassword(), authorities(account));
        this.account = account;
    }

    private static Collection<? extends GrantedAuthority> authorities(Account account) {
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_" + account.getUserRole().name()));
        return grantedAuthorities;
    }
}
