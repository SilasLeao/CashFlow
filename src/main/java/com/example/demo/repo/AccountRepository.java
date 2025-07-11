package com.example.demo.repo;

import com.example.demo.models.accounts.Account;
import com.example.demo.models.users.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AccountRepository extends JpaRepository<Account, UUID> {
    List<Account> findByUser(User user);
}
