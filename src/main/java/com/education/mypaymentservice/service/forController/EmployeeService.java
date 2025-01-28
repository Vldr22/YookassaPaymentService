package com.education.mypaymentservice.service.forController;

import com.education.mypaymentservice.exception.PaymentServiceException;
import com.education.mypaymentservice.model.entity.Employee;
import com.education.mypaymentservice.model.enums.Roles;
import com.education.mypaymentservice.model.request.EmployeeRegistrationRequest;
import com.education.mypaymentservice.model.request.TransactionFilterRequest;
import com.education.mypaymentservice.model.request.UpdateSettingRequest;
import com.education.mypaymentservice.model.response.EmployeeResponse;
import com.education.mypaymentservice.model.response.EmployeeTransactionResponse;
import com.education.mypaymentservice.repository.EmployeeRepository;
import com.education.mypaymentservice.service.common.ClientService;
import com.education.mypaymentservice.service.common.TransactionService;
import com.education.mypaymentservice.settings.AppSettingSingleton;
import com.education.mypaymentservice.model.response.ClientResponse;
import com.education.mypaymentservice.model.entity.AppSetting;
import com.education.mypaymentservice.model.entity.Transaction;
import com.education.mypaymentservice.service.security.SmsCodeService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static com.education.mypaymentservice.utils.NormalizeUtils.castToFullName;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmployeeService {

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

    public List<EmployeeTransactionResponse> getFilteredTransactionsResponses(
            TransactionFilterRequest transactionFilterRequest) {
        List<Transaction> transactionList = transactionService.getFilteredTransactions(
                transactionFilterRequest.phone(),
                transactionFilterRequest.startDate(),
                transactionFilterRequest.endDate(),
                transactionFilterRequest.minAmount(),
                transactionFilterRequest.maxAmount());

        return getFilteredTransactionResponses(transactionList);
    }

    private List<EmployeeTransactionResponse> getFilteredTransactionResponses (List<Transaction> transactions) {
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

            return EmployeeTransactionResponse.builder()
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


    public EmployeeResponse getRegisteredEmployeeResponse(EmployeeRegistrationRequest request, Roles role) {
        Employee addedEmployee = addEmployee(request, role);
        return new EmployeeResponse(castToFullName(
                addedEmployee.getName(),
                addedEmployee.getSurname(),
                addedEmployee.getMidname()),
                request.getEmail());
    }

    @Transactional
    public void updateAppSetting(UpdateSettingRequest request) {
        AppSetting appSetting = appSettingSingleton.getAppSetting();

        if (request.getMinutesExpireTimeSmsCode()!=0) {
            appSetting.setMinutesExpireTimeSmsCode(request.getMinutesExpireTimeSmsCode());
        }

        if (request.getSecondsJwtTokenExpirationAdmin()!=0) {
            appSetting.setSecondsJwtTokenExpirationAdmin(request.getSecondsJwtTokenExpirationAdmin());
        }

        if (request.getSecondsJwtTokenExpirationClient()!=0) {
            appSetting.setSecondsJwtTokenExpirationClient(request.getSecondsJwtTokenExpirationClient());
        }

        if (request.getSecondsJwtTokenExpirationEmployee()!=0) {
            appSetting.setSecondsJwtTokenExpirationEmployee(request.getSecondsJwtTokenExpirationEmployee());
        }

        if (request.getFeePercent().compareTo(BigDecimal.ZERO)>=0) {
            appSetting.setFeePercent(request.getFeePercent());
        }

        try {
            appSettingSingleton.updateAppSetting(appSetting);
            log.info("Настройки приложения успешно обновлены");
        } catch (Exception e) {
            throw new PaymentServiceException("Настройки приложения не были обновлены!", "settings", request.toString());
        }
    }

    public Employee addEmployee(EmployeeRegistrationRequest request, Roles role) {

        Employee employee = new Employee();

        employee.setName(request.getName());
        employee.setSurname(request.getSurName());
        employee.setMidname(request.getMidName());
        employee.setEmail(request.getEmail());
        employee.setPassword(passwordEncoder.encode(request.getPassword()));
        employee.setRole(role);

        Optional<Employee> optionalEmployee = Optional.of(employeeRepository.save(employee));
        return optionalEmployee.orElseThrow(() -> new PaymentServiceException(
                "Сотрудник c email " + employee.getEmail() + " не зарегистрирован "));
    }

    public Employee findEmployeeByEmail(String email) {
        Optional<Employee> optionalEmployee = employeeRepository.findByEmail(email);
        return optionalEmployee.orElseThrow(() -> new PaymentServiceException(
                "Сотрудник c email " + email + " не зарегистрирован "));
    }

    public boolean isFirstAdminSetupComplete() {
        return employeeRepository.countByRole(Roles.ROLE_ADMIN) > 0;
    }
}

