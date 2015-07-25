package personal.security;

import personal.employees.Employee;

import java.io.Serializable;

public class Passport implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer employeeId;

    private Employee employee;

    private Boolean valid;

    private String authResult;

    public static Passport invalidPassport(String authResult) {
        Passport passport = new Passport();
        passport.valid = false;
        passport.authResult = authResult;

        return passport;
    }

    public Integer getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Integer employeeId) {
        this.employeeId = employeeId;
    }

    public Boolean getValid() {
        return valid;
    }

    public void setValid(Boolean valid) {
        this.valid = valid;
    }

    public String getAuthResult() {
        return authResult;
    }

    public void setAuthResult(String authResult) {
        this.authResult = authResult;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }
}
