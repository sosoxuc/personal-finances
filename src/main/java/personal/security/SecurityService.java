package personal.security;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import personal.States;
import personal.hr.employees.Employee;
import personal.spring.ConfigUtil;
import personal.utils.SecurityUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.UUID;

import static org.apache.commons.lang.StringUtils.isBlank;
import static personal.security.Passport.invalidPassport;
import static personal.security.SessionUtils.SESSION_DATA_KEY;
import static personal.security.SessionUtils.getSession;
import static personal.utils.SecurityUtils.sha512;

@RestController
@RequestMapping("/security")
public class SecurityService {

    public static final int SALT_LENGHT = 8;

    private static final String USQL = "select e from Employee e where (e.userName=:username) and e.state=1";

    @PersistenceContext
    private EntityManager em;

    @RequestMapping(value = "/signin", method = RequestMethod.POST)
    @Transactional(rollbackFor = Throwable.class)
    public Passport signin(@RequestParam String username,
            @RequestParam String password) {

        if (isBlank(username) || isBlank(password)) {
            return invalidPassport("EMPTY_USER_PASSWORD");
        }

        username = username.trim().toUpperCase();

        if (username.equals("ADMIN")
                && password.equals(ConfigUtil.getConfig("AdminPassword"))) {
            return adminPassport();
        }

        TypedQuery<Employee> usersSql = em.createQuery(USQL, Employee.class);
        usersSql.setParameter("username", username);
        List<Employee> users = usersSql.getResultList();

        if (users.size() != 1) {
            return invalidPassport("BAD_USER");
        }

        Employee user = users.iterator().next();
        String hash = sha512(password.concat(user.passwordSalt));

        if (hash.equals(user.passwordHash)) {
            return buildPassport(user);
        } else {
            return invalidPassport("BAD_PASSWORD");
        }
    }

    private Passport buildPassport(Employee user) {
        Passport passport = new Passport();
        passport.setEmployeeId(user.id);
        passport.setValid(true);
        passport.setAuthResult("SUCCESSFUL");
        passport.setEmployee(user);
        getSession().setAttribute(SESSION_DATA_KEY, passport);
        return passport;
    }

    private Passport adminPassport() {
        Passport passport = new Passport();
        passport.setEmployeeId(0);
        passport.setValid(true);
        passport.setAuthResult("SUCCESSFUL");
        Employee admin = new Employee();
        admin.firstName = "Admin";
        admin.lastName = "Admin";
        admin.stateId = States.ACTIVE;
        passport.setEmployee(admin);
        getSession().setAttribute(SESSION_DATA_KEY, passport);
        return passport;
    }

    @RequestMapping(value = "/passport", method = RequestMethod.GET)
    public Passport passport() {
        return SessionUtils.getPassport();
    }

    @RequestMapping(value = "/signout", method = RequestMethod.POST)
    public void signout() {
        getSession().invalidate();
    }

    @RequestMapping("/password/change")
    public ResponseEntity<Boolean> changePassword(
            @RequestParam String oldPass,
            @RequestParam String newPass,
            HttpSession session){

        Passport passport = (Passport)session.getAttribute(SessionUtils.SESSION_DATA_KEY);
        Employee employee = em.find(Employee.class, passport.getEmployee().id);

        String oldPassHash = SecurityUtils.sha512(oldPass + employee.passwordSalt);
        if (oldPassHash.equals(employee.passwordHash)) {
            String salt = UUID.randomUUID().toString().substring(0, 8);
            String passHash = SecurityUtils.sha512(newPass.concat(salt));

            employee.passwordHash = passHash;
            employee.passwordSalt = salt;

            return new ResponseEntity<>(HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.NOT_MODIFIED);

    }

}
