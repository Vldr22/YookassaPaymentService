package com.education.mypaymentservice.service.forController;

import com.education.mypaymentservice.model.entity.Client;
import com.education.mypaymentservice.model.entity.Employee;
import com.education.mypaymentservice.model.entity.Transaction;
import com.education.mypaymentservice.model.enums.Roles;
import com.education.mypaymentservice.model.request.EmployeeRegistrationRequest;
import com.education.mypaymentservice.model.request.TransactionFilterRequest;
import com.education.mypaymentservice.model.response.ClientResponse;
import com.education.mypaymentservice.model.response.EmployeeResponse;
import com.education.mypaymentservice.model.response.EmployeeTransactionResponse;
import com.education.mypaymentservice.repository.EmployeeRepository;
import com.education.mypaymentservice.service.common.ClientService;
import com.education.mypaymentservice.service.common.TransactionService;
import com.education.mypaymentservice.service.model.TestModelFactory;
import com.education.mypaymentservice.service.security.SmsCodeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private SmsCodeService smsCodeService;

    @Mock
    private ClientService clientService;

    @Mock
    private TransactionService transactionService;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private EmployeeService employeeService;

    private Client testClient;
    private Employee testEmployee;
    private Transaction testTransaction;
    private EmployeeRegistrationRequest registrationRequest;

    @BeforeEach
    void setUp() {
        testClient = TestModelFactory.createTestClient();
        testTransaction = TestModelFactory.createTestTransaction(BigDecimal.valueOf(100));

        testEmployee = new Employee();
        testEmployee.setName("Петр");
        testEmployee.setSurname("Петров");
        testEmployee.setMidname("Петрович");
        testEmployee.setEmail("petr@mail.com");
        testEmployee.setPassword("hashedPassword");
        testEmployee.setRole(Roles.ROLE_EMPLOYEE);

        registrationRequest = new EmployeeRegistrationRequest();
        registrationRequest.setName(testEmployee.getName());
        registrationRequest.setSurName(testEmployee.getSurname());
        registrationRequest.setMidName(testEmployee.getMidname());
        registrationRequest.setEmail(testEmployee.getEmail());
    }

    @Test
    public void getAllClientsResponses_ShouldReturnListOfClientResponses() {
        List<Client> clients = Collections.singletonList(testClient);
        when(clientService.findAllClients()).thenReturn(clients);

        List<ClientResponse> responses = employeeService.getAllClientsResponses();

        assertEquals(1, responses.size());
        ClientResponse response = responses.get(0);
        assertEquals("Иванов Иван Иванович", response.getFullName());
        assertEquals("+79001234567", response.getPhone());
        assertEquals(testClient.getRegistrationDate(), response.getRegistrationDate());
        assertFalse(response.getBlocked());
    }

    @Test
    public void getFilteredTransactionsResponses_ShouldReturnFilteredTransactions() {
        TransactionFilterRequest filterRequest = new TransactionFilterRequest(
                "+79001234567",
                LocalDateTime.now().minusDays(7),
                LocalDateTime.now(),
                BigDecimal.valueOf(100),
                BigDecimal.valueOf(10000)
        );

        List<Transaction> transactions = Collections.singletonList(testTransaction);
        when(transactionService.getFilteredTransactions(
                filterRequest.phone(),
                filterRequest.startDate(),
                filterRequest.endDate(),
                filterRequest.minAmount(),
                filterRequest.maxAmount()
        )).thenReturn(transactions);

        List<EmployeeTransactionResponse> responses = employeeService.getFilteredTransactionsResponses(filterRequest);

        assertEquals(1, responses.size());
        EmployeeTransactionResponse response = responses.get(0);
        assertEquals(UUID.fromString("550e8400-e29b-41d4-a716-446655440000"), response.getId());
        assertEquals(testTransaction.getAmount(), response.getAmount());
        assertEquals(testTransaction.getCurrency(), response.getCurrency());
        assertEquals(testTransaction.getStatus(), response.getStatus());
        assertEquals("Иванов Иван Иванович", response.getClientResponse().getFullName());
        assertEquals("+79001234567", response.getClientResponse().getPhone());
    }

    @Test
    public void removeExpiredAndVerifiedSmsCodes_ShouldCallSmsCodeService() {
        employeeService.removeExpiredAndVerifiedSmsCodes();
        verify(smsCodeService).removeExpiredAndVerifiedSmsCodes();
    }

    @Test
    public void getRegisteredEmployeeResponse_ShouldReturnEmployeeResponse() {
        when(passwordEncoder.encode(registrationRequest.getPassword())).thenReturn("hashedPassword");
        when(employeeRepository.save(any(Employee.class))).thenReturn(testEmployee);

        EmployeeResponse response = employeeService.getRegisteredEmployeeResponse(registrationRequest, Roles.ROLE_EMPLOYEE);

        assertEquals("Петров Петр Петрович", response.fullName());
        assertEquals(registrationRequest.getEmail(), response.email());
    }

    @Test
    public void addEmployee_ShouldSaveAndReturnEmployee() {
        when(passwordEncoder.encode(registrationRequest.getPassword())).thenReturn("hashedPassword");
        when(employeeRepository.save(any(Employee.class))).thenReturn(testEmployee);

        Employee result = employeeService.addEmployee(registrationRequest, Roles.ROLE_EMPLOYEE);

        assertEquals(testEmployee, result);
        assertEquals("hashedPassword", result.getPassword());
        verify(employeeRepository).save(any(Employee.class));
        verify(passwordEncoder).encode(registrationRequest.getPassword());
    }

    @Test
    public void findEmployeeByEmail_ShouldReturnEmployee() {
        String email = "petr@mail.com";
        when(employeeRepository.findByEmail(email)).thenReturn(Optional.of(testEmployee));

        Employee result = employeeService.findEmployeeByEmail(email);

        assertEquals(testEmployee, result);
        verify(employeeRepository).findByEmail(email);
    }
}