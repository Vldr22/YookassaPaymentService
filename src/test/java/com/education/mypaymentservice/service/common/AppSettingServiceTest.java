package com.education.mypaymentservice.service.common;

import com.education.mypaymentservice.model.entity.AppSetting;
import com.education.mypaymentservice.model.request.UpdateFeePercentRequest;
import com.education.mypaymentservice.repository.AppSettingRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AppSettingServiceTest {

    @Mock
    private AppSettingRepository appSettingRepository;

    @InjectMocks
    private AppSettingService appSettingService;

    private UpdateFeePercentRequest testUpdateFeePercentRequest;

    @BeforeEach
    void setUp() {
        testUpdateFeePercentRequest = new UpdateFeePercentRequest();
        testUpdateFeePercentRequest.setFeePercent(BigDecimal.valueOf(0.5));
        ReflectionTestUtils.setField(appSettingService, "defaultFeePercent", BigDecimal.valueOf(0.5));
    }

    @Test
    void init_ShouldSetDefaultFeePercent() {
        ArgumentCaptor<AppSetting> settingCaptor = ArgumentCaptor.forClass(AppSetting.class);
        when(appSettingRepository.findBySetting("FeePercent")).thenReturn(Optional.empty());

        appSettingService.init();

        verify(appSettingRepository).save(settingCaptor.capture());
        AppSetting savedSetting = settingCaptor.getValue();
        assertThat(savedSetting.getValue()).isEqualTo("0.5");
        assertThat(savedSetting.getModule()).isEqualTo("YookassaPaymentService");
        assertThat(savedSetting.getSetting()).isEqualTo("FeePercent");
    }

    @Test
    void updateFeePercent_WhenValidValue_ShouldUpdateFeePercent() {
        AppSetting existingSetting = new AppSetting();
        existingSetting.setSetting("FeePercent");
        existingSetting.setValue("0.5");

        when(appSettingRepository.findBySetting("FeePercent"))
                .thenReturn(Optional.of(existingSetting));

        testUpdateFeePercentRequest.setFeePercent(BigDecimal.valueOf(0.7));
        appSettingService.updateFeePercent(testUpdateFeePercentRequest);

        verify(appSettingRepository).save(argThat(setting ->
                setting.getValue().equals("0.7") &&
                        setting.getSetting().equals("FeePercent")
        ));
    }

    @Test
    void getAll_WhenAppSettingNotFound_ShouldThrowException() {
        when(appSettingRepository.findAll()).thenReturn(Collections.emptyList());

        assertThatThrownBy(() -> appSettingService.getAll())
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void getFeePercent_WhenValueExists_ShouldReturnCorrectValue() {
        when(appSettingRepository.findValueAppSettingBySetting("FeePercent"))
                .thenReturn(Optional.of("0.5"));

        BigDecimal result = appSettingService.getFeePercent();
        assertThat(result).isEqualByComparingTo(BigDecimal.valueOf(0.5));
    }

    @Test
    void getFeePercent_WhenSettingNotFound_ShouldThrowException() {
        when(appSettingRepository.findValueAppSettingBySetting("FeePercent"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> appSettingService.getFeePercent())
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void getFeePercent_WhenInvalidValue_ShouldThrowException() {
        when(appSettingRepository.findValueAppSettingBySetting("FeePercent"))
                .thenReturn(Optional.of("invalid"));

        assertThatThrownBy(() -> appSettingService.getFeePercent())
                .isInstanceOf(NumberFormatException.class);
    }
}
