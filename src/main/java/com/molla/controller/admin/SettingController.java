package com.molla.controller.admin;

import com.molla.helper.SettingHelper;
import com.molla.model.Currency;
import com.molla.model.Setting;
import com.molla.repository.CurrencyRepository;
import com.molla.service.SettingService;
import com.molla.util.GeneralSettingBag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/admin/settings")
public class SettingController {

    private final SettingService settingService;
    private final CurrencyRepository currencyRepository;

    public SettingController(SettingService settingService, CurrencyRepository currencyRepository) {
        this.settingService = settingService;
        this.currencyRepository = currencyRepository;
    }

    @GetMapping("")
    public String listAll(Model model) {
        List<Setting> listSettings = settingService.listAllSettings();
        List<Currency> listCurrencies = currencyRepository.findAllByOrderByNameAsc();

        model.addAttribute("listCurrencies", listCurrencies);

        for (Setting setting : listSettings) {
            model.addAttribute(setting.getKey(), setting.getValue());
        }

        return "admin/settings/settings";
    }

    @PostMapping("/save_general")
    public String saveGeneralSettings(@RequestParam("fileImage") MultipartFile multipartFile,
                                      HttpServletRequest request, RedirectAttributes ra) throws IOException {
        GeneralSettingBag settingBag = settingService.getGeneralSettings();

        SettingHelper.saveSiteLogo(multipartFile, settingBag);
        SettingHelper.saveCurrencySymbol(request, settingBag, currencyRepository);

        SettingHelper.updateSettingValuesFromForm(request, settingBag.list(), settingService);

        ra.addFlashAttribute("messageSuccess", "General settings have been saved.");

        return "redirect:/admin/settings";
    }


}
