package com.example.taskmanager.task.dto;

import com.example.taskmanager.task.TaskStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record UpdateTaskStatusRequest(
        @Schema(
                description = "New task status",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull TaskStatus status
) {}