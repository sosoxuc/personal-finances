package personal.finances.transactions.rest;

import personal.finances.transactions.Transaction;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by niko on 7/15/15.
 */

public class TransactionRestCalculator {

    private EntityManager em;

    private Transaction transaction;

    public TransactionRestCalculator(EntityManager em, Transaction transaction) {
        this.em = em;
        this.transaction = transaction;
    }

    public Transaction lastProjectScopeTransaction(){
        List<Transaction> transactions = em.createQuery(
                "select e from Transaction e where e.projectId = :projectId order by transactionOrder desc", Transaction.class)
                .setParameter("projectId", transaction.projectId)
                .setFirstResult(0)
                .setMaxResults(1)
                .getResultList();

        return transactions.isEmpty() ? null : transactions.get(0);

    }

    public Transaction lastAccountScopeTransaction(){
        List<Transaction> transactions = em.createQuery(
                "select e from Transaction e where e.accountId = :accountId order by transactionOrder desc", Transaction.class)
                .setParameter("accountId", transaction.accountId)
                .setFirstResult(0)
                .setMaxResults(1)
                .getResultList();

        return transactions.isEmpty() ? null : transactions.get(0);
    }

    public Transaction getLastTransaction(){
        List<Transaction> transactions = em.createQuery(
                "select e from Transaction e order by transactionOrder desc", Transaction.class)
                .setFirstResult(0)
                .setMaxResults(1)
                .getResultList();

        return transactions.isEmpty() ? null : transactions.get(0);
    }

    public List<TransactionRest> calculateRests(){

        List<TransactionRest> transactionRests  = new ArrayList<>(3);
        TransactionRest tr;

        tr = new TransactionRest();
        tr.transactionRestType = TransactionRestType.PROJECT;
        transactionRests.add(tr);

        Transaction projectScopeTransaction = lastProjectScopeTransaction();
        if (projectScopeTransaction != null) {
            TransactionRest projectRest = extract(projectScopeTransaction.transactionRests, TransactionRestType.PROJECT);
            tr.transactionRest = transaction.transactionAmount.add(projectRest.transactionRest);

        } else {
            tr.transactionRest = transaction.transactionAmount;
        }


        //Account
        tr = new TransactionRest();
        tr.transactionRestType = TransactionRestType.ACCOUNT;
        transactionRests.add(tr);

        Transaction accountScopeTransaction = lastAccountScopeTransaction();
        if (accountScopeTransaction != null) {
            TransactionRest accScope = extract(accountScopeTransaction.transactionRests, TransactionRestType.ACCOUNT);
            tr.transactionRest = transaction.transactionAmount.add(accScope.transactionRest);

        } else {
            tr.transactionRest = transaction.transactionAmount;
        }

        //All
        tr = new TransactionRest();
        tr.transactionRestType = TransactionRestType.ALL;
        transactionRests.add(tr);

        Transaction lastTransaction = getLastTransaction();
        if (lastTransaction != null) {
            TransactionRest all = extract(lastTransaction.transactionRests, TransactionRestType.ALL);
            tr.transactionRest = transaction.transactionAmount.add(all.transactionRest);
        } else {
            tr.transactionRest = transaction.transactionAmount;
        }

        return transactionRests;
    }

    private TransactionRest extract(List<TransactionRest> transactionRests, TransactionRestType type){
        for (TransactionRest transactionRest : transactionRests) {
            if (transactionRest.transactionRestType.equals(type)) {
                return transactionRest;
            }
        }
        return null;
    }

}
