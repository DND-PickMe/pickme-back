package com.pickmebackend.service;

import com.pickmebackend.config.jwt.Jwt;
import com.pickmebackend.config.jwt.JwtProvider;
import com.pickmebackend.domain.dto.AccountDto;
import com.pickmebackend.error.ErrorMessage;
import com.pickmebackend.impl.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import static com.pickmebackend.error.ErrorMessageConstant.USERNOTFOUND;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final UserDetailsServiceImpl userDetailsServiceImpl;

    private final AuthenticationManager authenticationManager;

    private final JwtProvider jwtProvider;

    private final PasswordEncoder passwordEncoder;

    public ResponseEntity<?> login(AccountDto accountDto) throws Exception {
        authenticate(accountDto.getEmail(), accountDto.getPassword());
        UserDetails currentUser = this.userDetailsServiceImpl.loadUserByUsername(accountDto.getEmail());

        if(currentUser == null || !passwordEncoder.matches(accountDto.getPassword(), currentUser.getPassword())) {
            return new ResponseEntity<>(new ErrorMessage(USERNOTFOUND), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(new Jwt(jwtProvider.generateToken(accountDto)), HttpStatus.OK);
    }

    private void authenticate(String username, String password) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        }
        catch (DisabledException e) {
            throw new Exception("USER_DISABLED", e);
        }
        catch (BadCredentialsException e)   {
            throw new Exception("INVALID_CREDENTIALS", e);
        }
    }


}
