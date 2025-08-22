package com.example.demo.repo;

import com.example.demo.models.accounts.Account;
import com.example.demo.models.users.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AccountRepository extends JpaRepository<Account, UUID> {
    List<Account> findByUser(User user);
    Page<Account> findByUser(User user, Pageable pageable); // NOVO
    boolean existsByNumber(String number);
    List<Account> findByUser_Id(UUID userId);
}