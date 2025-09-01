package com.crypto.portfolio.services;

import com.crypto.portfolio.entities.CashOfficeOperation;
import com.crypto.portfolio.utils.cashofficeoperation.OperationType;
import com.crypto.portfolio.utils.exceptions.InsufficientBalanceException;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import com.crypto.portfolio.repositories.CashOfficeOperationRepository;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class CashOfficeOperationService {

    private CashOfficeOperationRepository repository;

    @Autowired
    public CashOfficeOperationService(CashOfficeOperationRepository repository) {
        this.repository = repository;
    }

    public CashOfficeOperation saveCashOfficeOperation(CashOfficeOperation operation) {
        if (operation.getOperationType().equals(OperationType.CASH_OUT)
                && getUserBalanceByIdAndCurrencyId(operation.getUser().getId(), operation.getCurrency().getId())
                .compareTo(operation.getOperationQuantity()) < 0) {
            throw new InsufficientBalanceException(
                    "Insufficient balance for user " + operation.getUser().getId() +
                            " in currency " + operation.getCurrency().getId() +
                            ". Requested: " + operation.getOperationQuantity() +
                            ", Available: " + getUserBalanceByIdAndCurrencyId(
                            operation.getUser().getId(),
                            operation.getCurrency().getId())
            );
        }
        return repository.save(operation);
    }

    public Optional<CashOfficeOperation> getCashOfficeOperationById(Integer id) {
        return repository.findById(id);
    }

    public Optional<CashOfficeOperation> getCashOfficeOperationByOperationPasscode(String operationPasscode) {
        return repository.findByOperationPasscode(operationPasscode);
    }

    public void deleteCashOfficeOperationById(Integer id) {
        repository.deleteById(id);
    }

    public Iterable<CashOfficeOperation> getAllCashOfficeOperations() {
        return repository.findAll();
    }

    public BigDecimal getUserBalanceByIdAndCurrencyId(Integer userId, Integer currencyId) {
        BigDecimal cashInSum = repository.findCashInSumsByUserIdAndCurrencyId(userId, currencyId);
        BigDecimal cashOutSum = repository.findCashOutSumsByUserIdAndCurrencyId(userId, currencyId);

        if (cashInSum == null) cashInSum = BigDecimal.ZERO;
        if (cashOutSum == null) cashOutSum = BigDecimal.ZERO;

        return cashInSum.subtract(cashOutSum);
    }
}
