package personal.hr.positions;

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
@RequestMapping("/hr/position")
public class PositionsService {

    @PersistenceContext
    private EntityManager em;

    @RequestMapping("/list")
    public ResponseEntity<List<Position>> list() {
        List<Position> positions = em.createQuery("from Position where stateId = :stateId", Position.class)
                .setParameter("stateId", States.ACTIVE)
                .getResultList();

        return new ResponseEntity<>(positions, HttpStatus.OK);
    }

    @RequestMapping("/add")
    @Transactional(rollbackFor = Throwable.class)
    public ResponseEntity<Position> add(@RequestParam String name) {
        Position position = new Position();
        position.positionName = name;
        position.stateId=States.ACTIVE;

        em.persist(position);

        return new ResponseEntity<>(position, HttpStatus.OK);
    }
    
    @RequestMapping("/remove")
    @Transactional(rollbackFor = Throwable.class)
    public ResponseEntity<Position> remove(@RequestParam Integer id) {
        Position position = em.find(Position.class, id);
        position.stateId=States.INACTIVE;

        return new ResponseEntity<>(position, HttpStatus.OK);
    }
}
