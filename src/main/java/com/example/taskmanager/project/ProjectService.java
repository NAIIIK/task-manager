package com.example.taskmanager.project;

import com.example.taskmanager.exception.ResourceNotFoundException;
import com.example.taskmanager.project.dto.CreateProjectRequest;
import com.example.taskmanager.project.dto.ProjectResponse;
import com.example.taskmanager.project.dto.UpdateProjectRequest;
import com.example.taskmanager.project.member.ProjectAccessGuard;
import com.example.taskmanager.project.member.ProjectMember;
import com.example.taskmanager.project.member.ProjectMemberRepository;
import com.example.taskmanager.project.member.ProjectRole;
import com.example.taskmanager.user.User;
import com.example.taskmanager.user.UserRepository;
import java.util.List;
import java.util.UUID;

import com.example.taskmanager.util.ExceptionMessages;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final UserRepository userRepository;
    private final ProjectAccessGuard projectAccessGuard;
    private final ProjectMapper projectMapper;

    public ProjectResponse createProject(UUID ownerId, CreateProjectRequest request) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new ResourceNotFoundException(ExceptionMessages.USER_NOT_FOUND_MSG + ownerId));

        Project project = Project.builder()
                .name(request.name())
                .description(request.description())
                .owner(owner)
                .build();
        projectRepository.save(project);

        ProjectMember membership = ProjectMember.builder()
                .project(project)
                .user(owner)
                .role(ProjectRole.OWNER)
                .build();
        projectMemberRepository.save(membership);

        return projectMapper.toResponse(project);
    }

    @Transactional(readOnly = true)
    public List<ProjectResponse> getProjectsForUser(UUID userId) {
        return projectMemberRepository.findAllByUserId(userId).stream()
                .map(member -> projectMapper.toResponse(member.getProject()))
                .toList();
    }

    @Transactional(readOnly = true)
    public ProjectResponse getProject(UUID projectId, UUID requesterId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException(ExceptionMessages.PROJECT_NOT_FOUND_MSG + projectId));

        projectAccessGuard.requireMembership(projectId, requesterId);

        return projectMapper.toResponse(project);
    }

    public ProjectResponse updateProject(UUID projectId, UUID requesterId, UpdateProjectRequest request) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException(ExceptionMessages.PROJECT_NOT_FOUND_MSG + projectId));

        projectAccessGuard.requireRoleAtLeast(projectId, requesterId, ProjectRole.OWNER);

        if (request.name() != null) {
            project.setName(request.name());
        }
        if (request.description() != null) {
            project.setDescription(request.description());
        }

        return projectMapper.toResponse(project);
    }

    public void deleteProject(UUID projectId, UUID requesterId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException(ExceptionMessages.PROJECT_NOT_FOUND_MSG + projectId));

        projectAccessGuard.requireRoleAtLeast(projectId, requesterId, ProjectRole.OWNER);

        projectRepository.delete(project);
    }
}