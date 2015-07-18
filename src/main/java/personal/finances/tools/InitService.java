package personal.finances.tools;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import personal.finances.accounts.AccountService;
import personal.finances.currency.CurrencyService;
import personal.finances.projects.ProjectService;

@RestController
@RequestMapping("/init")
public class InitService {
    
    @PersistenceContext
    private EntityManager em;
    
    @RequestMapping(value = "/do", method = RequestMethod.POST)
    @Transactional(rollbackFor = Throwable.class)
    public void doItint(){
        
        CurrencyService.init(em);
        ProjectService.init(em);
        AccountService.init(em);
    }

}
