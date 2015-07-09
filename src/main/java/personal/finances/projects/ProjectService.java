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

        em.persist(project);
        return new ResponseEntity<>(project.id, HttpStatus.OK);
    }

    @RequestMapping("/list")
    public ResponseEntity<List<Project>> create() {
        List<Project> projects = em.createQuery("from Project", Project.class)
                .getResultList();
        return new ResponseEntity<>(projects, HttpStatus.OK);
    }
}
