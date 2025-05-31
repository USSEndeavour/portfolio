package com.crypto.portfolio.services;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;
import java.lang.Iterable;

import com.crypto.portfolio.entities.CashOffice;
import com.crypto.portfolio.repositories.CashOfficeRepository;

@Service
public class CashOfficeService {

    private CashOfficeRepository repository;

    @Autowired
    public CashOfficeService(CashOfficeRepository repository) {
        this.repository = repository;
    }

    public void addCashOffice(CashOffice office) {
        repository.save(office);
    }

    public void deleteCashOfficeById(Integer id) {
        repository.deleteById(id);
    }

    public Optional<CashOffice> getCashOfficeById(Integer id) {
        return repository.findById(id);
    }

    public Iterable<CashOffice> getAllCashOffices() {
        return repository.findAll();
    }

}
