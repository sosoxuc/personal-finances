package personal.security;

import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

public class SecurityInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request,
            HttpServletResponse response, Object handler) throws Exception {

        // if (handler instanceof HandlerMethod) {
        // HandlerMethod handlerMethod = (HandlerMethod) handler;
        // Method method = handlerMethod.getMethod();
        // boolean admin = method.isAnnotationPresent(AdminRole.class);
        //
        // boolean user = method.isAnnotationPresent(UserRole.class);
        //
        // if (admin || user) {
        //
        // Passport passport = SessionUtils.getPassport();
        // if (passport != null) {
        //
        // if (admin && passport.getUserRole() != null
        // && passport.getUserRole().equals(Role.ADMIN)) {
        // return true;
        // } else if (user
        // && (passport.getUserRole() == null || passport
        // .getUserRole().equals(Role.USER))) {
        // return true;
        // }
        // response.sendError(HttpServletResponse.SC_FORBIDDEN);
        // return false;
        //
        // } else {
        // response.sendError(HttpServletResponse.SC_FORBIDDEN);
        // return false;
        // }
        // }
        //
        // }

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request,
            HttpServletResponse response, Object handler,
            ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest request,
            HttpServletResponse response, Object handler, Exception ex)
                    throws Exception {
    }

}
