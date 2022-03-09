package com.molla.service;


import com.molla.model.Setting;
import com.molla.util.GeneralSettingBag;

import java.util.List;

public interface SettingService {

    public List<Setting> listAllSettings();

    public GeneralSettingBag getGeneralSettings();

    public void saveAll(Iterable<Setting> settings);

    public List<Setting> getMailServerSettings();

    public List<Setting> getMailTemplateSettings();

    public List<Setting> getCurrencySettings();

}
