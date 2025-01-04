package com.education.mypaymentservice.service.forController;

import com.education.mypaymentservice.exception.PaymentServiceException;
import com.education.mypaymentservice.model.entity.Employee;
import com.education.mypaymentservice.model.enums.Roles;
import com.education.mypaymentservice.model.request.EmployeeRegistrationRequest;
import com.education.mypaymentservice.model.request.TransactionFilterRequest;
import com.education.mypaymentservice.model.response.EmployeeResponse;
import com.education.mypaymentservice.model.response.TransactionResponse;
import com.education.mypaymentservice.repository.EmployeeRepository;
import com.education.mypaymentservice.service.common.ClientService;
import com.education.mypaymentservice.service.common.TransactionService;
import com.education.mypaymentservice.settings.AppSettingSingleton;
import com.education.mypaymentservice.model.response.ClientResponse;
import com.education.mypaymentservice.model.entity.AppSetting;
import com.education.mypaymentservice.model.entity.Transaction;
import com.education.mypaymentservice.service.security.SmsCodeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.education.mypaymentservice.utils.NormalizeUtils.castToFullName;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmployeeService implements UserDetailsService {

    private final AppSettingSingleton appSettingSingleton;
    private final SmsCodeService smsCodeService;
    private final ClientService clientService;
    private final TransactionService transactionService;
    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;

    public List<ClientResponse> getAllClientsResponses(){
        return clientService.findAllClients().stream()
                .map(client -> {
                    String fullClientName = client.getSurname() + " " + client.getName();
                    if (client.getMidname() != null) {
                        fullClientName += " " + client.getMidname();
                    }

                    return new ClientResponse(
                            fullClientName,
                            client.getPhone(),
                            client.getRegistrationDate(),
                            client.isBlocked()
                    );
                }).toList();
    }

    public List<TransactionResponse> getFilteredTransactionsResponses(TransactionFilterRequest transactionFilterRequest) {
        List<Transaction> transactionList = transactionService.getFilteredTransactions(
                transactionFilterRequest.getPhone(),
                transactionFilterRequest.getStartDate(),
                transactionFilterRequest.getEndDate(),
                transactionFilterRequest.getMinAmount(),
                transactionFilterRequest.getMaxAmount());

        return getFilteredTransactionResponses(transactionList);
    }

    private List<TransactionResponse> getFilteredTransactionResponses (List<Transaction> transactions) {
        return transactions.stream().map(transaction -> {
            String fullClientName = castToFullName(
                    transaction.getClient().getName(),
                    transaction.getClient().getSurname(),
                    transaction.getClient().getMidname());

            ClientResponse clientResponse = new ClientResponse(
                    fullClientName,
                    transaction.getClient().getPhone(),
                    transaction.getClient().getRegistrationDate(),
                    transaction.getClient().isBlocked()
            );

            return TransactionResponse.builder()
                    .id(transaction.getId())
                    .createDate(transaction.getCreateDate())
                    .updateDate(transaction.getUpdateDate())
                    .amount(transaction.getAmount())
                    .currency(transaction.getCurrency())
                    .status(transaction.getStatus())
                    .fee(transaction.getFee())
                    .feePercent(transaction.getFeePercent())
                    .clientResponse(clientResponse)
                    .build();
        }).toList();
    }


    public AppSetting getAppSetting(){
        return appSettingSingleton.getAppSetting();
    }

    public void removeExpiredAndVerifiedSmsCodes() {
        smsCodeService.removeExpiredAndVerifiedSmsCodes();
    }

    public void updateAppSetting(AppSetting appSetting) {
        appSettingSingleton.updateAppSetting(appSetting);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Employee employee = employeeRepository.findByEmail(email);

        if (employee == null) {
            throw new UsernameNotFoundException("Сотрудник c email: " + email + " не найден");
        }
        return (UserDetails) employee;
    }

    public EmployeeResponse getRegisteredEmployeeResponse(EmployeeRegistrationRequest request) {
        Employee addedEmployee = addEmployee(request);
        return new EmployeeResponse(castToFullName(
                addedEmployee.getName(),
                addedEmployee.getSurname(),
                addedEmployee.getMidname()),
                request.getEmail());
    }

    private Employee addEmployee(EmployeeRegistrationRequest request) {

        Employee employee = Employee.builder()
                .name(request.getName())
                .surname(request.getSurname())
                .midname(request.getSurname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Roles.ROLE_EMPLOYEE)
                .build();

        Optional<Employee> optionalEmployee = Optional.of(employeeRepository.save(employee));
        return optionalEmployee.orElseThrow(() -> new PaymentServiceException(
                "Сотрудник c email " + employee.getEmail() + " не зарегистрирован "));
    }

    public Employee addAdmin(Employee admin) {
        admin.setPassword(passwordEncoder.encode(admin.getPassword()));
        Optional<Employee> optionalEmployee = Optional.of(employeeRepository.save(admin));
        return optionalEmployee.orElseThrow(() -> new PaymentServiceException(
                "Сотрудник c email " + admin.getEmail() + " не зарегистрирован "));
    }

    public Employee findEmployeeByEmail(String email) {
        return employeeRepository.findByEmail(email);
    }
}

