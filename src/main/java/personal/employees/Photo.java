package personal.employees;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;


// FIXME entity ანოტაცია არ ეწერა
@Entity
public class Photo  implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    public Integer id;
    
    @Lob
    public byte[] photo;
}
