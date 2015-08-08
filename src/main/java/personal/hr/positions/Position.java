package personal.hr.positions;

import javax.persistence.*;
import java.io.Serializable;

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
