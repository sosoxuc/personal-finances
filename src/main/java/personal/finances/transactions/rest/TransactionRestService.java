package personal.finances.transactions.rest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import personal.finances.transactions.Transaction;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

import static personal.finances.transactions.TransactionService.df;
/**
 * Created by niko on 7/18/15.
 */
@RestController
@RequestMapping("/transaction/rests")
public class TransactionRestService {

    @PersistenceContext
    private EntityManager em;

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
}
