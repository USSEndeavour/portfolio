package com.crypto.portfolio.repositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.crypto.portfolio.entities.CashOfficeOperation;

import java.math.BigDecimal;
import java.util.Optional;

public interface CashOfficeOperationRepository extends CrudRepository<CashOfficeOperation, Integer> {
    Optional<CashOfficeOperation> findByOperationPasscode(String operationPasscode);

    @Query(value = "SELECT SUM(operation_quantity) FROM CASH_OFFICE_OPERATIONS c WHERE c.operation_status = \"COMPLETED\" AND c.operation_type = \"CASH_IN\" AND c.user_id=?1 AND c.currency_id=?2", nativeQuery = true)
    BigDecimal findCashInSumsByUserIdAndCurrencyId(Integer userId, Integer currencyId);

    @Query(value = "SELECT SUM(operation_quantity) FROM CASH_OFFICE_OPERATIONS c WHERE c.operation_status = \"COMPLETED\" AND c.operation_type = \"CASH_OUT\" AND c.user_id=?1 AND c.currency_id=?2", nativeQuery = true)
    BigDecimal findCashOutSumsByUserIdAndCurrencyId(Integer id, Integer currencyId);
}
