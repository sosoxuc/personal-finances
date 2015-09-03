package personal.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Utility class to check logged user and his rights
 * 
 * @author soso, levan
 * 
 */
public class SessionUtils {

    public static final String SESSION_DATA_KEY = "data";

    public static Passport getPassport() {

        HttpSession session = getSession();
        Passport passport;
        if (session != null) {
            passport = (Passport) session.getAttribute(SESSION_DATA_KEY);
        } else {
            passport = null;
        }

        return passport;
    }

    public static HttpServletRequest getRequest() {

        HttpServletRequest request;
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder
                    .currentRequestAttributes();

            request = attributes.getRequest();
        } catch (Exception ex) {
            ex.printStackTrace();
            request = null;
        }

        return request;
    }

    public static HttpSession getSession() {

        HttpSession session;
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder
                    .currentRequestAttributes();

            session = attributes.getRequest().getSession();
        } catch (Exception ex) {
            ex.printStackTrace();
            session = null;
        }

        return session;
    }

    public static Integer getEmployeeId() {

        Integer employeeId;
        Passport passport = getPassport();
        if (passport == null) {
            employeeId = null;
        } else {
            employeeId = passport.getEmployeeId();
        }

        return employeeId;
    }

}
