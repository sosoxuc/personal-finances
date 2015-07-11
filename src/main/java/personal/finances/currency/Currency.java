package personal.finances.currency;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by niko on 7/11/15.
 */
@Entity
@Table(name = "CURRENCY")
public class Currency implements Serializable{
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "Currency")
    @TableGenerator(name = "Currency")
    public Integer id;

    public String currencyName;

    public Integer isActive;
}
