package com.pickmebackend.annotation.project;

import com.pickmebackend.common.ErrorsFormatter;
import com.pickmebackend.domain.Account;
import com.pickmebackend.domain.Project;
import com.pickmebackend.domain.dto.project.ProjectRequestDto;
import com.pickmebackend.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static com.pickmebackend.error.ErrorMessage.PROJECT_NOT_FOUND;
import static com.pickmebackend.error.ErrorMessage.UNAUTHORIZED_USER;

@RequiredArgsConstructor
@Component
@Aspect
public class ProjectAspect {

    private final ProjectRepository projectRepository;

    private final ErrorsFormatter errorsFormatter;

    @Pointcut("@annotation(ProjectValidation)")
    public void projectValidation() {}

    @Around("projectValidation() && args(projectId, projectRequestDto, currentUser)")
    public Object updateProject(ProceedingJoinPoint joinPoint, Long projectId,
                                ProjectRequestDto projectRequestDto, Account currentUser) throws Throwable {
        Optional<Project> projectOptional = this.projectRepository.findById(projectId);
        if (!projectOptional.isPresent()) {
            return errorsFormatter.badRequest(PROJECT_NOT_FOUND.getValue());
        }

        Project project = projectOptional.get();
        if (!project.getAccount().getId().equals(currentUser.getId())) {
            return errorsFormatter.badRequest(UNAUTHORIZED_USER.getValue());
        }
        return joinPoint.proceed();
    }

    @Around("projectValidation() && args(projectId, currentUser)")
    public Object deleteProject(ProceedingJoinPoint joinPoint, Long projectId, Account currentUser) throws Throwable {
        Optional<Project> projectOptional = this.projectRepository.findById(projectId);
        if (!projectOptional.isPresent()) {
            return errorsFormatter.badRequest(PROJECT_NOT_FOUND.getValue());
        }

        Project project = projectOptional.get();
        if (!project.getAccount().getId().equals(currentUser.getId())) {
            return errorsFormatter.badRequest(UNAUTHORIZED_USER.getValue());
        }
        return joinPoint.proceed();
    }
}
