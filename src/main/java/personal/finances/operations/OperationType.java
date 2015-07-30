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

    @Id
    public Integer operationTypeId;

    public String operationType;

    public Integer isActive;
}
