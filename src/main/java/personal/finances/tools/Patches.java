package personal.finances.tools;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import personal.finances.projects.Project;

@RestController
@RequestMapping("/patch")
public class Patches {

    @PersistenceContext
    private EntityManager em;

    @RequestMapping(value = "/1", method = RequestMethod.POST)
    @Transactional(rollbackFor = Throwable.class)
    public void patch1() {
        List<Project> projects = em
                .createQuery("select p from Project p where p.version is null",
                        Project.class)
                .getResultList();

        for (Project project : projects) {
            project.version = 0L;
        }
    }

}
