package personal.finances.currency;

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
 * Created by Niko on 7/11/15.
 */
@RestController
@RequestMapping("/currency")
public class CurrencyService {

    @PersistenceContext
    private EntityManager em;

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @Transactional(rollbackFor = Throwable.class)
    public ResponseEntity<Currency> create(@RequestParam String currencyName,
            @RequestParam String currencyCode) {

        Currency currency = new Currency();
        currency.currencyName = currencyName;
        currency.currencyCode = currencyCode;
        currency.isActive = ACTIVE;

        em.persist(currency);

        return new ResponseEntity<>(currency, HttpStatus.OK);
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ResponseEntity<List<Currency>> list() {
        List<Currency> currencies = em
                .createQuery(
                        "from Currency where isActive = :isActive order by currencyName asc",
                        Currency.class)
                .setParameter("isActive", ACTIVE).getResultList();
        return new ResponseEntity<>(currencies, HttpStatus.OK);
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @Transactional(rollbackFor = Throwable.class)
    public ResponseEntity<Currency> update(@RequestParam Integer id,
            @RequestParam String currencyName,
            @RequestParam String currencyCode) {

        Currency currency = em.find(Currency.class, id);
        if (currency != null && currency.isActive.equals(ACTIVE)) {
            currency.currencyName = currencyName;
            currency.currencyCode = currencyCode;
            // TODO update post process

            return new ResponseEntity<>(currency, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @RequestMapping(value = "/remove", method = RequestMethod.POST)
    @Transactional(rollbackFor = Throwable.class)
    public ResponseEntity<Currency> remove(@RequestParam("id") Integer id) {
        Currency currency = em.find(Currency.class, id);

        if (currency != null && currency.isActive.equals(ACTIVE)) {
            currency.isActive = INACTIVE;
            return new ResponseEntity<>(currency, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
