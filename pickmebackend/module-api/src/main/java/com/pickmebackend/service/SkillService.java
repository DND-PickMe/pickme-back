package com.pickmebackend.service;

import com.pickmebackend.domain.Technology;
import com.pickmebackend.repository.TechnologyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class SkillService {

    private final TechnologyRepository technologyRepository;

    public ResponseEntity<?> getTechnology(Pageable pageable) {
        Page<Technology> allTechs = technologyRepository.findAll(pageable);
        return new ResponseEntity<>(allTechs, HttpStatus.OK);
    }
}
