package personal.finances.operations;

import static personal.finances.operations.OperationType.PROJECT;
import static personal.finances.transactions.Direction.IN;
import static personal.finances.transactions.Direction.OUT;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import personal.finances.transactions.TransactionService;

/**
 * Created by niko on 7/30/15.
 */
@RestController
@RequestMapping(value = "/operations")
public class OperationService {
    
    @PersistenceContext
    private EntityManager em;

    @Autowired
    private TransactionService transactions;

    @RequestMapping(value = "/list", method = {RequestMethod.GET})
    public ResponseEntity<List<OperationType>> operationTypes() {
        List<OperationType> operationTypes = em.createQuery("select t from OperationType t where t.isActive = :isActive order by t.operationType asc", OperationType.class)
                .setParameter("isActive", 1)
                .getResultList();

        return new ResponseEntity<>(operationTypes, HttpStatus.OK);
    }
    
    @RequestMapping(value = "/project", method = {RequestMethod.POST})
    @Transactional(rollbackFor = Throwable.class)
    public ResponseEntity<Boolean> project(
            @RequestParam String date,
            @RequestParam Integer accountId,
            @RequestParam Integer currencyId,
            @RequestParam Integer from,
            @RequestParam Integer to,
            @RequestParam BigDecimal amount,
            @RequestParam String note) throws ParseException {

        //OperationType operationType = em.find(OperationType.class, OPERATION_TYPE_PROJECT);

        transactions.create(amount, from, date, note, OUT, accountId, currencyId,PROJECT);

        transactions.create(amount, to, date, note, IN, accountId, currencyId, PROJECT);

        return new ResponseEntity<>(Boolean.TRUE, HttpStatus.OK);
    }
    
    @RequestMapping(value = "/account", method = {RequestMethod.POST})
    @Transactional(rollbackFor = Throwable.class)
    public ResponseEntity<Boolean> account(
            @RequestParam String date,
            @RequestParam Integer projectId,
            @RequestParam Integer currencyId,
            @RequestParam Integer from,
            @RequestParam Integer to,
            @RequestParam BigDecimal amount,
            @RequestParam String note) throws ParseException {

        //OperationType operationType = em.find(OperationType.class, OPERATION_TYPE_PROJECT);

        transactions.create(amount, projectId, date, note, OUT, from, currencyId,PROJECT);

        transactions.create(amount, projectId, date, note, IN, to, currencyId, PROJECT);

        return new ResponseEntity<>(Boolean.TRUE, HttpStatus.OK);
    }
}
