package ru.nikita.lab2.dao.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import ru.nikita.lab2.dao.entity.OperationEntity;

import java.util.UUID;

public interface OperationRepository
        extends JpaRepository<OperationEntity, UUID>, JpaSpecificationExecutor<OperationEntity> {}
