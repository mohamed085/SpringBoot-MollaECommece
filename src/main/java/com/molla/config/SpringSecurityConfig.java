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
import org.springframework.security.config.http.SessionCreationPolicy;
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
    public BCryptPasswordEncoder passwordEncoder() {
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
                .antMatchers("/admin/products/new", "/admin/products/delete/**").hasAnyAuthority("Admin", "Editor")
                .antMatchers("/admin/products/edit/**", "/admin/products/save", "/admin/products/check_unique").hasAnyAuthority("Admin", "Editor", "Salesperson")
                .antMatchers("/admin/products", "/admin/products/", "/admin/products/detail/**", "/admin/products/page/**").hasAnyAuthority("Admin", "Editor", "Salesperson", "Shipper")
                .antMatchers("/admin/products/**").hasAnyAuthority("Admin", "Editor")
                .antMatchers("/account_details", "/update_account_details", "/cart" , "/address_book/**").authenticated()
                .antMatchers("/admin/**").authenticated()
                .anyRequest().permitAll()
                .and()
                .formLogin().loginPage("/login").usernameParameter("email").permitAll()
                .and()
                .logout().permitAll()
                .and()
                .rememberMe().key("1234567890_aBcDeFgHiJkLmNoPqRsTuVwXyZ").tokenValiditySeconds(14 * 24 * 60 * 60)
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.ALWAYS);
    }


    // Before authenticated, all matchers can be ignored and all these are performed.
    // Like -> showing image in /login , all images,js and web jars files are detected
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/images/**", "/js/**", "/css/**", "/webjars/**", "https://pro.fontawesome.com/**", "https://getbootstrap.com/**");
    }

}
