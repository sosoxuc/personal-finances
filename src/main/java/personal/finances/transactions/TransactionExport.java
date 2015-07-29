package personal.finances.transactions;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.net.URLEncoder;

/**
 * Created by niko on 7/30/15.
 */
@RestController
@RequestMapping("/transaction")
public class TransactionExport {

    @RequestMapping(value = "/excel", method = {RequestMethod.GET, RequestMethod.POST})
    ResponseEntity<byte[]> excel(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) Integer accountId,
            @RequestParam(required = false) Integer projectId,
            @RequestParam(required = false) Integer currencyId,
            @RequestParam(required = false) Integer direction,
            @RequestParam(required = false) String note,
            @RequestParam(required = false) Integer start,
            @RequestParam(required = false) Integer limit,
            HttpServletRequest request) throws Exception {

        final HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/vnd.ms-excell");

        headers.set("Cache-Control", "private");
        headers.set(
                "Content-Disposition",
                "attachment; filename="
                        + URLEncoder
                        .encode("ტრანზაქციის-ექსპორტი.xls", "UTF-8"));

        byte[] content = new byte[0];

        return new ResponseEntity<>(content, headers, HttpStatus.OK);
    }

}
