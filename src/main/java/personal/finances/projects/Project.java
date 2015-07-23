package personal.finances.projects;

import javax.persistence.*;
import java.io.Serializable;

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
