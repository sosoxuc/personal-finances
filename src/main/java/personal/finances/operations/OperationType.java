package personal.finances.operations;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by niko on 7/30/15.
 */
@Entity
@Table(name = "OPERATION_TYPE")
public class OperationType {

    public static final Integer PROJECT = 1;
    public static final Integer ACCOUNT = 2;
    public static final Integer CURRENCY= 3;

    
    @Id
    public Integer operationTypeId;

    public String operationType;

    public Integer isActive;
}
