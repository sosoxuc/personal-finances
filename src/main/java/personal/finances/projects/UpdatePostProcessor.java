package personal.finances.projects;

import javax.persistence.EntityManager;

/**
 * Created by Niko on 7/10/15.
 */
public class UpdatePostProcessor {

    private EntityManager em;
    private Project project;

    public UpdatePostProcessor(EntityManager em, Project project) {
        this.em = em;
        this.project = project;
    }

    void process(){
        //update project name into transactions
        updateTransactions();
    }

    private int updateTransactions(){
        int affectedRows = em.createQuery("update Transaction set projectName = :projectName where projectId = :projectId")
                .setParameter("projectName", project.projectName)
                .setParameter("projectId", project.id)
                .executeUpdate();
        return affectedRows;
    }
}
