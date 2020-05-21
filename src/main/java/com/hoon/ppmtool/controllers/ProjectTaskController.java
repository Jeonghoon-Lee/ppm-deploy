package com.hoon.ppmtool.controllers;

import com.hoon.ppmtool.domain.ProjectTask;
import com.hoon.ppmtool.services.MapValidationErrorService;
import com.hoon.ppmtool.services.ProjectTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/project-task/")
@CrossOrigin
public class ProjectTaskController {

    @Autowired
    private ProjectTaskService projectTaskService;

    @Autowired
    private MapValidationErrorService mapValidationErrorService;

    @PostMapping("/{projectId}")
    public ResponseEntity<?> addProjectTask(@Valid @RequestBody ProjectTask projectTask,
                                                     BindingResult result, @PathVariable String projectId) {
        if (result.hasErrors()) return mapValidationErrorService.mapValidationErrorResult(result);

        ProjectTask newProjectTask = projectTaskService.addProjectTask(projectId, projectTask);
        return new ResponseEntity<>(newProjectTask, HttpStatus.CREATED);
    }

    @GetMapping("/{projectId}")
    public Iterable<?> getProjectTasks(@PathVariable String projectId) {
        return projectTaskService.findProjectTasksByProjectId(projectId);
    }

    @GetMapping("/{projectId}/{projectTaskId}")
    public ResponseEntity<?> getProjectTask(@PathVariable String projectId, @PathVariable String projectTaskId) {
        ProjectTask projectTask = projectTaskService.findProjectTaskByProjectSequence(projectId, projectTaskId);
        return new ResponseEntity<>(projectTask, HttpStatus.OK);
    }

    @PatchMapping("/{projectId}/{projectTaskId}")
    public ResponseEntity<?> updateProjectTask(@Valid @RequestBody ProjectTask projectTask, BindingResult result,
                                               @PathVariable String projectId, @PathVariable String projectTaskId) {
        ResponseEntity<?> errorMap = mapValidationErrorService.validateResult(result);
        if (errorMap != null) return errorMap;

        ProjectTask updatedTask = projectTaskService.updateProjectTaskByProjectSequence(projectTask, projectId, projectTaskId);
        return new ResponseEntity<>(updatedTask, HttpStatus.OK);
    }

    @DeleteMapping("/{projectId}/{projectTaskId}")
    public ResponseEntity<String> deleteProjectTask(@PathVariable String projectId, @PathVariable String projectTaskId) {
        projectTaskService.deleteProjectTaskByProjectSequence(projectId, projectTaskId);

        return new ResponseEntity<>("Project Task: " + projectTaskId + " was deleted successfully", HttpStatus.OK);
    }
}
