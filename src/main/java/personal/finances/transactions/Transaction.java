package personal.finances.transactions;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import personal.finances.transactions.rest.TransactionRest;

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

    public Integer transactionOrder;

    public Integer employeeId;

    public String employeeFullName;

    @Temporal(TemporalType.TIMESTAMP)
    public Date userDate;

    @Lob
    public String transactionNote;

    public Integer projectId;

    public String projectName;

    public Integer accountId;

    public String accountName;

    public Integer currencyId;

    public String currencyCode;

    public Integer direction;

    public Integer operationTypeId;

    public String operationType;

    public Integer transactionType;

    @OneToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "transactionId")
    public List<TransactionRest> transactionRests;

    @Version
    public Long version;

    public Integer isActive;
}
