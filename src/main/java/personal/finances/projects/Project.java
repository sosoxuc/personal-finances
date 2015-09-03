package personal.finances.projects;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Version;

@Entity
@Table(name = "PROJECTS")
public class Project implements Serializable{
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "Project")
    @TableGenerator(name = "Project")
    public Integer id;

    public String projectName;

    public Integer isActive;

    @Version
    public Long version;
}
