package com.example.taskmanager.task.dto;

import com.example.taskmanager.task.TaskStatus;

import java.time.LocalDate;
import java.util.List;

public record TaskFilter(
        List<TaskStatus> statuses,
        LocalDate dueDateFrom,
        LocalDate dueDateTo,
        Boolean overdue
) {}