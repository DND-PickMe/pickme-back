package com.pickmebackend.service;

import com.pickmebackend.domain.dto.jwt.JwtResponseDto;
import com.pickmebackend.config.jwt.JwtProvider;
import com.pickmebackend.domain.Account;
import com.pickmebackend.domain.dto.login.LoginRequestDto;
import com.pickmebackend.error.ErrorMessage;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import static com.pickmebackend.error.ErrorMessageConstant.USERNOTFOUND;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final AuthenticationManager authenticationManager;

    private final JwtProvider jwtProvider;

    private final ModelMapper modelMapper;

    public ResponseEntity<?> login(LoginRequestDto loginRequestDto) {
        if(!authenticate(loginRequestDto.getEmail(), loginRequestDto.getPassword()))  {
            return ResponseEntity.badRequest().body(new ErrorMessage(USERNOTFOUND));
        }
        Account account = modelMapper.map(loginRequestDto, Account.class);

        return new ResponseEntity<>(new JwtResponseDto(jwtProvider.generateToken(account)), HttpStatus.OK);
    }

    private boolean authenticate(String username, String password) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
            return true;
        }
        catch (DisabledException | BadCredentialsException e) {
            return false;
        }
    }
}
