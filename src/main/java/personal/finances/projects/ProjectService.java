package personal.finances.projects;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

/**
 * Created by niko on 7/9/15.
 */

@RestController
@RequestMapping("/project")
public class ProjectService {

    @PersistenceContext
    private EntityManager em;

    @RequestMapping("/create")
    @Transactional(rollbackFor = Throwable.class)
    public ResponseEntity<Integer> create(@RequestParam("projectName") String projectName) {
        Project project = new Project();
        project.projectName = projectName;
        project.isActive = 1;

        em.persist(project);

        return new ResponseEntity<>(project.id, HttpStatus.OK);
    }

    @RequestMapping("/list")
    public ResponseEntity<List<Project>> list() {
        List<Project> projects = em.createQuery("from Project where isActive = :isActive", Project.class)
                .setParameter("isActive", 1)
                .getResultList();
        return new ResponseEntity<>(projects, HttpStatus.OK);
    }

    @RequestMapping("/update")
    @Transactional(rollbackFor = Throwable.class)
    public ResponseEntity<Project> update(
            @RequestParam("id") Integer id,
            @RequestParam("projectName") String projectName) {
        Project project = em.find(Project.class, id);
        //TODO project name validation
        if (project != null && project.isActive.equals(1)) {
            project.projectName = projectName;
            return new ResponseEntity<>(project, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }


    @RequestMapping("/remove")
    @Transactional(rollbackFor = Throwable.class)
    public ResponseEntity<Project> remove(@RequestParam("id") Integer id) {
        Project project = em.find(Project.class, id);

        if (project != null && project.isActive.equals(1)) {
            project.isActive = 0;
            return new ResponseEntity<>(project, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

}
