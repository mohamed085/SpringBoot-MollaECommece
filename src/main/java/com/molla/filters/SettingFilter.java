package com.molla.filters;

import com.molla.model.Setting;
import com.molla.service.SettingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

@Slf4j
@Component
public class SettingFilter implements Filter {

    private final SettingService service;

    public SettingFilter(SettingService service) {
        this.service = service;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        // TODO Auto-generated method stub

        log.debug("SettingFilter | doFilter is called");

        HttpServletRequest servletRequest = (HttpServletRequest) request;
        String url = servletRequest.getRequestURL().toString();

        log.debug("SettingFilter | doFilter | url : " + url);

        if (url.endsWith(".css") || url.endsWith(".js") || url.endsWith(".png") ||
                url.endsWith(".jpg")) {
            log.debug("SettingFilter | doFilter | .css , .js , .png , . jpg | url : " + url);
            chain.doFilter(request, response);
            return;
        }

        List<Setting> generalSettings = service.getListGeneralSettings();

        generalSettings.forEach(setting -> {
            log.debug("SettingFilter | doFilter | generalSettings : " + generalSettings);
            request.setAttribute(setting.getKey(), setting.getValue());
        });

        chain.doFilter(request, response);

    }


}
