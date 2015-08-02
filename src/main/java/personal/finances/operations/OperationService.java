package personal.finances.operations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import personal.finances.transactions.Direction;
import personal.finances.transactions.TransactionService;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.List;

/**
 * Created by niko on 7/30/15.
 */
@RestController
@RequestMapping(value = "/operations")
public class OperationService {
    public static final Integer OPERATION_TYPE_PROJECT = 1;
    public static final Integer OPERATION_TYPE_ACCOUNT = 2;
    public static final Integer OPERATION_TYPE_CURRENCY_EXCHANGE = 3;

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private TransactionService transactionService;

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
            @RequestParam BigDecimal amount) throws ParseException {

        OperationType operationType = em.find(OperationType.class, OPERATION_TYPE_PROJECT);

        transactionService.create(amount, from, date,
                operationType.operationType, Direction.OUT,
                accountId, currencyId, OPERATION_TYPE_PROJECT);

        transactionService.create(amount, to, date,
                operationType.operationType, Direction.IN,
              accountId, currencyId, OPERATION_TYPE_PROJECT);
        return new ResponseEntity<>(Boolean.TRUE, HttpStatus.OK);
    }
}
