package com.example.taskmanager.project;

import java.util.UUID;

import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project, UUID> {
    boolean existsById(@NonNull UUID id);
}