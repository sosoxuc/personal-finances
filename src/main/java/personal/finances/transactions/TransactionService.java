package personal.finances.transactions;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import personal.security.AdminRole;
import personal.security.UserRole;

@RestController
@RequestMapping("/transaction")
public class TransactionService {

	@PersistenceContext
	private EntityManager em;

	@UserRole
	@AdminRole
	@RequestMapping("/create")
	@Transactional(rollbackFor = Throwable.class)
	public Integer create(@RequestBody Transaction transaction) {
		transaction.setUserDate(new Date());

		em.persist(transaction);
		return transaction.getId();
	}

	@RequestMapping("/search")
	public List<Transaction> search() {
		String qlString = "select t from Transaction t order by t.transactionDate desc,t.id desc";
		return em.createQuery(qlString, Transaction.class).getResultList();
	}

	@UserRole
	@AdminRole
	@RequestMapping("/remove")
	@Transactional(rollbackFor = Throwable.class)
	public void remove(@RequestParam Integer id) {
		Transaction transaction = em.find(Transaction.class, id);
		em.remove(transaction);
	}
}
