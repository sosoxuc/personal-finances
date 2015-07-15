package personal.finances.accounts;

import personal.finances.currency.Currency;

import javax.persistence.EntityManager;

/**
 * Created by niko on 7/15/15.
 */
public class UpdatePostProcessor {

    private EntityManager em;
    private Account account;

    public UpdatePostProcessor(EntityManager em, Account account) {
        this.em = em;
        this.account = account;
    }

    void process(){
        //update account code into transactions
        updateTransactions();
    }

    private int updateTransactions(){
        int affectedRows = em.createQuery("update Transaction set accountName = :accountName where accountId = :accountId")
                .setParameter("accountName", account.accountName)
                .setParameter("accountId", account.id)
                .executeUpdate();
        return affectedRows;
    }
}
