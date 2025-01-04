package com.education.mypaymentservice.repository;

import com.education.mypaymentservice.model.entity.AppSetting;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppSettingRepository extends JpaRepository<AppSetting, Integer> {
}
