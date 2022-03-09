package com.molla.helper;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import com.molla.model.Currency;
import com.molla.model.Setting;
import com.molla.repository.CurrencyRepository;
import com.molla.service.SettingService;
import com.molla.util.FileUploadUtil;
import com.molla.util.GeneralSettingBag;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

public class SettingHelper {
    public static void saveSiteLogo(MultipartFile multipartFile, GeneralSettingBag settingBag) throws IOException {
        if (!multipartFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
            String value = "/site-logo/" + fileName;
            settingBag.updateSiteLogo(value);

            String uploadDir = "../site-logo/";
            FileUploadUtil.cleanDir(uploadDir);
            FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
        }
    }

    public static void saveCurrencySymbol(HttpServletRequest request, GeneralSettingBag settingBag,
                                          CurrencyRepository currencyRepo) {
        Integer currencyId = Integer.parseInt(request.getParameter("CURRENCY_ID"));
        Optional<Currency> findByIdResult = currencyRepo.findById(currencyId);

        if (findByIdResult.isPresent()) {
            Currency currency = findByIdResult.get();
            settingBag.updateCurrencySymbol(currency.getSymbol());
        }
    }

    public static void updateSettingValuesFromForm(HttpServletRequest request, List<Setting> listSettings,
                                                   SettingService service) {
        for (Setting setting : listSettings) {
            String value = request.getParameter(setting.getKey());
            if (value != null) {
                setting.setValue(value);
            }
        }

        service.saveAll(listSettings);
    }

}
