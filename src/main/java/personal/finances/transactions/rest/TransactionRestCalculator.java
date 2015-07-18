package personal.finances.transactions.rest;

import personal.States;
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
                "select e from Transaction e where e.projectId = :projectId and isActive = :isActive and e.transactionDate <= :transactionDate order by transactionOrder desc", Transaction.class)
                .setParameter("isActive", States.ACTIVE)
                .setParameter("projectId", transaction.projectId)
                .setParameter("transactionDate", transaction.transactionDate)
                .setFirstResult(0)
                .setMaxResults(1)
                .getResultList();

        return transactions.isEmpty() ? null : transactions.get(0);

    }

    public Transaction lastAccountScopeTransaction(){
        List<Transaction> transactions = em.createQuery(
                "select e from Transaction e where e.accountId = :accountId and e.transactionDate <= :transactionDate and e.isActive = :isActive order by transactionOrder desc", Transaction.class)
                .setParameter("accountId", transaction.accountId)
                .setParameter("transactionDate", transaction.transactionDate)
                .setParameter("isActive", States.ACTIVE)
                .setFirstResult(0)
                .setMaxResults(1)
                .getResultList();

        return transactions.isEmpty() ? null : transactions.get(0);
    }

    public Transaction lastCurrencyScopeTransaction(){
        List<Transaction> transactions = em.createQuery(
                "select e from Transaction e where e.accountId = :accountId and e.currencyId =:currencyId and e.transactionDate <= :transactionDate and e.isActive = :isActive order by transactionOrder desc", Transaction.class)
                .setParameter("accountId", transaction.accountId)
                .setParameter("isActive", States.ACTIVE)
                .setParameter("currencyId", transaction.currencyId)
                .setParameter("transactionDate", transaction.transactionDate)
                .setFirstResult(0)
                .setMaxResults(1)
                .getResultList();

        return transactions.isEmpty() ? null : transactions.get(0);
    }

    public Transaction getLastTransaction(){
        List<Transaction> transactions = em.createQuery(
                "select e from Transaction e where e.isActive = :isActive and e.transactionDate <= :transactionDate order by transactionOrder desc", Transaction.class)
                .setParameter("isActive", States.ACTIVE)
                .setParameter("transactionDate", transaction.transactionDate)
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

        //Project
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

        //Currency
        tr = new TransactionRest();
        tr.transactionRestType = TransactionRestType.CURRENCY;
        transactionRests.add(tr);

        Transaction currencyScopedTransaction = lastCurrencyScopeTransaction();
        if (currencyScopedTransaction != null) {
            TransactionRest currencyScoped = extract(currencyScopedTransaction.transactionRests, TransactionRestType.CURRENCY);
            tr.transactionRest = transaction.transactionAmount.add(currencyScoped.transactionRest);
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
