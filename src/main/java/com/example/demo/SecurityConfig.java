package com.example.demo;

import com.example.demo.entity.Role;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Настройки безопасности: кому какие адреса API доступны.
 * Вход выполняется через HTTP Basic (логин — email, пароль — пароль пользователя).
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Правила доступа. Порядок важен: сначала более конкретные адреса, потом общие.
     * <ul>
     *   <li>/api/auth/register — открыт всем (регистрация); остальное под /api/auth (например, /me) — только после входа;</li>
     *   <li>каталог, поводы и цвета — открыты всем, чтобы товары можно было смотреть до регистрации;</li>
     *   <li>остальное под /api/customer, /api/florist, /api/courier, /api/admin — только своей роли;</li>
     *   <li>всё прочее — для любого авторизованного пользователя.</li>
     * </ul>
     * CSRF отключён: API работает без браузерных сессий и форм.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/register").permitAll()
                        .requestMatchers("/api/customer/categories/**",
                                "/api/customer/products/**",
                                "/api/customer/occasions",
                                "/api/customer/colors").permitAll()
                        .requestMatchers("/api/customer/**").hasRole(Role.CUSTOMER.name())
                        .requestMatchers("/api/florist/**").hasRole(Role.FLORIST.name())
                        .requestMatchers("/api/courier/**").hasRole(Role.COURIER.name())
                        .requestMatchers("/api/admin/**").hasRole(Role.ADMIN.name())
                        .anyRequest().authenticated()
                )
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }

    /**
     * Шифратор паролей. BCrypt хранит в базе не сам пароль, а необратимый хеш с «солью»,
     * поэтому даже при утечке базы пароли не восстановить.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
