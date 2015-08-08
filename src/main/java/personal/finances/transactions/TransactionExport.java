package personal.finances.transactions;

import jxl.DateCell;
import jxl.NumberCell;
import jxl.Workbook;
import jxl.write.*;
import jxl.write.Number;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import personal.ListPage;
import personal.finances.transactions.rest.TransactionRest;
import personal.finances.transactions.rest.TransactionRestType;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.net.URLEncoder;
import java.util.List;

/**
 * Created by niko on 7/30/15.
 */
@RestController
@RequestMapping("/transaction/export")
public class TransactionExport {

    @Autowired
    TransactionService transactionService;

    @RequestMapping(value = "/excel", method = {RequestMethod.GET, RequestMethod.POST})
    ResponseEntity<byte[]> excel(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) List<Integer> accountId,
            @RequestParam(required = false) List<Integer> projectId,
            @RequestParam(required = false) List<Integer> currencyId,
            @RequestParam(required = false) Integer direction,
            @RequestParam(required = false) String note,
            HttpServletResponse response) throws Exception {

        final HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/vnd.ms-excell");

        headers.set("Cache-Control", "private");
        headers.set(
                "Content-Disposition",
                "attachment; filename="
                        + URLEncoder
                        .encode("ტრანზაქციის-ექსპორტი.xls", "UTF-8"));

        ResponseEntity<ListPage<Transaction>> resp = transactionService.search(startDate, endDate, accountId, projectId, currencyId, direction, note, null, null);
        List<Transaction> transactions =(List)resp.getBody().getList();

        WritableWorkbook wworkbook;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        wworkbook = Workbook.createWorkbook(outputStream);
        WritableSheet wsheet = wworkbook.createSheet("Transactions", 0);

        String[] sheetHeaders = {"Date", "Project", "Destination", "Sum", "Currency", "Rest", "Currency", "Account"};
        int[] sheetHeaderWidth = {10, 40, 50, 10, 10, 10, 10, 40};

        for (int i = 0; i < sheetHeaders.length; i++) {
            Label label = new Label(i, 0, sheetHeaders[i]);
            wsheet.setColumnView(i,sheetHeaderWidth[i]);
            wsheet.addCell(label);
        }

        for (int i= 0; i < transactions.size(); i++) {
            Transaction transaction = transactions.get(i);
            DateTime date = new DateTime(0, i + 1, transaction.transactionDate);
            wsheet.addCell(date);

            Label project = new Label(1, i + 1, transaction.projectName);
            wsheet.addCell(project);

            Label destination = new Label(2, i + 1, transaction.transactionNote);
            wsheet.addCell(destination);

            jxl.write.Number sum = new Number(3, i + 1, transaction.transactionAmount.doubleValue());
            wsheet.addCell(sum);

            Label currency = new Label(4, i + 1, transaction.currencyCode);
            wsheet.addCell(currency);

            List<TransactionRest> transactionRests = transaction.transactionRests;
            if (transactionRests != null && !transactionRests.isEmpty()) {
                TransactionRest currencyRest = getRest(transactionRests, TransactionRestType.CURRENCY);

                jxl.write.Number rest = new Number(5, i + 1, currencyRest.transactionRest.doubleValue());
                wsheet.addCell(rest);

                Label restCurrency = new Label(6, i + 1, transaction.currencyCode);
                wsheet.addCell(restCurrency);
            }

            Label account = new Label(7, i + 1, transaction.accountName);
            wsheet.addCell(account);
        }

        wworkbook.write();
        wworkbook.close();

        return new ResponseEntity<>(outputStream.toByteArray(), headers, HttpStatus.OK);
    }

    private TransactionRest getRest(List<TransactionRest> transactionRests, TransactionRestType transactionRestType) {
        for (TransactionRest transactionRest : transactionRests) {
            if (transactionRest.transactionRestType.equals(transactionRestType)) {
                return transactionRest;
            }
        }
        return null;
    }
}
