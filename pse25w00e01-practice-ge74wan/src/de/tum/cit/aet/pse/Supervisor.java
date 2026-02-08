package de.tum.cit.aet.pse;

import java.util.ArrayList;
import java.util.List;

public class Supervisor extends Employee {

    private final List<Employee> supervisedEmployees;

    public Supervisor(String name) {
        super(name);

        this.supervisedEmployees = new ArrayList<>();
    }

    public void hireEmployee(Employee employee) {
        supervisedEmployees.add(employee);
    }

    public void fireEmployee(Employee employee) {
        supervisedEmployees.remove(employee);
    }

    @Override
    public void listHierarchy(int level) {
        printName(level);
        for (Employee employee : supervisedEmployees) {
            employee.listHierarchy(level + 1);
        }
    }

    // TODO 2: Implement the Supervisor class
    // TODO 3: Implement listHierarchy() for Supervisor
    public List<Employee> getSupervisedEmployees() {
        return supervisedEmployees;
    }
}
