package com.crypto.portfolio.repositories;

import com.crypto.portfolio.entities.Currency;
import com.crypto.portfolio.utils.currencies.CurrencyTicker;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface CurrencyRepository extends CrudRepository<Currency, Integer>{
    Optional<Currency> findByTicker(CurrencyTicker ticker);
}
