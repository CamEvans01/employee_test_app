package com.mindex.challenge.service.impl;

import com.mindex.challenge.dao.EmployeeRepository;
import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.service.EmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private static final Logger LOG = LoggerFactory.getLogger(EmployeeServiceImpl.class);

    @Autowired
    private EmployeeRepository employeeRepository;

    // method to create a new employee
    @Override
    public Employee createEmployee(Employee employee) {
        LOG.debug("Creating employee a new employee");

        employee.setEmployeeId(UUID.randomUUID().toString());

        employeeRepository.insert(employee);

        LOG.debug("Successfully created employee with id: [{}]", employee.getEmployeeId());
        return employee;
    }

    // method to get an employee's info from the repository
    @Override
    public Employee getEmployee(String id) {
        LOG.debug("Retrieving employee with id: [{}]", id);

        Employee employee = employeeRepository.findByEmployeeId(id);

        if (employee == null) {
            LOG.error("Cannot find employee with id: [{}] in the database", id);
            throw new RuntimeException();
        }
        LOG.debug("Successful retrieval of employee with id: [{}]", id);
        return employee;
    }

    // method to get employee reports
    @Override
    public ReportingStructure getEmployeeReports(String id) {
        LOG.debug("Retrieving the number of employee reports for employee with id: [{}]", id);

        Employee employee = getEmployee(id);
        ReportingStructure reportingStructure = new ReportingStructure();

        int numberOfReports = reportCount(employee);

        reportingStructure.setNumberOfReports(numberOfReports);
        reportingStructure.setEmployee(id);

        LOG.debug("Successfully retrieved the number of employee reports for employee with id: [{}]", id);
        return reportingStructure;

    }

    // method to recursively count the direct reports
    private int reportCount(Employee employee) {
        int count = 0;
        if (employee.getDirectReports() != null && !employee.getDirectReports().isEmpty()) {
            List<Employee> directReports = employee.getDirectReports();
            count += directReports.size();
            LOG.debug("Found {} reports for employee with id: [{}]. Searching the database to find their reports", directReports.size(), employee.getEmployeeId());
            for (Employee report : directReports) {
                if (report.getEmployeeId() != null && !report.getEmployeeId().isEmpty()) {
                    LOG.debug("Searching the database for reports for employee with id: [{}]", report.getEmployeeId());
                    count += reportCount(getEmployee(report.getEmployeeId()));
                }
            }
        }
        LOG.debug("Successfully found {} reports for employee with id: [{}]", count, employee.getEmployeeId());
        return count;
    }

    // method to update an employee
    @Override
    public Employee updateEmployee(Employee employee) {
        LOG.debug("Updating employee with id: [{}]", employee.getEmployeeId());

        return employeeRepository.save(employee);
    }

    // method to create a new compensation object for an employee
    @Override
    public Compensation createEmployeeCompensation(String id, Compensation compensation) {
        LOG.debug("Creating employee compensation for employee with id: [{}]", id);

        Employee employee = getEmployee(id);
        employee.setCompensation(compensation);
        updateEmployee(employee);

        LOG.debug("Successfully created employee compensation for employee with id: [{}]", id);
        return compensation;
    }

    // method to retrieve the employee compensation from the repository
    @Override
    public Compensation getEmployeeCompensation(String id) {
        LOG.debug("Retrieving employee compensation with id: [{}]", id);

        Employee employee = getEmployee(id);

        if (employee.getCompensation() == null) {
            LOG.error("Cannot find compensation for employee with id: [{}]", id);
            throw new RuntimeException();
        }

        LOG.debug("Successfully retrieved compensation for employee with id: [{}]", id);
        return employee.getCompensation();
    }
}

