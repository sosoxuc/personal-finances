package personal.hr.workplaces;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

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
