package com.hoon.ppmtool.services;

import com.hoon.ppmtool.domain.Project;
import com.hoon.ppmtool.domain.User;
import com.hoon.ppmtool.exeptions.ProjectIdException;
import com.hoon.ppmtool.exeptions.ProjectNotFoundException;
import com.hoon.ppmtool.repositories.ProjectRepository;
import com.hoon.ppmtool.repositories.UserRepository;
import com.hoon.ppmtool.security.IAuthenticationFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private IAuthenticationFacade authenticationFacade;

    private String getAuthenticatedUsername() {
        Authentication authentication = authenticationFacade.getAuthentication();
        return authentication.getName();
    }

    private User getAuthenticatedUserByName() {
        return userRepository.findByUsername(getAuthenticatedUsername());
    }

    private Project createProject(Project project) {
        try {
            User authUser = getAuthenticatedUserByName();

            // set authenticated user information
            project.setUser(authUser);
            project.setProjectLeader(authUser.getUsername());

            return projectRepository.save(project);
        } catch (Exception e) {
            throw new ProjectIdException("Project ID '" + project.getProjectIdentifier() + "' already exists");
        }
    }

    private Project updateProject(Project newProject) {
        User authUser = getAuthenticatedUserByName();
        Project oldProject = projectRepository.findByProjectIdentifier(newProject.getProjectIdentifier().toUpperCase());

        if (oldProject == null || !oldProject.getProjectLeader().equals(authUser.getUsername())) {
            throw new ProjectNotFoundException("Project not found in your account.");
        }
        // updating missing values from client request
        // Need to implement patching logic for partial project
        newProject.setUser(oldProject.getUser());
        newProject.setProjectLeader(authUser.getUsername());
        newProject.setProjectTaskSequence(oldProject.getProjectTaskSequence());

        try {
            return projectRepository.save(newProject);
        } catch (Exception e) {
            throw new ProjectIdException("Project ID '" + newProject.getProjectIdentifier() + "' failed to update!");
        }
    }

    public Project saveOrUpdateProject(Project project) {
        if (project.getId() == null) {
            return createProject(project);
        }
        return updateProject(project);
    }

    public Project findProjectByIdentifier(String projectIdentifier) {
        Project project = projectRepository.findByProjectIdentifier(projectIdentifier.toUpperCase());
        String authUsername = authenticationFacade.getAuthentication().getName();

        if (project == null) {
            throw new ProjectIdException("Project ID[" + projectIdentifier + "] does not exist");
        }
        if (!project.getProjectLeader().equals(authUsername)) {
            throw new ProjectNotFoundException("Project not found in your account");
        }
        return project;
    }

    public Iterable<Project> findAllProjects() {
        String authUsername = authenticationFacade.getAuthentication().getName();
        return projectRepository.findAllByProjectLeader(authUsername);
    }

    public void deleteProjectByIdentifier(String projectIdentifier) {
        projectRepository.delete(findProjectByIdentifier(projectIdentifier));
    }
}
