package com.mindex.challenge.controller;

import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.service.EmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class EmployeeController {
    private static final Logger LOG = LoggerFactory.getLogger(EmployeeController.class);

    @Autowired
    private EmployeeService employeeService;

    /**
     * Create Employee request
     * @param employee
     */
    @PostMapping("/employee")
    public Employee createEmployee(@RequestBody Employee employee) {
        LOG.debug("Received employee create request for [{}]", employee);
        try {
            return employeeService.createEmployee(employee);
        }catch (Exception e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error creating Employee", e);
        }
    }

    /**
     * Get Employee by id request
     * @param id
     */
    @GetMapping("/employee/{id}")
    public Employee getEmployee(@PathVariable String id) {
        LOG.debug("Received get employee request for id [{}]", id);
        try {
            return employeeService.getEmployee(id);
        }catch(RuntimeException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cannot find employee: " + id, e);
        }
    }

    /**
     * Get the number of reports by id request
     * @param id
     */
    @GetMapping("/employee/numberOfReports/{id}")
    public ReportingStructure getEmployeeNumberOfReports(@PathVariable String id) {
        LOG.debug("Received get employee number of reports request for id [{}]", id);
        try {
            return employeeService.getEmployeeReports(id);
        }catch(RuntimeException e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error occurred while processing the employee reports for: " + id, e);
        }
    }

    /**
     * Update Employee request
     * @param id
     * @param employee
     */
    @PutMapping("/employee/{id}")
    public Employee updateEmployee(@PathVariable String id, @RequestBody Employee employee) {
        LOG.debug("Received update employee request for id [{}] and employee [{}]", id, employee);

        employee.setEmployeeId(id);
        try {
            return employeeService.updateEmployee(employee);
        }catch (Exception e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error Updating Employee", e);
        }
    }

    /**
     * Create Compensation for Employee
     * @param id
     * @param compensation
     */
    @PostMapping("/employee/compensation/{id}")
    public Compensation createEmployeeCompensation(@PathVariable String id, @RequestBody Compensation compensation) {
        LOG.debug("Received employee create compensation request for [{}]", id);
        try {
            return employeeService.createEmployeeCompensation(id, compensation);
        }catch (Exception e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error creating compensation for: " + id, e);
        }
    }

    /**
     * Get Employee Compensation request
     * @param id
     */
    @GetMapping("/employee/compensation/{id}")
    public Compensation getEmployeeCompensation(@PathVariable String id) {
        LOG.debug("Received get employee compensation request for id [{}]", id);
        try {
            return employeeService.getEmployeeCompensation(id);
        }catch (RuntimeException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cannot find compensation for: " +id, e);
        }
    }
}
