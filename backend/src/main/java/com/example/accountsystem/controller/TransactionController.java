package com.example.accountsystem.controller;

import com.example.accountsystem.dto.TransactionRequest;
import com.example.accountsystem.dto.TransactionResponse;
import com.example.accountsystem.dto.TransferRequest;
import com.example.accountsystem.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@CrossOrigin(origins = "http://localhost:5173")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @PostMapping("/deposit")
    public ResponseEntity<TransactionResponse> deposit(
            @RequestParam Long userId,
            @Valid @RequestBody TransactionRequest request) {
        TransactionResponse transaction = transactionService.deposit(userId, request);
        return new ResponseEntity<>(transaction, HttpStatus.CREATED);
    }

    @PostMapping("/withdraw")
    public ResponseEntity<TransactionResponse> withdraw(
            @RequestParam Long userId,
            @Valid @RequestBody TransactionRequest request) {
        TransactionResponse transaction = transactionService.withdraw(userId, request);
        return new ResponseEntity<>(transaction, HttpStatus.CREATED);
    }

    @PostMapping("/transfer")
    public ResponseEntity<TransactionResponse> transfer(
            @RequestParam Long userId,
            @Valid @RequestBody TransferRequest request) {
        TransactionResponse transaction = transactionService.transfer(userId, request);
        return new ResponseEntity<>(transaction, HttpStatus.CREATED);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<TransactionResponse>> getUserTransactions(@PathVariable Long userId) {
        List<TransactionResponse> transactions = transactionService.getUserTransactions(userId);
        return ResponseEntity.ok(transactions);
    }
}
