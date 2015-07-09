package personal.finances.transactions;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

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

    @Lob
    public String transactionNote;

    public Integer projectId;

    public String projectName;
}
