package personal.finances.transactions.rest;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Created by niko on 7/11/15.
 */
@Entity
@Table(name = "TRANSACTION_RESTS")
public class TransactionRest implements Serializable {

    public static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "transactionRest")
    @TableGenerator(name = "transactionRest")
    public Integer id;

    public Integer transactionId;

    public BigDecimal transactionRest;

    @Enumerated(EnumType.STRING)
    public TransactionRestType transactionRestType;


}
