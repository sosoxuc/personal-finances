package personal.finances.transactions;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "TRANSACTIONS")
public class Transaction implements Serializable {

    public static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "Transaction")
    @TableGenerator(name = "Transaction")
    public Integer id;

    @Temporal(TemporalType.DATE)
    public Date transactionDate;

    public BigDecimal transactionAmount;

    public BigDecimal transactionRest;

    public Integer transactionOrder;

    public Integer employeeId;

    public Integer employeeName;

    @Temporal(TemporalType.TIMESTAMP)
    public Date userDate;

    public Integer debitAccountId;

    public String debitAccountName;

    public BigDecimal debitAccountRest;

    public Integer creditAccountId;

    public String creditAccountName;

    public BigDecimal creditAccountRest;

    public Integer projectId;

    public String projectName;

    public Integer reasonId;

    public String reasonName;

    public Integer transactionState;

    @Lob
    public String note;
}
