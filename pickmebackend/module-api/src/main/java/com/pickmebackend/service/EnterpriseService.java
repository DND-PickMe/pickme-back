package com.pickmebackend.service;

import com.pickmebackend.domain.Enterprise;
import com.pickmebackend.domain.dto.EnterpriseDto;
import com.pickmebackend.repository.EnterpriseRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EnterpriseService {

    private final EnterpriseRepository enterpriseRepository;

    private final ModelMapper modelMapper;

    private final PasswordEncoder passwordEncoder;

    public ResponseEntity<?> loadEnterprise(Long enterpriseId) {
        Optional<Enterprise> enterpriseOptional = enterpriseRepository.findById(enterpriseId);
        Enterprise enterprise = enterpriseOptional.get();

        return new ResponseEntity<>(enterprise, HttpStatus.OK);
    }

    public ResponseEntity<?> saveEnterprise(EnterpriseDto enterpriseDto) {
        Enterprise enterprise = modelMapper.map(enterpriseDto, Enterprise.class);
        enterprise.setPassword(passwordEncoder.encode(enterprise.getPassword()));
        enterprise.setCreatedAt(LocalDateTime.now());

        return new ResponseEntity<>(enterpriseRepository.save(enterprise), HttpStatus.CREATED);
    }

    public ResponseEntity<?> updateEnterprise(Long enterpriseId, EnterpriseDto enterpriseDto) {
        Optional<Enterprise> enterpriseOptional = enterpriseRepository.findById(enterpriseId);
        Enterprise enterprise = enterpriseOptional.get();
        modelMapper.map(enterpriseDto, enterprise);

        return new ResponseEntity<>(enterpriseRepository.save(enterprise), HttpStatus.OK);
    }

    public ResponseEntity<?> deleteEnterprise(Long enterpriseId) {
        enterpriseRepository.deleteById(enterpriseId);
        return ResponseEntity.ok().build();
    }

    public boolean isDuplicatedEnterprise(EnterpriseDto enterpriseDto) {
        return enterpriseRepository.findByEmail(enterpriseDto.getEmail()).isPresent();
    }

    public boolean isNonEnterprise(Long enterpriseId) {
        return !enterpriseRepository.findById(enterpriseId).isPresent();
    }
}
