package com.crypto.portfolio.services;

import com.crypto.portfolio.entities.CashOfficeOperation;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import com.crypto.portfolio.repositories.CashOfficeOperationRepository;

import java.util.Optional;

@Service
public class CashOfficeOperationService {

    private CashOfficeOperationRepository repository;

    @Autowired
    public CashOfficeOperationService(CashOfficeOperationRepository repository) {
        this.repository = repository;
    }

    public CashOfficeOperation saveCashOfficeOperation(CashOfficeOperation operation) {
        return repository.save(operation);
    }

    public Optional<CashOfficeOperation> getCashOfficeOperationById(Integer id) {
        return repository.findById(id);
    }

    public void deleteCashOfficeOperationById(Integer id) {
        repository.deleteById(id);
    }

    public Iterable<CashOfficeOperation> getAllCashOfficeOperations() {
        return repository.findAll();
    }
}
