package personal.hr.positions;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

@Entity
@Table(name = "POSITIONS")
public class Position  implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "Position")
    @TableGenerator(name = "Position")
    public Integer id;
    
    public String positionName;
    
    public Integer positionOrder;
    
    public Integer stateId;
}
