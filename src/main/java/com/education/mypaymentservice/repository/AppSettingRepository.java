package com.education.mypaymentservice.repository;

import com.education.mypaymentservice.model.entity.AppSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface AppSettingRepository extends JpaRepository<AppSetting, Integer> {

    @Query("SELECT a.value FROM AppSetting a WHERE a.setting= :setting")
    Optional<String> findValueAppSettingBySetting(@Param("setting") String setting);

    Optional<AppSetting> findBySetting(String setting);

}
