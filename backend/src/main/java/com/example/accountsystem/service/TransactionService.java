package com.example.accountsystem.service;

import com.example.accountsystem.dto.TransactionRequest;
import com.example.accountsystem.dto.TransactionResponse;
import com.example.accountsystem.dto.TransferRequest;
import com.example.accountsystem.exception.BadRequestException;
import com.example.accountsystem.model.Transaction;
import com.example.accountsystem.model.User;
import com.example.accountsystem.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserService userService;

    @Transactional
    public TransactionResponse deposit(Long userId, TransactionRequest request) {
        User user = userService.findById(userId);

        if (request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("金額必須大於 0");
        }

        // 更新餘額
        BigDecimal newBalance = user.getBalance().add(request.getAmount());
        userService.updateBalance(userId, newBalance);

        // 建立交易記錄
        Transaction transaction = new Transaction();
        transaction.setUser(user);
        transaction.setType(Transaction.TransactionType.DEPOSIT);
        transaction.setAmount(request.getAmount());
        transaction.setDescription(request.getDescription() != null ? request.getDescription() : "存款");

        Transaction savedTransaction = transactionRepository.save(transaction);

        return mapToResponse(savedTransaction);
    }

    @Transactional
    public TransactionResponse withdraw(Long userId, TransactionRequest request) {
        User user = userService.findById(userId);

        if (request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("金額必須大於 0");
        }

        if (user.getBalance().compareTo(request.getAmount()) < 0) {
            throw new BadRequestException("餘額不足");
        }

        // 更新餘額
        BigDecimal newBalance = user.getBalance().subtract(request.getAmount());
        userService.updateBalance(userId, newBalance);

        // 建立交易記錄
        Transaction transaction = new Transaction();
        transaction.setUser(user);
        transaction.setType(Transaction.TransactionType.WITHDRAW);
        transaction.setAmount(request.getAmount());
        transaction.setDescription(request.getDescription() != null ? request.getDescription() : "提款");

        Transaction savedTransaction = transactionRepository.save(transaction);

        return mapToResponse(savedTransaction);
    }

    @Transactional
    public TransactionResponse transfer(Long fromUserId, TransferRequest request) {
        User fromUser = userService.findById(fromUserId);
        User toUser = userService.findByEmail(request.getToEmail());

        if (request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("金額必須大於 0");
        }

        if (fromUser.getEmail().equals(request.getToEmail())) {
            throw new BadRequestException("不能轉帳給自己");
        }

        if (fromUser.getBalance().compareTo(request.getAmount()) < 0) {
            throw new BadRequestException("餘額不足");
        }

        // 扣款
        BigDecimal fromNewBalance = fromUser.getBalance().subtract(request.getAmount());
        userService.updateBalance(fromUserId, fromNewBalance);

        // 入帳
        BigDecimal toNewBalance = toUser.getBalance().add(request.getAmount());
        userService.updateBalance(toUser.getId(), toNewBalance);

        // 建立轉出交易記錄
        Transaction outTransaction = new Transaction();
        outTransaction.setUser(fromUser);
        outTransaction.setType(Transaction.TransactionType.TRANSFER_OUT);
        outTransaction.setAmount(request.getAmount());
        outTransaction.setDescription(request.getDescription() != null ? request.getDescription() : "轉帳");
        outTransaction.setToUserEmail(toUser.getEmail());
        transactionRepository.save(outTransaction);

        // 建立轉入交易記錄
        Transaction inTransaction = new Transaction();
        inTransaction.setUser(toUser);
        inTransaction.setType(Transaction.TransactionType.TRANSFER_IN);
        inTransaction.setAmount(request.getAmount());
        inTransaction.setDescription(request.getDescription() != null ? request.getDescription() : "轉帳");
        inTransaction.setFromUserEmail(fromUser.getEmail());
        transactionRepository.save(inTransaction);

        return mapToResponse(outTransaction);
    }

    public List<TransactionResponse> getUserTransactions(Long userId) {
        return transactionRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private TransactionResponse mapToResponse(Transaction transaction) {
        return new TransactionResponse(
                transaction.getId(),
                transaction.getUser().getId(),
                transaction.getType().name().toLowerCase().replace('_', '-'),
                transaction.getAmount(),
                transaction.getDescription(),
                transaction.getToUserEmail(),
                transaction.getFromUserEmail(),
                transaction.getCreatedAt()
        );
    }
}
