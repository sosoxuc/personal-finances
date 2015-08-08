package personal.finances.transactions;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import personal.ListPage;
import personal.States;
import personal.UploadResponse;
import personal.finances.accounts.Account;
import personal.finances.currency.Currency;
import personal.finances.projects.Project;
import personal.finances.transactions.rest.TransactionRest;
import personal.finances.transactions.rest.TransactionRestCalculator;
import personal.security.AdminRole;
import personal.security.UserRole;

@RestController
@RequestMapping("/transaction")
public class TransactionService {

    public static SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");

    @PersistenceContext
    private EntityManager em;

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @Transactional(rollbackFor = Throwable.class)
    public Transaction create(
            @RequestParam BigDecimal amount,
            @RequestParam Integer projectId,
            @RequestParam String date,
            @RequestParam String note,
            @RequestParam Integer direction,
            @RequestParam Integer accountId,
            @RequestParam Integer currencyId,
            @RequestParam(required = false) Integer operationTypeId

            ) throws ParseException {
        Transaction transaction = new Transaction();
        Date transactionDate = df.parse(date);
        transaction.userDate = new Date();

        // set direction to amount
        transaction.direction = direction;
        transaction.transactionAmount = amount.multiply(new BigDecimal(direction));
        transaction.transactionDate = transactionDate;
        transaction.transactionNote = note;

        //set account
        Account account = em.find(Account.class, accountId);
        transaction.accountId = account.id;
        transaction.accountName = account.accountName;

        //set currency
        Currency currency = em.find(Currency.class, currencyId);
        transaction.currencyId = currency.id;
        transaction.currencyCode = currency.currencyCode;

        //set project
        Project project = em.find(Project.class, projectId);
        transaction.projectId = projectId;
        transaction.projectName = project.projectName;

        //set order
        List<Transaction> dayTransactions = getLastTransactions(transactionDate);

        if ( ! dayTransactions.isEmpty()) {
            Integer transactionOrder = dayTransactions.get(0).transactionOrder;
            transaction.transactionOrder = transactionOrder + 1;
        } else {
            transaction.transactionOrder = 0;
        }

//        if (operationTypeId != null) {
//            OperationType operationType = em.find(OperationType.class, operationTypeId);
//            transaction.operationTypeId = operationType.operationTypeId;
//            transaction.operationType = operationType.operationType;
//        }
        //set rest
        List<TransactionRest> transactionRests = new TransactionRestCalculator(em, transaction).calculateRests();

        transaction.transactionRests = transactionRests;

        transaction.isActive = States.ACTIVE;
        em.persist(transaction);

        return transaction;
    }

    private List<Transaction> getLastTransactions(Date transactionDate) {
        return em.createQuery(
                    "from Transaction where transactionDate = :transactionDate order by transactionOrder desc", Transaction.class)
                .setParameter("transactionDate", transactionDate)
                .setFirstResult(0)
                .setMaxResults(1)
                .getResultList();
    }

    private List<Transaction> getLastTransactions() {
        return em.createQuery(
                "from Transaction order by transactionDate desc, transactionOrder desc", Transaction.class)
                .setFirstResult(0)
                .setMaxResults(1)
                .getResultList();
    }

    @RequestMapping(value = "/search", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity<ListPage<Transaction>> search(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) List<Integer> accountId,
            @RequestParam(required = false) List<Integer> projectId,
            @RequestParam(required = false) List<Integer> currencyId,
            @RequestParam(required = false) Integer direction,
            @RequestParam(required = false) String note,
            @RequestParam(required = false) Integer start,
            @RequestParam(required = false) Integer limit) throws ParseException {

        Map<String, Object> queryParams = new HashMap<>();
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append(" %s  where t.isActive = :isActive");
        queryParams.put("isActive", States.ACTIVE);

        if(startDate != null) {
            Date sDate = df.parse(startDate);
            queryBuilder.append(" and t.transactionDate >= :startDate");
            queryParams.put("startDate", sDate);
        }

        if(endDate != null) {
            Date eDate = df.parse(endDate);
            queryBuilder.append(" and t.transactionDate <= :eDate");
            queryParams.put("eDate", eDate);
        }

        if (accountId != null && !accountId.isEmpty()) {
            queryBuilder.append(" and t.accountId in (:accountId)");
            queryParams.put("accountId", accountId);
        }

        if (projectId != null && !projectId.isEmpty()) {
            queryBuilder.append(" and t.projectId in (:projectId)");
            queryParams.put("projectId", projectId);
        }

        if (currencyId != null && !currencyId.isEmpty()) {
            queryBuilder.append(" and t.currencyId in (:currencyId)");
            queryParams.put("currencyId", currencyId);
        }

        if (direction != null) {
            queryBuilder.append(" and t.direction = :direction");
            queryParams.put("direction", direction);
        }

        if (note != null) {
            queryBuilder.append(" and t.transactionNote like :transactionNote");
            queryParams.put("transactionNote", note + "%");
        }

        javax.persistence.Query countQuery = em.createQuery(String.format(queryBuilder.toString(), "select count(t) from Transaction t" ));

        queryBuilder.append(" order by t.transactionDate desc, t.transactionOrder desc, t.id desc");
        TypedQuery<Transaction> query = em.createQuery(String.format(queryBuilder.toString(), "select distinct t from Transaction t left join fetch t.transactionRests" ), Transaction.class);

        for (String key : queryParams.keySet()) {
            query.setParameter(key, queryParams.get(key));
            countQuery.setParameter(key, queryParams.get(key));
        }

        if (start != null) {
            query.setFirstResult(start);
        }
        if (limit != null) {
            query.setMaxResults(limit);
        }

        List<Transaction> transactions = query.getResultList();
        Long total = (Long)countQuery.getSingleResult();
        ListPage<Transaction> listPage = new ListPage<>(transactions, total.intValue());
        return new ResponseEntity<>(listPage, HttpStatus.OK);

    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @Transactional(rollbackFor = Throwable.class)
    public ResponseEntity<Transaction> update(
          @RequestParam                   Integer transactionId,
          @RequestParam                   Long version,
          @RequestParam(required = false) BigDecimal amount,
          @RequestParam(required = false) Integer projectId,
          @RequestParam(required = false) String date,
          @RequestParam(required = false) String note,
          @RequestParam(required = false) Integer direction,
          @RequestParam(required = false) Integer accountId,
          @RequestParam(required = false) Integer currencyId) throws ParseException {

        Transaction transaction = em.find(Transaction.class, transactionId);
        if ( ! version.equals(transaction.version)) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        if (amount == null) {
            amount = transaction.transactionAmount;
        }
        if (projectId == null) {
            projectId = transaction.projectId;
        }

        if (date == null) {
            date = df.format(transaction.transactionDate);
        }

        if (note == null) {
            note = transaction.transactionNote;
        }

        if (direction == null) {
            direction = transaction.direction;
        }
        if (accountId == null) {
            accountId = transaction.accountId;
        }

        if (currencyId == null) {
            currencyId = transaction.currencyId;
        }

        ResponseEntity<Transaction> remove = remove(transactionId, transaction.version);
        if (remove.getStatusCode().equals(HttpStatus.OK)) {
            Transaction created = create(amount, projectId, date, note, direction, accountId, currencyId, null);
            return new ResponseEntity<>(created, HttpStatus.OK);
        } else {
            return remove;
     }

    }

    @UserRole
    @AdminRole
    @RequestMapping(value = "/remove", method = RequestMethod.POST)
    @Transactional(rollbackFor = Throwable.class)
    public ResponseEntity<Transaction> remove(@RequestParam Integer id, @RequestParam Long version) {
        Transaction transaction = em.find(Transaction.class, id);
        if ( ! version.equals(transaction.version)) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        for (TransactionRest transactionRest : transaction.transactionRests) {
            transactionRest.isActive = States.INACTIVE;
        }
        transaction.isActive = States.INACTIVE;
        new TransactionPostDelete(em, transaction).updateRests();

        return new ResponseEntity<>(transaction, HttpStatus.OK);
    }

    @RequestMapping(value = "/shift", method = RequestMethod.POST)
    @Transactional(rollbackFor = Throwable.class)
    public ResponseEntity<Transaction> shift(@RequestParam Integer transactionId, @RequestParam Integer direction) {

        Transaction shiftFrom = em.find(Transaction.class, transactionId);

        if (shiftFrom.isActive.equals(1)) {
            StringBuilder queryBuilder = new StringBuilder();
            queryBuilder.append("select e from Transaction e where e.isActive = :isActive");

            queryBuilder.append(" ");

            if (direction * -1 > 0) {
                queryBuilder.append(" and ((e.transactionDate = :transactionDate and e.transactionOrder < :transactionOrder) or (e.transactionDate < :transactionDate))   order by e.transactionDate desc, e.transactionOrder desc");
            } else {
                queryBuilder.append(" and ((e.transactionDate = :transactionDate and e.transactionOrder > :transactionOrder) or (e.transactionDate > :transactionDate))   order by e.transactionDate asc, e.transactionOrder asc");
            }

            List<Transaction> transactions = em.createQuery(queryBuilder.toString(), Transaction.class)
                    .setParameter("isActive", 1)
                    .setParameter("transactionOrder", shiftFrom.transactionOrder)
                    .setParameter("transactionDate", shiftFrom.transactionDate)
                    .setFirstResult(0)
                    .setMaxResults(1)
                    .getResultList();

            if (transactions.size() == 1) {
                Transaction shiftTo = transactions.get(0);
                for (TransactionRest from : shiftFrom.transactionRests) {
                     for (TransactionRest to : shiftTo.transactionRests) {
                        if (from.transactionRestType.equals(to.transactionRestType)) {

                            if ((from.referenceId == null && to.referenceId == null)
                                    || from.referenceId.equals(to.referenceId)) {
                                if (direction * -1 > 0) {
                                    from.transactionRest = to.transactionRest.subtract(shiftTo.transactionAmount).add(shiftFrom.transactionAmount);
                                    to.transactionRest = from.transactionRest.add(shiftTo.transactionAmount);

                                } else {
                                    to.transactionRest = from.transactionRest.subtract(shiftFrom.transactionAmount).add(shiftTo.transactionAmount);
                                    from.transactionRest = to.transactionRest.add(shiftFrom.transactionAmount);

                                }
                            }
                        }
                     }
                }
                if ( ! shiftFrom.transactionDate.equals(shiftTo.transactionDate)) {
                    if (direction * -1 > 0) {
                        Integer transactionOrder = shiftTo.transactionOrder;
                        shiftFrom.transactionOrder = transactionOrder;
                        shiftTo.transactionOrder  = transactionOrder + 1;
                    } else {
                        Integer transactionOrder = shiftTo.transactionOrder;
                        shiftFrom.transactionOrder = transactionOrder;
                        shiftTo.transactionOrder = transactionOrder - 1;
                    }
                    shiftFrom.transactionDate = shiftTo.transactionDate;

                    for (TransactionRest transactionRest : shiftFrom.transactionRests) {
                        transactionRest.transactionDate = shiftTo.transactionDate;
                    }

                } else {
                    Integer transactionOrder = shiftFrom.transactionOrder;
                    shiftFrom.transactionOrder = shiftTo.transactionOrder;
                    shiftTo.transactionOrder = transactionOrder;
                }
                return new ResponseEntity<>(shiftFrom, HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @RequestMapping("/upload")
    @Transactional(rollbackFor = Throwable.class)
    public UploadResponse uploadTransactions(
            @RequestParam Integer accountId,
            @RequestParam Integer projectId,
            @RequestParam MultipartFile file) {
        UploadResponse response;
        try {
            File tempFile = File.createTempFile("upload", ".tmp");
            file.transferTo(tempFile);

            processTransactionsFile(tempFile, accountId, projectId);

            response = new UploadResponse(true);
            response.fileName = tempFile.getName();
        } catch (Exception ex) {
            ex.printStackTrace();
            response = new UploadResponse(false);
            response.error = ex.getMessage();
        }
        return response;
    }

    private void processTransactionsFile(File file, Integer accountId, Integer projectId) throws IOException, BiffException, ParseException {

        Workbook workbook = Workbook.getWorkbook(file);
        Sheet sheet = workbook.getSheet(0);
        for (int i = 1; i < sheet.getRows(); i++) {

            Transaction transaction = new Transaction();
            transaction.accountId = accountId;
            transaction.projectId = projectId;

            Cell dateCell = sheet.getCell(0, i);
            String dateText = dateCell.getContents();
            dateText = dateText.replaceAll("/", "-");

            Cell amountCell = sheet.getCell(1, i);
            String amountText = amountCell.getContents();
            if (StringUtils.isNotBlank(amountText)) {
                DecimalFormatSymbols symbols = new DecimalFormatSymbols();
                symbols.setGroupingSeparator(',');
                symbols.setDecimalSeparator('.');
                String pattern = "#,##0.0#";
                DecimalFormat decimalFormat = new DecimalFormat(pattern, symbols);
                decimalFormat.setParseBigDecimal(true);

                BigDecimal amount = (BigDecimal) decimalFormat.parse(amountText);
                transaction.transactionAmount = amount;

                if (amount.signum() < 0) {
                    transaction.direction = Direction.OUT;
                } else {
                    transaction.direction = Direction.IN;
                }
            }

            Cell currencyCell = sheet.getCell(2, i);
            String currencyCellText = currencyCell.getContents();
            if (StringUtils.isNotBlank(currencyCellText)) {
                transaction.currencyId = Integer.parseInt(currencyCellText);
            }

            Cell noteCell = sheet.getCell(3, i);
            String noteText = noteCell.getContents();
            if (StringUtils.isNotBlank(noteText)) {
                transaction.transactionNote = noteText;
            }

            create(transaction.transactionAmount, transaction.projectId, dateText, noteText,
                   transaction.direction, transaction.accountId, transaction.currencyId, null);
        }
    }
}
