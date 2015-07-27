package personal.finances.transactions;

import personal.States;
import personal.finances.transactions.rest.TransactionRestType;

import javax.persistence.EntityManager;

/**
 * Created by niko on 7/18/15.
 */
public class TransactionPostDelete {
    private EntityManager em;
    private Transaction transaction;

    public TransactionPostDelete(EntityManager em, Transaction transaction) {
        this.em = em;
        this.transaction = transaction;
    }

    public void updateRests(){

        //Project
        em.createQuery("UPDATE TransactionRest r set r.transactionRest = r.transactionRest - :rest" +
                "  WHERE r.isActive = :isActive and r.transactionRestType = :transactionRestType" +
                " and r.referenceId = :referenceId and r.transactionDate >= :transactionDate ")
                .setParameter("rest", transaction.transactionAmount)
                .setParameter("isActive", States.ACTIVE)
                .setParameter("referenceId", transaction.projectId)
                .setParameter("transactionDate", transaction.transactionDate)
                .setParameter("transactionRestType", TransactionRestType.PROJECT)
                .executeUpdate();



        //Account

        em.createQuery("UPDATE TransactionRest r set r.transactionRest = r.transactionRest - :rest" +
                "  WHERE r.isActive = :isActive and r.transactionRestType = :transactionRestType" +
                " and r.referenceId = :referenceId and r.transactionDate >= :transactionDate ")
                .setParameter("rest", transaction.transactionAmount)
                .setParameter("isActive", States.ACTIVE)
                .setParameter("referenceId", transaction.accountId)
                .setParameter("transactionDate", transaction.transactionDate)
                .setParameter("transactionRestType", TransactionRestType.ACCOUNT)
                .executeUpdate();
        //All
        em.createQuery("UPDATE TransactionRest r set r.transactionRest = r.transactionRest - :rest" +
                "  WHERE r.isActive = :isActive and r.transactionRestType = :transactionRestType" +
                " and r.referenceId is null and r.transactionDate >= :transactionDate ")
                .setParameter("rest", transaction.transactionAmount)
                .setParameter("isActive", States.ACTIVE)
                .setParameter("transactionDate", transaction.transactionDate)
                .setParameter("transactionRestType", TransactionRestType.ALL)
                .executeUpdate();

        //Currency
        em.createQuery("UPDATE TransactionRest r set r.transactionRest = r.transactionRest - :rest" +
                "  WHERE r.isActive = :isActive and r.transactionRestType = :transactionRestType" +
                " and r.referenceId = :referenceId and r.transactionDate >= :transactionDate ")
                .setParameter("rest", transaction.transactionAmount)
                .setParameter("isActive", States.ACTIVE)
                .setParameter("referenceId", transaction.currencyId)
                .setParameter("transactionDate", transaction.transactionDate)
                .setParameter("transactionRestType", TransactionRestType.CURRENCY)
                .executeUpdate();
    }
}
