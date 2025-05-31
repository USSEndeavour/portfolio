package com.crypto.portfolio.web;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import com.crypto.portfolio.services.CashOfficeOperationService;
import com.crypto.portfolio.entities.CashOfficeOperation;

import java.util.Optional;
import java.lang.Iterable;

@RestController
@RequestMapping("/cashofficeoperations")
public class CashOfficeOperationController {

    private CashOfficeOperationService service;

    @Autowired
    public CashOfficeOperationController(CashOfficeOperationService service) {
        this.service = service;
    }

    @GetMapping("/{id}")
    public Optional<CashOfficeOperation> getCashOfficeOperation(@PathVariable Integer id) {
        return service.getCashOfficeOperationById(id);
    }

    @PostMapping("/add")
    @ResponseStatus(HttpStatus.CREATED)
    public CashOfficeOperation addCashOfficeOperation(@RequestBody CashOfficeOperation operation) {
        return service.addCashOfficeOperation(operation);
    }

    @DeleteMapping("/delete/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCashOfficeOperation(@PathVariable Integer id) {
        service.deleteCashOfficeOperationById(id);
    }

    @GetMapping
    public Iterable<CashOfficeOperation> getAllCashOfficeOperations() {
        return service.getAllCashOfficeOperations();
    }
}
