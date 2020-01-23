package com.pickmebackend.service;

import com.pickmebackend.domain.Project;
import com.pickmebackend.domain.dto.ProjectDto;
import com.pickmebackend.error.ErrorMessage;
import com.pickmebackend.error.ErrorMessageConstant;
import com.pickmebackend.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.pickmebackend.error.ErrorMessageConstant.*;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;

    private final ModelMapper modelMapper;

    public ResponseEntity<?> saveProject(ProjectDto projectDto) {
        Project project = modelMapper.map(projectDto, Project.class);
        return new ResponseEntity<>(this.projectRepository.save(project), HttpStatus.CREATED);
    }

    public ResponseEntity<?> updateProject(Long projectId, ProjectDto projectDto) {
        Optional<Project> projectOptional = projectRepository.findById(projectId);
        if (!projectOptional.isPresent()) {
            return ResponseEntity.badRequest().body(new ErrorMessage(PROJECTNOTFOUND));
        }
        Project project = projectOptional.get();
        modelMapper.map(projectDto, project);
        return new ResponseEntity<>(this.projectRepository.save(project), HttpStatus.OK);
    }

    public ResponseEntity<?> deleteProject(Long projectId) {
        Optional<Project> projectOptional = projectRepository.findById(projectId);
        if (!projectOptional.isPresent()) {
            return ResponseEntity.badRequest().body(new ErrorMessage(PROJECTNOTFOUND));
        }
        Project project = projectOptional.get();
        this.projectRepository.delete(project);
        return ResponseEntity.ok().build();
    }
}
