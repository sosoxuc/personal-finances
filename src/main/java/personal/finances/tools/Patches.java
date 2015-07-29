package personal.finances.tools;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import personal.finances.transactions.Transaction;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@RestController
@RequestMapping("/patch")
public class Patches {

    @PersistenceContext
    private EntityManager em;

    @RequestMapping(value = "/1", method = RequestMethod.POST)
    @Transactional(rollbackFor = Throwable.class)
    public void patch1() {
        // Update project version
        em.createQuery(
                "update Project p set p.version = :version where p.version is null")
                .setParameter("version", 1L).executeUpdate();

        // Update currency version
        em.createQuery(
                "update Currency c set c.version = :version where c.version is null")
                .setParameter("version", 1L).executeUpdate();

        // Update account version
        em.createQuery(
                "update Account e set e.version = :version where e.version is null")
                .setParameter("version", 1L).executeUpdate();

        // Update transactions version
        em.createQuery(
                "update Transaction t set t.version = :version where t.version is null")
                .setParameter("version", 1L).executeUpdate();

    }

    @RequestMapping(value = "/2", method = RequestMethod.POST)
    @Transactional(rollbackFor = Throwable.class)
    public void patch2() {
        List<Transaction> transactions = em.createQuery(
                "select t from  Transaction t order by t.transactionDate , t.transactionOrder",
                Transaction.class).getResultList();

        int order = 0;

        for (Transaction transaction : transactions) {
            order++;
            transaction.transactionOrder = order;
            transaction.version = transaction.version + 1;
            // em.merge(transaction);
        }
    }
}
