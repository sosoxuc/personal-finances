package personal.finances.transactions;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import personal.finances.projects.Project;
import personal.security.AdminRole;
import personal.security.UserRole;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/transaction")
public class TransactionService {

    private static SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");

    @PersistenceContext
    private EntityManager em;

    @RequestMapping("/create")
    @Transactional(rollbackFor = Throwable.class)
    public Integer create(
            @RequestParam BigDecimal amount,
            @RequestParam Integer projectId,
            @RequestParam String date) throws ParseException {
        Transaction transaction = new Transaction();
        Date transactionDate = df.parse(date);
        transaction.userDate = new Date();
        transaction.transactionAmount = amount;
        transaction.transactionDate = transactionDate;

        //set project
        Project project = em.find(Project.class, projectId);
        transaction.projectId = projectId;
        transaction.projectName = project.projectName;

        //set order
        List<Transaction> lastTransaction = em.createQuery(
                "from Transaction where transactionDate = :transactionDate order by transactionOrder desc", Transaction.class)
                .setParameter("transactionDate", transactionDate)
                .setFirstResult(0)
                .setMaxResults(1)
                .getResultList();

        if ( ! lastTransaction.isEmpty()) {
            Integer transactionOrder = lastTransaction.get(0).transactionOrder;
            transaction.transactionOrder = transactionOrder + 1;
        } else {
            transaction.transactionOrder = 0;
        }

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
