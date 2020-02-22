package com.pickmebackend.service;

import com.pickmebackend.domain.Account;
import com.pickmebackend.domain.Project;
import com.pickmebackend.domain.dto.project.ProjectRequestDto;
import com.pickmebackend.domain.dto.project.ProjectResponseDto;
import com.pickmebackend.error.ErrorMessage;
import com.pickmebackend.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.util.Optional;
import static com.pickmebackend.error.ErrorMessageConstant.PROJECTNOTFOUND;
import static com.pickmebackend.error.ErrorMessageConstant.UNAUTHORIZEDUSER;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;

    private final ModelMapper modelMapper;

    public ProjectResponseDto saveProject(ProjectRequestDto projectRequestDto, Account currentUser) {
        Project project = modelMapper.map(projectRequestDto, Project.class);
        project.mapAccount(currentUser);
        Project savedProject = this.projectRepository.save(project);
        ProjectResponseDto projectResponseDto = modelMapper.map(savedProject, ProjectResponseDto.class);

        return projectResponseDto;
    }

    public ProjectResponseDto updateProject(Project project, ProjectRequestDto projectRequestDto, Account currentUser) {
        modelMapper.map(projectRequestDto, project);
        Project modifiedProject = this.projectRepository.save(project);
        ProjectResponseDto projectResponseDto = modelMapper.map(modifiedProject, ProjectResponseDto.class);

        return projectResponseDto;
    }

    public ResponseEntity<?> deleteProject(Long projectId, Account currentUser) {
        Optional<Project> projectOptional = this.projectRepository.findById(projectId);
        if (!projectOptional.isPresent()) {
            return ResponseEntity.badRequest().body(new ErrorMessage(PROJECTNOTFOUND));
        }

        Project project = projectOptional.get();
        if (!project.getAccount().getId().equals(currentUser.getId())) {
            return new ResponseEntity<>(new ErrorMessage(UNAUTHORIZEDUSER), HttpStatus.BAD_REQUEST);
        }

        this.projectRepository.delete(project);
        return ResponseEntity.ok().build();
    }
}
