package personal.finances.transactions;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import personal.security.AdminRole;
import personal.security.UserRole;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/transaction")
public class TransactionService {

    @PersistenceContext
    private EntityManager em;

    @RequestMapping("/create")
    @Transactional(rollbackFor = Throwable.class)
    public Integer create(@RequestParam BigDecimal amount) {
        Transaction transaction = new Transaction();
        Date now = new Date();
        transaction.userDate = now;
        transaction.transactionAmount = amount;
        transaction.transactionDate = now;
        em.persist(transaction);
        return transaction.id;
    }

    @RequestMapping("/search")
    public List<Transaction> search() {
        String qlString = "select t from Transaction t order by t.transactionDate desc,t.id desc";
        return em.createQuery(qlString, Transaction.class).getResultList();
    }

    @UserRole
    @AdminRole
    @RequestMapping("/remove")
    @Transactional(rollbackFor = Throwable.class)
    public void remove(@RequestParam Integer id) {
        Transaction transaction = em.find(Transaction.class, id);
        em.remove(transaction);
    }
}
