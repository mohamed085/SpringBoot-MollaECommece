package com.molla.service.serviceImp;

import com.molla.model.Setting;
import com.molla.model.SettingCategory;
import com.molla.repository.SettingRepository;
import com.molla.service.SettingService;
import com.molla.util.GeneralSettingBag;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SettingServiceImp implements SettingService {

    private final SettingRepository settingRepository;

    public SettingServiceImp(SettingRepository settingRepository) {
        this.settingRepository = settingRepository;
    }

    @Override
    public List<Setting> listAllSettings() {
        return (List<Setting>) settingRepository.findAll();
    }

    @Override
    public GeneralSettingBag getGeneralSettings() {
        List<Setting> settings = new ArrayList<>();

        List<Setting> generalSettings = settingRepository.findByCategory(SettingCategory.GENERAL);
        List<Setting> currencySettings = settingRepository.findByCategory(SettingCategory.CURRENCY);

        settings.addAll(generalSettings);
        settings.addAll(currencySettings);

        return new GeneralSettingBag(settings);

    }

    @Override
    public List<Setting> getListGeneralSettings() {
        List<Setting> settings = new ArrayList<>();

        List<Setting> generalSettings = settingRepository.findByCategory(SettingCategory.GENERAL);
        List<Setting> currencySettings = settingRepository.findByCategory(SettingCategory.CURRENCY);

        settings.addAll(generalSettings);
        settings.addAll(currencySettings);
        return settings;
    }

    @Override
    public void saveAll(Iterable<Setting> settings) {
        settingRepository.saveAll(settings);
    }

    @Override
    public List<Setting> getMailServerSettings() {
        return null;
    }

    @Override
    public List<Setting> getMailTemplateSettings() {
        return null;
    }

    @Override
    public List<Setting> getCurrencySettings() {
        return null;
    }
}
