package com.example.demo.repo;

import com.example.demo.models.accounts.Account;
import com.example.demo.models.transactions.Transaction;
import com.example.demo.models.users.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.UUID;
import java.util.List;
import java.util.Date;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
    List<Transaction> findByCategoryId(UUID categoryId);
    List<Transaction> findByAccount_Id(UUID accountId);
    List<Transaction> findByAccount_IdAndDateBetween(UUID accountId, Date start, Date end);
    List<Transaction> findByAccount_User(User user);
    List<Transaction> findByAccountAndDateBetween(Account account, Date startDate, Date endDate);



}