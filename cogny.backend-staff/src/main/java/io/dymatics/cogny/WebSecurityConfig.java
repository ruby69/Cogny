
package io.dymatics.cogny;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import io.dymatics.cogny.domain.model.User;
import io.dymatics.cogny.staff.firebase.FirebaseAuthenticationProvider;
import io.dymatics.cogny.staff.firebase.FirebaseAuthenticationTokenFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private FirebaseAuthenticationProvider authenticationProvider;

    @Value("${security.logout.success-url}") private String logoutSuccessUrl;

    @Bean
    @Override
    public AuthenticationManager authenticationManager() throws Exception {
        return new ProviderManager(Arrays.asList(authenticationProvider));
    }

    public FirebaseAuthenticationTokenFilter authenticationTokenFilterBean() throws Exception {
        FirebaseAuthenticationTokenFilter authenticationTokenFilter = new FirebaseAuthenticationTokenFilter();
        authenticationTokenFilter.setAuthenticationManager(authenticationManager());
        authenticationTokenFilter.setAuthenticationSuccessHandler((request, response, authentication) -> {});
        return authenticationTokenFilter;
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .cors()
        .and()
            .csrf().disable()
            .authorizeRequests()
                .antMatchers("/auth/token/**").authenticated()
                .antMatchers("/auth/signout").permitAll()

                .antMatchers("/partners").hasAuthority(User.Role.ADMIN.name())
                .antMatchers("/vehicles/**").hasAnyAuthority(User.Role.ADMIN.name(), User.Role.PARTNER_MECHANIC.name())
                .antMatchers("/vehicles/**").hasAnyAuthority(User.Role.ADMIN.name(), User.Role.ADMIN_ANALYST.name(), User.Role.PARTNER_MECHANIC.name())
                .antMatchers("/invitation/**").hasAnyAuthority(User.Role.ADMIN.name(), User.Role.PARTNER_MECHANIC.name())
                .antMatchers("/obds/**").hasAnyAuthority(User.Role.ADMIN.name(), User.Role.PARTNER_MECHANIC.name())
                .antMatchers("/dtc/**").hasAnyAuthority(User.Role.ADMIN.name(), User.Role.ADMIN_ANALYST.name(), User.Role.PARTNER_MECHANIC.name())

                .antMatchers("/auth/denied").permitAll()
                .antMatchers("/auth/profile").permitAll()
                .antMatchers("/auth/invitation/**").permitAll()

                .anyRequest().authenticated()

        .and()
            .logout()
                .logoutUrl("/auth/signout")
                .logoutSuccessUrl(logoutSuccessUrl)
                .deleteCookies("JSESSIONID")

        .and()
            .exceptionHandling()
                .accessDeniedPage("/auth/denied");
        http
            .addFilterBefore(authenticationTokenFilterBean(), UsernamePasswordAuthenticationFilter.class);

    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring()
        .antMatchers("/auth/denied")
        .antMatchers("/auth/unauthenticated")
        .antMatchers("/error")
        .antMatchers("/healthcheck")
        .antMatchers(HttpMethod.OPTIONS);
    }
}
