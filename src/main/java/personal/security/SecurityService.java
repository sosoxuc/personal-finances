package personal.security;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import personal.employees.Employee;
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

    @RequestMapping("/signin")
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
        String hash = sha512(password.concat(user.getPasswordSalt()));

        if (hash.equals(user.getPasswordHash())) {
            return buildPassport(user);
        } else {
            return invalidPassport("BAD_PASSWORD");
        }
    }

    private Passport buildPassport(Employee user) {
        Passport passport = new Passport();
        passport.setEmployeeId(user.getId());
        passport.setUserRole(user.getUserRole());
        passport.setValid(true);
        passport.setAuthResult("SUCCESSFUL");
        passport.setEmployee(user);
        getSession().setAttribute(SESSION_DATA_KEY, passport);
        return passport;
    }

    private Passport adminPassport() {
        Passport passport = new Passport();
        passport.setEmployeeId(0);
        passport.setUserRole(Role.ADMIN);
        passport.setValid(true);
        passport.setAuthResult("SUCCESSFUL");
        Employee admin = new Employee();
        admin.setFirstName("Admin");
        admin.setLastName("Admin");
        admin.setUserRole(Role.ADMIN);
        admin.setState(1);
        passport.setEmployee(admin);
        getSession().setAttribute(SESSION_DATA_KEY, passport);
        return passport;
    }

    @RequestMapping("/passport")
    public Passport passport() {
        return SessionUtils.getPassport();
    }

    @RequestMapping("/signout")
    public void signout() {
        getSession().invalidate();
    }

    @AdminRole
    @UserRole
    @RequestMapping("/changePass")
    @Transactional(rollbackFor = Throwable.class)
    public void change(String password) {
        Employee user = em.find(Employee.class, getEmployeeId());

        String salt = generateString("0123456789ABCDEF", SALT_LENGHT);
        String hash = SecurityUtils.sha512(password.concat(salt));

        user.setPasswordHash(hash);
        user.setPasswordSalt(salt);
    }

}
