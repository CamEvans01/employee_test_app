package com.mindex.challenge.service.impl;

import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.service.EmployeeService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EmployeeServiceImplTest {

    private String employeeUrl;
    private String employeeIdUrl;
    private String employeeNumberOfReportsUrl;
    private String employeeCompensationUrl;
    private String employeeCompensationIdUrl;

    @Autowired
    private EmployeeService employeeService;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Before
    public void setup() {
        employeeUrl = "http://localhost:" + port + "/employee";
        employeeIdUrl = "http://localhost:" + port + "/employee/{id}";
        employeeNumberOfReportsUrl = "http://localhost:" + port + "/employee/numberOfReports/{id}";
        employeeCompensationUrl = "http://localhost:" + port + "/employee/compensation/{id}";
        employeeCompensationIdUrl = "http://localhost:" + port + "/employee/compensation/{id}";
    }

    @Test
    public void testCreateEmployee() {
        Employee createdEmployee = restTemplate.postForEntity(employeeUrl, createTestEmployee(), Employee.class).getBody();
        assert createdEmployee != null;
        assertNotNull(createdEmployee.getEmployeeId());
        assertEmployeeEquivalence(createdEmployee, createdEmployee);
    }

    @Test
    public void testCreateCompensation() {
        Employee createdEmployee = restTemplate.postForEntity(employeeUrl, createTestEmployee(), Employee.class).getBody();
        Compensation createdCompensation = restTemplate.postForEntity(employeeCompensationUrl, createtestCompensation(), Compensation.class, createdEmployee.getEmployeeId()).getBody();
        assert createdCompensation != null;
        assertNotNull(createdCompensation.getSalary());
        assertEmployeeCompensationEquivalence(createtestCompensation(), createdCompensation);
    }

    @Test
    public void testReadEmployee() {
        Employee testEmployee = createTestEmployee();
        Employee createdEmployee = restTemplate.postForEntity(employeeUrl, testEmployee, Employee.class).getBody();
        Employee readEmployee = restTemplate.getForEntity(employeeIdUrl, Employee.class, createdEmployee.getEmployeeId()).getBody();
        assert readEmployee != null;
        assertEquals(createdEmployee.getEmployeeId(), readEmployee.getEmployeeId());
        assertEmployeeEquivalence(createdEmployee, readEmployee);

    }

    /* This method 2 employees, creates the "reports to" relation in the database,
    then calls employee/numberOfReports/{id} to count the number of reports. A more elegant solution would be to
    create a test database, and/or to use mocking for all of these tests, but this is a proof of concept to test the functionality via JUnit
     */
    @Test
    public void testReadReportingStructure() {
        Employee testEmployee = createTestEmployee();
        Employee createdEmployee = restTemplate.postForEntity(employeeUrl, testEmployee, Employee.class).getBody();
        Employee createdEmployee2 = restTemplate.postForEntity(employeeUrl, testEmployee, Employee.class).getBody();
        List<Employee> e = new ArrayList<>();
        e.add(createdEmployee2);
        createdEmployee.setDirectReports(e);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        restTemplate.exchange(employeeIdUrl,
                HttpMethod.PUT,
                new HttpEntity<Employee>(createdEmployee, headers),
                Employee.class,
                createdEmployee.getEmployeeId()).getBody();

        createdEmployee.setDirectReports(e);
        ReportingStructure readEmployeeStructure = restTemplate.getForEntity(employeeNumberOfReportsUrl, ReportingStructure.class, createdEmployee.getEmployeeId()).getBody();
        assert readEmployeeStructure != null;
        assertEquals(1, readEmployeeStructure.getNumberOfReports());

    }

    @Test
    public void testReadEmployeeCompensation() {
        Employee testEmployee = createTestEmployee();
        Employee createdEmployee = restTemplate.postForEntity(employeeUrl, testEmployee, Employee.class).getBody();
        Compensation createdCompensation = restTemplate.postForEntity(employeeCompensationUrl, createtestCompensation(), Compensation.class, createdEmployee.getEmployeeId()).getBody();
        Compensation readCompensation = restTemplate.getForEntity(employeeCompensationIdUrl, Compensation.class, createdEmployee.getEmployeeId()).getBody();
        assert readCompensation != null;
        assertEquals(createdCompensation.getSalary(), readCompensation.getSalary());
        assertEmployeeCompensationEquivalence(createdCompensation, readCompensation);

    }

    @Test
    public void testUpdateEmployee() {
        Employee testEmployee = createTestEmployee();
        Employee createdEmployee = restTemplate.postForEntity(employeeUrl, testEmployee, Employee.class).getBody();
        Employee readEmployee = restTemplate.getForEntity(employeeIdUrl, Employee.class, createdEmployee.getEmployeeId()).getBody();

        readEmployee.setPosition("Development Manager");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        Employee updatedEmployee =
                restTemplate.exchange(employeeIdUrl,
                        HttpMethod.PUT,
                        new HttpEntity<Employee>(readEmployee, headers),
                        Employee.class,
                        readEmployee.getEmployeeId()).getBody();

        assertEmployeeEquivalence(readEmployee, updatedEmployee);
    }


    private static void assertEmployeeEquivalence(Employee expected, Employee actual) {
        assertEquals(expected.getFirstName(), actual.getFirstName());
        assertEquals(expected.getLastName(), actual.getLastName());
        assertEquals(expected.getDepartment(), actual.getDepartment());
        assertEquals(expected.getPosition(), actual.getPosition());
    }

    private static void assertEmployeeCompensationEquivalence(Compensation expected, Compensation actual) {
        assertEquals(expected.getSalary(), actual.getSalary());
        assertEquals(expected.getEffectiveDate(), actual.getEffectiveDate());
    }

    public static Compensation createtestCompensation() {
        Compensation testCompensation = new Compensation();
        testCompensation.setEffectiveDate("01-01-2024");
        testCompensation.setSalary("50000");
        return testCompensation;
    }

    public static Employee createTestEmployee() {
        Employee employee = new Employee();
        employee.setFirstName("John");
        employee.setLastName("Doe");
        employee.setDepartment("Engineering");
        employee.setPosition("Developer");
        employee.setCompensation(createtestCompensation());
        return employee;
    }
}
