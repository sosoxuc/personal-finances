package personal.finances.transactions.rest;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import personal.States;

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


    @Temporal(TemporalType.DATE)
    public Date transactionDate;

    public BigDecimal transactionRest;

    @Enumerated(EnumType.STRING)
    public TransactionRestType transactionRestType;

    @Transient
    public String resourceName;

    public Integer referenceId;

    public Integer isActive = States.ACTIVE; //Active default

    public TransactionRest(Date transactionDate) {
        this.transactionDate = transactionDate;
    }
    public TransactionRest(){}
}
