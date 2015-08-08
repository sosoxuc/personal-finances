package personal.security;

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
import java.util.List;

import static org.apache.commons.lang.StringUtils.isBlank;
import static personal.security.Passport.invalidPassport;
import static personal.security.SessionUtils.*;
import static personal.utils.SecurityUtils.sha512;
import static personal.utils.StringUtils.generateString;

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

    @AdminRole
    @UserRole
    @RequestMapping(value = "/changePass", method = RequestMethod.POST)
    @Transactional(rollbackFor = Throwable.class)
    public void change(String password) {
        Employee user = em.find(Employee.class, getEmployeeId());

        String salt = generateString("0123456789ABCDEF", SALT_LENGHT);
        String hash = SecurityUtils.sha512(password.concat(salt));

        user.passwordHash = hash;
        user.passwordSalt = salt;
    }

}
