package personal.finances.accounts;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Version;

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

    @Version
    public Long version;

    public Integer isActive;
}
