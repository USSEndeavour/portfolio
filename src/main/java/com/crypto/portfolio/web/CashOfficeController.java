package com.crypto.portfolio.web;

import com.crypto.portfolio.entities.CashOffice;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import com.crypto.portfolio.services.CashOfficeService;

import java.util.Optional;

@RestController
@RequestMapping("/cashoffices")
public class CashOfficeController {

    private CashOfficeService service;

    @Autowired
    public CashOfficeController(CashOfficeService service) {
        this.service = service;
    }

    @GetMapping("/{id}")
    public Optional<CashOffice> getCashOffice(@PathVariable Integer id) {
        return service.getCashOfficeById(id);
    }

    @PostMapping("/add")
    @ResponseStatus(HttpStatus.CREATED)
    public void addCashOffice(@RequestBody CashOffice office) {
        service.addCashOffice(office);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteCashOffice(@PathVariable Integer id) {
        service.deleteCashOfficeById(id);
    }

    @GetMapping
    public Iterable<CashOffice> getAllCashOffices() {
        return service.getAllCashOffices();
    }

}
