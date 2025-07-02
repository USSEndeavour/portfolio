package com.crypto.portfolio.repositories;

import org.springframework.data.repository.CrudRepository;

import com.crypto.portfolio.entities.CashOfficeOperation;

import java.util.Optional;

public interface CashOfficeOperationRepository extends CrudRepository<CashOfficeOperation, Integer> {
    Optional<CashOfficeOperation> findByOperationPasscode(String operationPasscode);
}
