package com.mindex.challenge.service;

import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;

public interface EmployeeService {
    Employee createEmployee(Employee employee);
    Employee getEmployee(String id);
    ReportingStructure getEmployeeReports(String id);
    Employee updateEmployee(Employee employee);
    Compensation createEmployeeCompensation(String id, Compensation compensation);
    Compensation getEmployeeCompensation(String id);
}
