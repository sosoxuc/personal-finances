package personal.finances.transactions.rest;

import static personal.finances.transactions.TransactionService.df;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import personal.finances.currency.Currency;
import personal.finances.currency.CurrencyService;
import personal.finances.transactions.Transaction;
/**
 * Created by niko on 7/18/15.
 */
@RestController
@RequestMapping("/transaction/rests")
public class TransactionRestService {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    CurrencyService currencyService;

    @RequestMapping("/account")
    public ResponseEntity<TransactionRest> account(
            @RequestParam Integer accountId,
            @RequestParam(required = false) String transactionDate) throws ParseException {

        Date trDate;
        if (transactionDate != null) {
            trDate = df.parse(transactionDate);
        } else {
            trDate = new Date();
        }

        Transaction transaction = new Transaction();
        transaction.accountId = accountId;
        transaction.transactionDate = trDate;

        transaction = new TransactionRestCalculator(em, transaction).lastAccountScopeTransaction();

        if (transaction != null) {
            List<TransactionRest> transactionRests = transaction.transactionRests;
            for (TransactionRest transactionRest : transactionRests) {
                if (transactionRest.transactionRestType.equals(TransactionRestType.ACCOUNT)) {
                    return new ResponseEntity<>(transactionRest, HttpStatus.OK);
                }
            }
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @RequestMapping("/project")
    public ResponseEntity<TransactionRest> project(
            @RequestParam Integer projectId,
            @RequestParam(required = false) String transactionDate) throws ParseException {

        Date trDate;
        if (transactionDate != null) {
            trDate = df.parse(transactionDate);
        } else {
            trDate = new Date();
        }

        Transaction transaction = new Transaction();
        transaction.projectId = projectId;
        transaction.transactionDate = trDate;

        transaction = new TransactionRestCalculator(em, transaction).lastProjectScopeTransaction();

        if (transaction != null) {
            List<TransactionRest> transactionRests = transaction.transactionRests;
            for (TransactionRest transactionRest : transactionRests) {
                if (transactionRest.transactionRestType.equals(TransactionRestType.PROJECT)) {
                    return new ResponseEntity<>(transactionRest, HttpStatus.OK);
                }
            }
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @RequestMapping("/currency")
    public ResponseEntity<TransactionRest> currency(
            @RequestParam Integer currencyId,
            @RequestParam(required = false) String transactionDate) throws ParseException {

        Date trDate;
        if (transactionDate != null) {
            trDate = df.parse(transactionDate);
        } else {
            trDate = new Date();
        }

        Transaction transaction = new Transaction();
        transaction.currencyId = currencyId;
        transaction.transactionDate = trDate;

        transaction = new TransactionRestCalculator(em, transaction).lastCurrencyScopeTransaction();

        if (transaction != null) {
            List<TransactionRest> transactionRests = transaction.transactionRests;
            for (TransactionRest transactionRest : transactionRests) {
                if (transactionRest.transactionRestType.equals(TransactionRestType.CURRENCY)) {
                    return new ResponseEntity<>(transactionRest, HttpStatus.OK);
                }
            }
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @RequestMapping("/currencies")
    public ResponseEntity<List<TransactionRest>> currencies(@RequestParam(required = false) String transactionDate) throws ParseException {
        ResponseEntity<List<Currency>> responseEntity = currencyService.list();
        List<Currency> currencies = responseEntity.getBody();

        if ( ! currencies.isEmpty()) {
            List<TransactionRest> rests = new ArrayList<>();
            for (Currency currency : currencies) {
                ResponseEntity<TransactionRest> currencyResp = currency(currency.id, transactionDate);
                if (currencyResp.getStatusCode().equals(HttpStatus.OK)) {
                    TransactionRest transactionRest = currencyResp.getBody();
                    transactionRest.resourceName = currency.currencyCode;
                    rests.add(transactionRest);
                }
            }

            return new ResponseEntity<>(rests, HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @RequestMapping("/global")
    public ResponseEntity<TransactionRest> global(@RequestParam(required = false) String transactionDate) throws ParseException {

        Date trDate;
        if (transactionDate != null) {
            trDate = df.parse(transactionDate);
        } else {
            trDate = new Date();
        }

        Transaction transaction = new Transaction();
        transaction.transactionDate = trDate;

        transaction = new TransactionRestCalculator(em, transaction).getLastTransaction();

        if (transaction != null) {
            List<TransactionRest> transactionRests = transaction.transactionRests;
            for (TransactionRest transactionRest : transactionRests) {
                if (transactionRest.transactionRestType.equals(TransactionRestType.ALL)) {
                    return new ResponseEntity<>(transactionRest, HttpStatus.OK);
                }
            }
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @RequestMapping("/calculate")
    @Transactional(rollbackFor = Throwable.class)
    public ResponseEntity<Boolean> calculate(){
        //Delete Transaction rests
        em.createQuery("delete from TransactionRest").executeUpdate();

        List<Transaction> transactions = em.createQuery(
                "select e from Transaction e where e.isActive = :isActive" +
                " order by e.transactionDate asc, e.transactionOrder asc ", Transaction.class)
                .setParameter("isActive", 1)
                .getResultList();
        Transaction prev = null;
        for (Transaction transaction : transactions) {
            List<TransactionRest> transactionRests = new ArrayList<>(3);
            TransactionRest tr;

            tr = new TransactionRest(transaction.transactionDate);
            tr.transactionRestType = TransactionRestType.PROJECT;
            tr.referenceId = transaction.projectId;
            transactionRests.add(tr);

                //Project
            if (prev != null ) {
                TransactionRest projectRest = TransactionRestCalculator.extract(prev.transactionRests, TransactionRestType.PROJECT);

                if (tr.referenceId.equals(projectRest.referenceId)) {
                    tr.transactionRest = transaction.transactionAmount.add(projectRest.transactionRest);
                } else {
                    tr.transactionRest = transaction.transactionAmount;
                }

            } else {
                tr.transactionRest = transaction.transactionAmount;
            }

            //Account
            tr = new TransactionRest(transaction.transactionDate);
            tr.transactionRestType = TransactionRestType.ACCOUNT;
            tr.referenceId = transaction.accountId;
            transactionRests.add(tr);

            if (prev != null) {
                TransactionRest accScope = TransactionRestCalculator.extract(prev.transactionRests, TransactionRestType.ACCOUNT);
                if (tr.referenceId.equals(accScope.referenceId)) {
                    tr.transactionRest = transaction.transactionAmount.add(accScope.transactionRest);
                } else {
                    tr.transactionRest = transaction.transactionAmount;
                }
            } else {
                tr.transactionRest = transaction.transactionAmount;
            }

            //All
            tr = new TransactionRest(transaction.transactionDate);
            tr.transactionRestType = TransactionRestType.ALL;
            transactionRests.add(tr);


            if (prev != null) {
                TransactionRest all = TransactionRestCalculator.extract(prev.transactionRests, TransactionRestType.ALL);
                tr.transactionRest = transaction.transactionAmount.add(all.transactionRest);
            } else {
                tr.transactionRest = transaction.transactionAmount;
            }

            //Currency
            tr = new TransactionRest(transaction.transactionDate);
            tr.transactionRestType = TransactionRestType.CURRENCY;
            tr.referenceId = transaction.currencyId;
            transactionRests.add(tr);

            if (prev != null) {
                TransactionRest currencyScoped = TransactionRestCalculator.extract(prev.transactionRests, TransactionRestType.CURRENCY);
                if (tr.referenceId.equals(currencyScoped.referenceId)) {
                    tr.transactionRest = transaction.transactionAmount.add(currencyScoped.transactionRest);
                } else {
                    tr.transactionRest = transaction.transactionAmount;
                }
            } else {
                tr.transactionRest = transaction.transactionAmount;
            }

            transaction.transactionRests = transactionRests;

            prev = transaction;

            em.merge(transaction);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
