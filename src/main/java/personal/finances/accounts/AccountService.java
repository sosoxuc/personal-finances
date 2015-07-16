package personal.finances.accounts;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

import static personal.States.ACTIVE;
import static personal.States.INACTIVE;

/**
 * Created by niko on 7/11/15.
 */
@RestController
@RequestMapping("/account")
public class AccountService {

    @PersistenceContext
    private EntityManager em;

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @Transactional(rollbackFor = Throwable.class)
    public ResponseEntity<Account> create(@RequestParam String accountName,
            @RequestParam(required = false) String accountNumber) {
        Account account = new Account();
        account.accountName = accountName;
        account.accountNumber = accountNumber;
        account.isActive = ACTIVE;

        em.persist(account);

        return new ResponseEntity<>(account, HttpStatus.OK);
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ResponseEntity<List<Account>> list() {
        List<Account> accounts = em
                .createQuery("from Account where isActive = :isActive",
                        Account.class)
                .setParameter("isActive", ACTIVE).getResultList();
        return new ResponseEntity<>(accounts, HttpStatus.OK);
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @Transactional(rollbackFor = Throwable.class)
    public ResponseEntity<Account> update(@RequestParam Integer id,
            @RequestParam String accountName,
            @RequestParam(required = false) String accountNumber) {

        Account account = em.find(Account.class, id);
        if (account != null && account.isActive.equals(ACTIVE)) {
            account.accountName = accountName;
            account.accountNumber = accountNumber;

            // update relation transactions and so on
            new UpdatePostProcessor(em, account).process();
            return new ResponseEntity<>(account, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @RequestMapping(value = "/remove", method = RequestMethod.POST)
    @Transactional(rollbackFor = Throwable.class)
    public ResponseEntity<Account> remove(@RequestParam("id") Integer id) {
        Account account = em.find(Account.class, id);

        if (account != null && account.isActive.equals(ACTIVE)) {
            account.isActive = INACTIVE;
            return new ResponseEntity<>(account, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
