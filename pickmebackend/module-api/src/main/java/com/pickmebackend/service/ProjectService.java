package com.pickmebackend.service;

import com.pickmebackend.domain.Account;
import com.pickmebackend.domain.Project;
import com.pickmebackend.domain.dto.project.ProjectRequestDto;
import com.pickmebackend.domain.dto.project.ProjectResponseDto;
import com.pickmebackend.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

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

    public ProjectResponseDto updateProject(Project project, ProjectRequestDto projectRequestDto) {
        modelMapper.map(projectRequestDto, project);
        Project modifiedProject = this.projectRepository.save(project);
        ProjectResponseDto projectResponseDto = modelMapper.map(modifiedProject, ProjectResponseDto.class);

        return projectResponseDto;
    }

    public ProjectResponseDto deleteProject(Project project) {
        ProjectResponseDto projectResponseDto = modelMapper.map(project, ProjectResponseDto.class);
        this.projectRepository.delete(project);

        return projectResponseDto;
    }
}
