package personal.hr.workplaces;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "WORKPLACES")
public class Workplace implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "Workplace")
    @TableGenerator(name = "Workplace")
    public Integer id;

    public String workplaceName;
    
    public Integer stateId;
}
