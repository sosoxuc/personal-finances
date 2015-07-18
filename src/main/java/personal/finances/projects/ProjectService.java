package personal.finances.projects;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

import static personal.States.ACTIVE;
import static personal.States.INACTIVE;
/**
 * Created by Niko on 7/9/15.
 */

@RestController
@RequestMapping("/project")
public class ProjectService {

    @PersistenceContext
    private EntityManager em;

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @Transactional(rollbackFor = Throwable.class)
    public ResponseEntity<Project> create(@RequestParam("projectName") String projectName) {

        ResponseEntity<Project> resp = getByName(projectName);
        if (resp.getStatusCode().equals(HttpStatus.OK) && resp.getBody() != null) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        Project project = new Project();
        project.projectName = projectName;
        project.isActive = ACTIVE;

        em.persist(project);

        return new ResponseEntity<>(project, HttpStatus.OK);
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ResponseEntity<List<Project>> list() {
        List<Project> projects = em.createQuery("from Project where isActive = :isActive", Project.class)
                .setParameter("isActive", ACTIVE)
                .getResultList();
        return new ResponseEntity<>(projects, HttpStatus.OK);
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @Transactional(rollbackFor = Throwable.class)
    public ResponseEntity<Project> update(
            @RequestParam("id") Integer id,
            @RequestParam("projectName") String projectName) {

        ResponseEntity<Project> resp = getByName(projectName);
        if (resp.getStatusCode().equals(HttpStatus.OK) && resp.getBody() != null) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        Project project = em.find(Project.class, id);
        if (project != null && project.isActive.equals(ACTIVE)) {
            project.projectName = projectName;

            new UpdatePostProcessor(em, project).process();

            return new ResponseEntity<>(project, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }


    @RequestMapping(value = "/remove", method = RequestMethod.POST)
    @Transactional(rollbackFor = Throwable.class)
    public ResponseEntity<Project> remove(@RequestParam("id") Integer id) {
        Project project = em.find(Project.class, id);

        if (project != null && project.isActive.equals(ACTIVE)) {
            project.isActive = INACTIVE;
            return new ResponseEntity<>(project, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @RequestMapping(value = "/get/{name}", method = RequestMethod.GET)
    public ResponseEntity<Project> getByName(@PathVariable("name") String name) {

        List<Project> projects = em.createQuery("from Project where projectName = :projectName and isActive = :isActive", Project.class)
                .setParameter("projectName", name)
                .setParameter("isActive", ACTIVE)
                .getResultList();

        if (projects.size() > 1) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        } else if (projects.size() == 0) {
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(projects.get(0), HttpStatus.OK);
    }
}
