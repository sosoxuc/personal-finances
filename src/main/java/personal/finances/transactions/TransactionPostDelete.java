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
                " and r.referenceId = :referenceId and r.transactionDate >= :transactionDate" +
                " and r.transactionId not in (select t.id from Transaction t where t.isActive = :isActive and t.transactionDate = :transactionDate and t.transactionOrder <= :transactionOrder)")
                .setParameter("rest", transaction.transactionAmount)
                .setParameter("isActive", States.ACTIVE)
                .setParameter("referenceId", transaction.projectId)
                .setParameter("transactionDate", transaction.transactionDate)
                .setParameter("transactionRestType", TransactionRestType.PROJECT)
                .setParameter("transactionOrder", transaction.transactionOrder)
                .executeUpdate();



        //Account

        em.createQuery("UPDATE TransactionRest r set r.transactionRest = r.transactionRest - :rest" +
                "  WHERE r.isActive = :isActive and r.transactionRestType = :transactionRestType" +
                " and r.referenceId = :referenceId and r.transactionDate >= :transactionDate " +
                " and r.transactionId not in (select t.id from Transaction t where t.isActive = :isActive and t.transactionDate = :transactionDate and t.transactionOrder <= :transactionOrder)")
                .setParameter("rest", transaction.transactionAmount)
                .setParameter("isActive", States.ACTIVE)
                .setParameter("referenceId", transaction.accountId)
                .setParameter("transactionDate", transaction.transactionDate)
                .setParameter("transactionRestType", TransactionRestType.ACCOUNT)
                .setParameter("transactionOrder", transaction.transactionOrder)
                .executeUpdate();
        //All
        em.createQuery("UPDATE TransactionRest r set r.transactionRest = r.transactionRest - :rest" +
                "  WHERE r.isActive = :isActive and r.transactionRestType = :transactionRestType" +
                " and r.referenceId is null and r.transactionDate >= :transactionDate " +
                " and r.transactionId not in (select t.id from Transaction t where t.isActive = :isActive and t.transactionDate = :transactionDate and t.transactionOrder <= :transactionOrder)")
                .setParameter("rest", transaction.transactionAmount)
                .setParameter("isActive", States.ACTIVE)
                .setParameter("transactionDate", transaction.transactionDate)
                .setParameter("transactionRestType", TransactionRestType.ALL)
                .setParameter("transactionOrder", transaction.transactionOrder)
                .executeUpdate();

        //Currency
        em.createQuery("UPDATE TransactionRest r set r.transactionRest = r.transactionRest - :rest" +
                "  WHERE r.isActive = :isActive and r.transactionRestType = :transactionRestType" +
                " and r.referenceId = :referenceId and r.transactionDate >= :transactionDate " +
                " and r.transactionId not in (select t.id from Transaction t where t.isActive = :isActive and t.transactionDate = :transactionDate and t.transactionOrder <= :transactionOrder)")
                .setParameter("rest", transaction.transactionAmount)
                .setParameter("isActive", States.ACTIVE)
                .setParameter("referenceId", transaction.currencyId)
                .setParameter("transactionDate", transaction.transactionDate)
                .setParameter("transactionRestType", TransactionRestType.CURRENCY)
                .setParameter("transactionOrder", transaction.transactionOrder)
                .executeUpdate();
    }
}
