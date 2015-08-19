package personal.finances.projects;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import personal.security.Secured;

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
    
    @CacheEvict(value="project", allEntries=true)
    public static void init(EntityManager em){
        ProjectService service =new ProjectService();
        service.em=em;
        service.create("სამსახური");
        service.create("სხვა პროექტი");
    }
    
    @Secured
    @CacheEvict(value="project", allEntries=true)
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

    @Secured
    @Cacheable(value="project")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ResponseEntity<List<Project>> list() {
        List<Project> projects = em.createQuery("from Project where isActive = :isActive", Project.class)
                .setParameter("isActive", ACTIVE)
                .getResultList();
        return new ResponseEntity<>(projects, HttpStatus.OK);
    }

    @Secured
    @CacheEvict(value="project", allEntries=true)
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @Transactional(rollbackFor = Throwable.class)
    public ResponseEntity<Project> update(
            @RequestParam("id") Integer id,
            @RequestParam("projectName") String projectName,
            @RequestParam("version") Long version) {

        ResponseEntity<Project> resp = getByName(projectName);
        if (resp.getStatusCode().equals(HttpStatus.OK) && resp.getBody() != null) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        Project project = em.find(Project.class, id);
        if (project != null && project.isActive.equals(ACTIVE)) {

            if ( ! version.equals(project.version)) {
                return new ResponseEntity<>(HttpStatus.CONFLICT);
            }
            project.projectName = projectName;

            new UpdatePostProcessor(em, project).process();

            return new ResponseEntity<>(project, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @Secured
    @CacheEvict(value="project", allEntries=true)
    @RequestMapping(value = "/remove", method = RequestMethod.POST)
    @Transactional(rollbackFor = Throwable.class)
    public ResponseEntity<Project> remove(
            @RequestParam("id") Integer id,
            @RequestParam("version") Long version) {

        Project project = em.find(Project.class, id);

        if (project != null && project.isActive.equals(ACTIVE)) {
            if ( ! version.equals(project.version)) {
                return new ResponseEntity<>(HttpStatus.CONFLICT);
            }
            project.isActive = INACTIVE;

            return new ResponseEntity<>(project, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    private ResponseEntity<Project> getByName(String name) {

        List<Project> projects = em.createQuery("from Project where projectName = :projectName and isActive = :isActive", Project.class)
                .setParameter("projectName", name)
                .setParameter("isActive", ACTIVE)
                .getResultList();

        if (projects.size() == 0) {
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(projects.get(0), HttpStatus.OK);
    }
}
