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
        return modelMapper.map(savedProject, ProjectResponseDto.class);
    }

    public ProjectResponseDto updateProject(Project project, ProjectRequestDto projectRequestDto) {
        modelMapper.map(projectRequestDto, project);
        Project modifiedProject = this.projectRepository.save(project);
        return modelMapper.map(modifiedProject, ProjectResponseDto.class);
    }

    public ProjectResponseDto deleteProject(Project project) {
        this.projectRepository.delete(project);
        project.getAccount().getProjects().remove(project);
        return modelMapper.map(project, ProjectResponseDto.class);
    }
}
