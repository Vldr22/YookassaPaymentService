package com.education.mypaymentservice.service.common;

import com.education.mypaymentservice.model.entity.AppSetting;
import com.education.mypaymentservice.model.request.UpdateFeePercentRequest;
import com.education.mypaymentservice.repository.AppSettingRepository;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AppSettingService {

    private final AppSettingRepository appSettingRepository;

    @Value("${feePercent}")
    private BigDecimal defaultFeePercent;

    @PostConstruct
    public void init() {
        UpdateFeePercentRequest updateFeePercentRequest = new UpdateFeePercentRequest();
        updateFeePercentRequest.setFeePercent(defaultFeePercent);
        updateFeePercent(updateFeePercentRequest);
    }

    public void updateFeePercent(UpdateFeePercentRequest updateSettingRequest) {
        AppSetting appSetting = appSettingRepository.findBySetting("FeePercent")
                .orElseGet(() -> {
                    AppSetting newSetting = new AppSetting();
                    newSetting.setModule("YookassaPaymentService");
                    newSetting.setSetting("FeePercent");
                    return newSetting;
                });

        appSetting.setValue(String.valueOf(updateSettingRequest.getFeePercent()));
        appSettingRepository.save(appSetting);
    }

    public BigDecimal getFeePercent() {
        Optional<String> optionalString = appSettingRepository.findValueAppSettingBySetting("FeePercent");
        String result = optionalString.orElseThrow(() -> new EntityNotFoundException("Значение настройки FeePercent " +
                "не найдено"));
        return new BigDecimal(result);
    }

    public List<AppSetting> getAll() {
        return Optional.of(appSettingRepository.findAll())
                .filter(list -> !list.isEmpty())
                .orElseThrow(() -> new EntityNotFoundException("No settings found"));
    }
}
