package com.hoon.ppmtool.services;

import com.hoon.ppmtool.domain.Project;
import com.hoon.ppmtool.domain.ProjectTask;
import com.hoon.ppmtool.exeptions.ProjectIdException;
import com.hoon.ppmtool.exeptions.ProjectNotFoundException;
import com.hoon.ppmtool.repositories.ProjectTaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProjectTaskService {

    @Autowired
    private ProjectTaskRepository projectTaskRepository;

    @Autowired
    private ProjectService projectService;

    public ProjectTask addProjectTask(String projectIdentifier, ProjectTask projectTask) {
        if (projectTask.getId() != null)
            throw new ProjectIdException("Invalid ID[" + projectTask.getId() + "] value exception.");

        Project project = projectService.findProjectByIdentifier(projectIdentifier);

        // Increase the project task sequence number before adding project task.
        project.increaseProjectTaskSequence();

        // set project task information
        projectTask.setProject(project);
        projectTask.setProjectSequence(projectIdentifier + "-" + project.getProjectTaskSequence());
        projectTask.setProjectIdentifier(projectIdentifier);

        return projectTaskRepository.save(projectTask);
    }

    public Iterable<ProjectTask> findProjectTasksByProjectId(String projectIdentifier) {
        if (projectService.findProjectByIdentifier(projectIdentifier) == null)
            throw new ProjectNotFoundException("Project ID[" + projectIdentifier + "] does not exist.");

        return projectTaskRepository.findByProjectIdentifierOrderByPriority(projectIdentifier);
    }

    public ProjectTask findProjectTaskByProjectSequence(String projectIdentifier, String projectSequence) {
        // check project identifier validation
        if (projectService.findProjectByIdentifier(projectIdentifier) == null)
            throw new ProjectNotFoundException("Project ID[" + projectIdentifier + "] does not exist.");

        ProjectTask projectTask = projectTaskRepository.findByProjectSequence(projectSequence);

        if (projectTask == null) {
            throw new ProjectNotFoundException("Project Task ID[" + projectSequence + "] does not exist.");
        }
        if (!projectTask.getProjectIdentifier().equals(projectIdentifier)) {
            throw new ProjectNotFoundException("Project Task ID[" + projectSequence + "] does not exist in project");
        }
        return projectTask;
    }

    public ProjectTask updateProjectTaskByProjectSequence(ProjectTask updatedTask, String projectIdentifier, String projectSequence) {
        // get old project task
        ProjectTask oldProjectTask = this.findProjectTaskByProjectSequence(projectIdentifier, projectSequence);

        // check projectTask Id, projectIdentifier, projectSequence number
        if ((oldProjectTask.getId() != updatedTask.getId())
                || !oldProjectTask.getProjectIdentifier().equals(updatedTask.getProjectIdentifier())
                || !oldProjectTask.getProjectSequence().equals(updatedTask.getProjectSequence())) {
            throw new ProjectNotFoundException("Invalid project task with ID: '" + projectSequence + ". Fail to update");
        }
        return projectTaskRepository.save(updatedTask);
    }

    public void deleteProjectTaskByProjectSequence(String projectIdentifier, String projectSequence) {
        ProjectTask projectTask = this.findProjectTaskByProjectSequence(projectIdentifier, projectSequence);
        projectTaskRepository.delete(projectTask);
    }
}
