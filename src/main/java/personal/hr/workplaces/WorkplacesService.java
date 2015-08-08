package personal.hr.workplaces;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import personal.States;

@RestController
@RequestMapping("/hr/workplace")
public class WorkplacesService {

    @PersistenceContext
    private EntityManager em;

    @RequestMapping("/workplace/list")
    public List<Workplace> getWorkplaces() {
        return em.createQuery("from Workplace", Workplace.class)
                .getResultList();
    }

    @RequestMapping("/workplace/add")
    @Transactional(rollbackFor = Throwable.class)
    public Integer addWorkplace(@RequestParam String name) {
        Workplace workplace = new Workplace();
        workplace.workplaceName = name;
        workplace.stateId=States.ACTIVE;
        em.persist(workplace);

        return workplace.id;
    }
}
