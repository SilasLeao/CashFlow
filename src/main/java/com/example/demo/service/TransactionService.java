package com.example.demo.service; // Mantendo seu pacote original

import com.example.demo.models.transactions.Transaction;
import com.example.demo.models.users.User;
import com.example.demo.repo.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Importe esta anotação

import java.util.List;
import java.util.Optional; // Importe o Optional
import java.util.UUID;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

     /**
     * 👇 MÉTODO NOVO ADICIONADO AQUI 👇
     * Busca todas as transações pertencentes a um usuário específico.
     * @param user O usuário cujas transações serão buscadas.
     * @return Uma lista de transações do usuário.
     * 
     * 
     */
    @Transactional(readOnly = true)
    public List<Transaction> findByUser(User user) {
        return transactionRepository.findByAccounts_User(user);
    }



    /**
     * 👇 MÉTODO NOVO ADICIONADO AQUI 👇
     * Busca todas as transações associadas a um ID de conta específico.
     * @param accountId O UUID da conta.
     * @return Uma lista de transações da conta.
     */
    @Transactional(readOnly = true)
    public List<Transaction> findByAccountId(UUID accountId) {
        // Este método 'findByAccounts_Id' já existia no seu repositório!
        return transactionRepository.findByAccounts_Id(accountId);
    }

    @Transactional(readOnly = true)
    public List<Transaction> findAll() {
        return transactionRepository.findAll();
    }

    // Retornando Optional para mais segurança no Controller
    @Transactional(readOnly = true)
    public Optional<Transaction> findById(UUID id) {
        return transactionRepository.findById(id);
    }

    @Transactional
    public Transaction save(Transaction transaction) {
        // Lógica para o comentário: se o texto do comentário for nulo ou vazio,
        // consideramos que não há comentário para não salvar um registro em branco.
        if (transaction.getComment() != null && (transaction.getComment().getText() == null || transaction.getComment().getText().trim().isEmpty())) {
            // Remove a referência ao comentário para que o JPA não tente salvar uma entidade vazia
            // e também para que a cascata (CascadeType.ALL) não cause problemas.
            transaction.setComment(null);
        }
        return transactionRepository.save(transaction);
    }

    @Transactional
    public void deleteById(UUID id) {
        transactionRepository.deleteById(id);
    }
}