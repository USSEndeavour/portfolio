package com.crypto.portfolio.repositories;

import org.springframework.data.repository.CrudRepository;

import com.crypto.portfolio.entities.CashOfficeOperation;

public interface CashOfficeOperationRepository extends CrudRepository<CashOfficeOperation, Integer> {
}
