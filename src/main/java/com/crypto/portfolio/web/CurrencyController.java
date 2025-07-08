package com.crypto.portfolio.web;

import java.util.List;
import java.util.Optional;
import java.lang.Iterable;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.crypto.portfolio.utils.currencies.CurrencyTicker;
import org.checkerframework.checker.units.qual.C;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<?> getCurrencyById(@PathVariable Integer id) {
        Optional<Currency> currency = service.getCurrencyById(id);
        if (currency.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("CurrencyNotFound");
        }
        return ResponseEntity.status(HttpStatus.OK).body(currency);
    }

    @GetMapping("/ticker/{ticker}")
    public ResponseEntity<?> getCurrencyByTicker(@PathVariable CurrencyTicker ticker) {
        Optional<Currency> currency = service.getCurrencyByTicker(ticker);
        if (currency.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("CurrencyNotFound");
        }
        return ResponseEntity.ok(currency);
    }

    @PostMapping("/add")
    public ResponseEntity<?> addCurrency(@RequestBody Currency currency) {
        if (!service.getCurrencyByTicker(currency.getTicker()).isEmpty()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("The currency already exists");
        }
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.addCurrency(currency));
    }

    @GetMapping
    public ResponseEntity<?> getAllCurrencies() {
        List<Currency> result =
                StreamSupport.stream(service.getAllCurrencies().spliterator(), false)
                        .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("delete/{id}")
    public ResponseEntity<?> deleteCurrency(@PathVariable Integer id) {
        if (service.getCurrencyById(id).isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        service.deleteCurrencyById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
