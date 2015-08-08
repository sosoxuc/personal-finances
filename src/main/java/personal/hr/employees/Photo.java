package personal.hr.employees;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import java.io.Serializable;


@Entity
@Table(name = "PHOTOS")
public class Photo  implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    public Integer id;
    
    @Lob
    public byte[] photo;
}
