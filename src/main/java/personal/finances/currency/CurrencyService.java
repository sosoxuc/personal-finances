package personal.finances.currency;

import static personal.States.ACTIVE;
import static personal.States.INACTIVE;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Niko on 7/11/15.
 */
@RestController
@RequestMapping("/currency")
public class CurrencyService {

    @PersistenceContext
    private EntityManager em;
    
    public static void init(EntityManager em){
        CurrencyService service =new CurrencyService();
        service.em=em;
        service.create("ლარი","GEL");
        service.create("დოლარი","USD");
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @Transactional(rollbackFor = Throwable.class)
    public ResponseEntity<Currency> create(
            @RequestParam String currencyName,
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
    public ResponseEntity<Currency> update(
            @RequestParam Integer id,
            @RequestParam String currencyName,
            @RequestParam String currencyCode,
            @RequestParam Long version) {

        Currency currency = em.find(Currency.class, id);
        if (currency != null && currency.isActive.equals(ACTIVE)) {
            if ( ! version.equals(currency.version)) {
                return new ResponseEntity<>(HttpStatus.CONFLICT);
            }

            currency.currencyName = currencyName;
            currency.currencyCode = currencyCode;

            //update related transactions etc
            new UpdatePostProcessor(em, currency).process();

            return new ResponseEntity<>(currency, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @RequestMapping(value = "/remove", method = RequestMethod.POST)
    @Transactional(rollbackFor = Throwable.class)
    public ResponseEntity<Currency> remove(
            @RequestParam("id") Integer id,
            @RequestParam Long version) {
        Currency currency = em.find(Currency.class, id);

        if (currency != null && currency.isActive.equals(ACTIVE)) {
            if ( ! version.equals(currency.version)) {
                return new ResponseEntity<>(HttpStatus.CONFLICT);
            }
            currency.isActive = INACTIVE;
            return new ResponseEntity<>(currency, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
