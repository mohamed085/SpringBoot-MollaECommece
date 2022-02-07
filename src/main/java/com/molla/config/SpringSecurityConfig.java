package com.molla.config;

import com.molla.service.MollaUserDetailsServices;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {

    private final MollaUserDetailsServices mollaUserDetailsServices;

    public SpringSecurityConfig(MollaUserDetailsServices mollaUserDetailsServices) {
        this.mollaUserDetailsServices = mollaUserDetailsServices;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider  authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(mollaUserDetailsServices);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authenticationProvider());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
//        http.authorizeRequests().anyRequest().permitAll();

        http.authorizeRequests()
                .antMatchers("/admin/users/**").hasAuthority("Admin")
                .antMatchers("/admin/categories/**", "/admin/brands/**").hasAnyAuthority("Admin", "Editor")
                .antMatchers("/admin/products/**").hasAnyAuthority("Admin", "Editor", "Shipper", "Salesperson")
                .anyRequest().authenticated()
                .and()
                .formLogin().loginPage("/login").usernameParameter("email").permitAll()
                .and()
                .logout().permitAll()
                .and()
                .rememberMe().key("AbcDefgKLDSLmvop_0123456789").tokenValiditySeconds(7 * 24 * 60 * 60);
    }


    // Before authenticated, all matchers can be ignored and all these are performed.
    // Like -> showing image in /login , all images,js and web jars files are detected
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/images/**", "/js/**", "/css/**", "/webjars/**", "https://pro.fontawesome.com/**", "https://getbootstrap.com/**");
    }

}
