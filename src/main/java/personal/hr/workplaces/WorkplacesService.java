package personal.hr.workplaces;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import personal.States;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@RestController
@RequestMapping("/hr/workplace")
public class WorkplacesService {

    @PersistenceContext
    private EntityManager em;

    @RequestMapping("/list")
    public ResponseEntity<List<Workplace>> list() {
        List<Workplace> workplaces = em.createQuery("from Workplace where stateId = :stateId", Workplace.class)
                .setParameter("stateId", States.ACTIVE)
                .getResultList();

        return new ResponseEntity<>(workplaces, HttpStatus.OK);
    }

    @RequestMapping("/add")
    @Transactional(rollbackFor = Throwable.class)
    public ResponseEntity<Workplace> add(@RequestParam String name) {
        Workplace workplace = new Workplace();
        workplace.workplaceName = name;
        workplace.stateId=States.ACTIVE;
        em.persist(workplace);

        return new ResponseEntity<>(workplace, HttpStatus.OK);
    }

    @RequestMapping("/remove")
    @Transactional(rollbackFor = Throwable.class)
    public ResponseEntity<Workplace> remove(@RequestParam Integer id) {
        Workplace workplace = em.find(Workplace.class, id);
        workplace.stateId=States.INACTIVE;

        return new ResponseEntity<>(workplace, HttpStatus.OK);
    }


}
