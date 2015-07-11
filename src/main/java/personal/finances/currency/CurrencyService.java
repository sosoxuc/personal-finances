package personal.finances.currency;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

/**
 * Created by Niko on 7/11/15.
 */
@RestController
@RequestMapping("/currency")
public class CurrencyService {

    @PersistenceContext
    private EntityManager em;

    @RequestMapping("/create")
    @Transactional(rollbackFor = Throwable.class)
    public ResponseEntity<Currency> create(@RequestParam String currencyName){

        Currency currency = new Currency();
        currency.currencyName = currencyName;
        currency.isActive = 1;

        em.persist(currency);

        return new ResponseEntity<>(currency, HttpStatus.OK);
    }

    @RequestMapping("/list")
    public ResponseEntity<List<Currency>> list() {
        List<Currency> currencies = em.createQuery("from Currency where isActive = :isActive order by currencyName asc", Currency.class)
                .setParameter("isActive", 1)
                .getResultList();
        return new ResponseEntity<>(currencies, HttpStatus.OK);
    }

    @RequestMapping("/update")
    @Transactional(rollbackFor = Throwable.class)
    public ResponseEntity<Currency> update(
            @RequestParam Integer id,
            @RequestParam String currencyName) {

        Currency currency = em.find(Currency.class, id);
        if (currency != null && currency.isActive.equals(1)) {
            currency.currencyName = currencyName;

            //TODO update post process

            return new ResponseEntity<>(currency, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }


    @RequestMapping("/remove")
    @Transactional(rollbackFor = Throwable.class)
    public ResponseEntity<Currency> remove(@RequestParam("id") Integer id) {
        Currency currency = em.find(Currency.class, id);

        if (currency != null && currency.isActive.equals(1)) {
            currency.isActive = 0;
            return new ResponseEntity<>(currency, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
