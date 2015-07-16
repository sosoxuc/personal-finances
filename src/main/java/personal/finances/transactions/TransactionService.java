package personal.finances.transactions;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import personal.States;
import personal.finances.accounts.Account;
import personal.finances.currency.Currency;
import personal.finances.projects.Project;
import personal.finances.transactions.rest.TransactionRest;
import personal.finances.transactions.rest.TransactionRestCalculator;
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

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @Transactional(rollbackFor = Throwable.class)
    public Integer create(
            @RequestParam BigDecimal amount,
            @RequestParam Integer projectId,
            @RequestParam String date,
            @RequestParam String note,
            @RequestParam Integer direction,
            @RequestParam Integer accountId,
            @RequestParam Integer currencyId
            ) throws ParseException {
        Transaction transaction = new Transaction();
        Date transactionDate = df.parse(date);
        transaction.userDate = new Date();

        // set direction to amount

        transaction.transactionAmount = amount.multiply(new BigDecimal(direction));
        transaction.transactionDate = transactionDate;
        transaction.transactionNote = note;

        //set account
        Account account = em.find(Account.class, accountId);
        transaction.accountId = account.id;
        transaction.accountName = account.accountName;

        //set currency
        Currency currency = em.find(Currency.class, currencyId);
        transaction.currencyId = currency.id;
        transaction.currencyCode = currency.currencyCode;

        //set project
        Project project = em.find(Project.class, projectId);
        transaction.projectId = projectId;
        transaction.projectName = project.projectName;

        //set order
        List<Transaction> dayTransactions = getLastTransactions(transactionDate);

        if ( ! dayTransactions.isEmpty()) {
            Integer transactionOrder = dayTransactions.get(0).transactionOrder;
            transaction.transactionOrder = transactionOrder + 1;
        } else {
            transaction.transactionOrder = 0;
        }

        //set rest
        List<TransactionRest> transactionRests = new TransactionRestCalculator(em, transaction).calculateRests();

        transaction.transactionRests = transactionRests;

        transaction.isActive = States.ACTIVE;
        em.persist(transaction);

        return transaction.id;
    }

    private List<Transaction> getLastTransactions(Date transactionDate) {
        return em.createQuery(
                    "from Transaction where transactionDate = :transactionDate order by transactionOrder desc", Transaction.class)
                .setParameter("transactionDate", transactionDate)
                .setFirstResult(0)
                .setMaxResults(1)
                .getResultList();
    }

    private List<Transaction> getLastTransactions() {
        return em.createQuery(
                "from Transaction order by transactionDate desc, transactionOrder desc", Transaction.class)
                .setFirstResult(0)
                .setMaxResults(1)
                .getResultList();
    }

    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public List<Transaction> search() {
        String qlString = "select distinct t from Transaction t left join fetch t.transactionRests where t.isActive = :isActive order by t.transactionDate desc, t.id desc";
        return em.createQuery(qlString, Transaction.class).setParameter("isActive", States.ACTIVE).getResultList();
    }

    @UserRole
    @AdminRole
    @RequestMapping(value = "/remove", method = RequestMethod.POST)
    @Transactional(rollbackFor = Throwable.class)
    public void remove(@RequestParam Integer id) {
        Transaction transaction = em.find(Transaction.class, id);
        transaction.isActive = States.INACTIVE;
    }
}
