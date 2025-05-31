package com.crypto.portfolio.web;

import java.util.Optional;
import java.lang.Iterable;

import com.crypto.portfolio.utils.currencies.CurrencyTicker;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import com.crypto.portfolio.entities.Currency;
import com.crypto.portfolio.services.CurrencyService;

@RestController
@RequestMapping("/currencies")
public class CurrencyController {

    private CurrencyService service;

    @Autowired
    public CurrencyController(CurrencyService service) {
        this.service = service;
    }

    @GetMapping("/{id}")
    public Optional<Currency> getCurrency(@PathVariable Integer id){
        return service.getCurrencyById(id);
    }

    @GetMapping("/ticker/{ticker}")
    public Optional<Currency> getCurrency(@PathVariable CurrencyTicker ticker){
        return service.getCurrencyByTicker(ticker);
    }

    @PostMapping("/add")
    @ResponseStatus(HttpStatus.CREATED)
    public void addCurrency(@RequestBody Currency currency){
        service.addCurrency(currency);
    }

    @DeleteMapping("delete/{id}")
    public void deleteCurrency(@PathVariable Integer id){
        service.deleteCurrencyById(id);
    }

    @GetMapping
    public Iterable<Currency> getAllCurrencies(){
        return service.getAllCurrencies();
    }
}
