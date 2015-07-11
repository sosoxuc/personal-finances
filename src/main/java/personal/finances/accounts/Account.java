package personal.finances.accounts;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by niko on 7/11/15.
 */
@Entity
@Table(name = "ACCOUNTS")
public class Account implements Serializable{
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "Account")
    @TableGenerator(name = "Account")
    public Integer id;

    public String accountName;

    public String accountNumber;

    public Integer isActive;
}
