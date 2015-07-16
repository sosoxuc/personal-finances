package personal.finances.currency;

import javax.persistence.EntityManager;

/**
 * Created by niko on 7/15/15.
 */
public class UpdatePostProcessor {

    private EntityManager em;
    private Currency currency;

    public UpdatePostProcessor(EntityManager em, Currency currency) {
        this.em = em;
        this.currency = currency;
    }

    void process(){
        //update currency code into transactions
        updateTransactions();
    }

    private int updateTransactions(){
        int affectedRows = em.createQuery("update Transaction set currencyCode = :currencyCode where currencyId = :currencyId")
                .setParameter("currencyCode", currency.currencyCode)
                .setParameter("currencyId", currency.id)
                .executeUpdate();
        return affectedRows;
    }
}
