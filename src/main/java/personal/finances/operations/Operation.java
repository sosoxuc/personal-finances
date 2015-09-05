package personal.finances.operations;

import personal.States;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by niko on 9/5/15.
 */
@Entity
@Table(name = "OPERATIONS")
public class Operation implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "Operation")
    @TableGenerator(name = "Operation")
    public Integer id;

    public String operationName;

    @Temporal(TemporalType.TIMESTAMP)
    public Date operationDate;

    public Integer stateId;

    public Operation() {
        this.stateId = States.ACTIVE;
    }

    public Operation(String operationName, Date operationDate) {
        this.operationName = operationName;
        this.operationDate = operationDate;
        this.stateId = States.ACTIVE;
    }

    public Operation(Date operationDate) {
        this.operationDate = operationDate;
        this.stateId = States.ACTIVE;
    }
}
