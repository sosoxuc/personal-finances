package personal.employees;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author soso
 *
 */
@Entity
@Table(name = "EMPLOYEES")
public class Employee implements Serializable {

    public static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "Employee")
    @TableGenerator(name = "Employee")
    public Integer id;

    public String personalNo;

    @Temporal(TemporalType.TIMESTAMP)
    public Date createDate;

    public Integer stateId;

    public String firstName;

    public String lastName;

    public String phone;

    public String email;

    @Temporal(TemporalType.DATE)
    public Date birthDate;

    public String workplaceName;

    public Integer workplaceId;

    public String positionName;

    public Integer positionId;

    public String userName;

    @JsonIgnore
    public String passwordHash;

    @JsonIgnore
    public String passwordSalt;

    public Integer userStateId;

    @Temporal(TemporalType.DATE)
    public Date expireDate;
}
