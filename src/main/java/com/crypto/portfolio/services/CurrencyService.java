package com.crypto.portfolio.services;

import com.crypto.portfolio.entities.Currency;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import com.crypto.portfolio.repositories.CurrencyRepository;
import com.crypto.portfolio.utils.currencies.CurrencyTicker;

import java.util.Optional;
import java.lang.Iterable;

@Service
public class CurrencyService {

    private CurrencyRepository repository;

    @Autowired
    public CurrencyService(CurrencyRepository repository) {
        this.repository = repository;
    }

    public Currency addCurrency(Currency currency) {
        return repository.save(currency);
    }

    public Optional<Currency> getCurrencyById(Integer id) {
        return repository.findById(id);
    }

    public Optional<Currency> getCurrencyByTicker(CurrencyTicker ticker) {
        return repository.findByTicker(ticker);
    }

    public void deleteCurrencyById(Integer id) {
        repository.deleteById(id);
    }

    public Iterable<Currency> getAllCurrencies() {
        return repository.findAll();
    }
}
