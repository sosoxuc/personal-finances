package personal.hr.positions;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import personal.States;

@RestController
@RequestMapping("/hr/position")
public class PositionsService {

    @PersistenceContext
    private EntityManager em;

    @RequestMapping("/position/list")
    public List<Position> list() {
        return em.createQuery("from Position", Position.class).getResultList();
    }

    @RequestMapping("/position/add")
    @Transactional(rollbackFor = Throwable.class)
    public Integer add(@RequestParam String name) {
        Position position = new Position();
        position.positionName = name;
        position.stateId=States.ACTIVE;
        em.persist(position);

        return position.id;
    }
    
    @RequestMapping("/position/remove")
    @Transactional(rollbackFor = Throwable.class)
    public void remove(@RequestParam Integer id) {
        Position position = em.find(Position.class, id);
        position.stateId=States.INACTIVE;
    }
}
