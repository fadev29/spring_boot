package com.example.todolist.config;

import com.example.todolist.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
/*
* buat ngatur konfigurasi seperti menentukan request mana yang di izinkan
* ataun CROS, autentikasi.
* */
@Configuration
public class SecurityConfig {

    private  final  JwtRequestFilter jwtRequestFilter;
    private  final UserService userService;

    public SecurityConfig(JwtRequestFilter jwtRequestFilter, UserService userService) {
        this.jwtRequestFilter = jwtRequestFilter;
        this.userService = userService;
    }

    // method untuk konfigurasi keamanan spring
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // http.csrf(AbstractHttpConfigurer::disable)
        http.csrf(AbstractHttpConfigurer::disable)
                .cors(corsCustomizer -> corsCustomizer.configurationSource(new CorsConfigurationSource() {
                    @Override
                    public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
                        CorsConfiguration corsConfiguration = new CorsConfiguration();// buat mengizinkan kredensial
                        corsConfiguration.addAllowedOrigin("*");// mengizinkan app apa saja yang dapat mengakses resource
                        corsConfiguration.addAllowedMethod("*");// mengizinkan method semua header
                        corsConfiguration.addAllowedHeader("*");// mengizinkan semua method (post,put,get dll)
                        corsConfiguration.setMaxAge(3600L);// durasi cros
                        return corsConfiguration;
                    }
                    // pengaturan ototrisasi(siapa aja yang bisa mengakses endpoint)
                })).authorizeHttpRequests(session -> session
                        .requestMatchers(HttpMethod.GET,"/api/user/all").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.POST,"/api/user/register").permitAll()
                                .requestMatchers(HttpMethod.POST,"/api/user/login").permitAll()
                        .requestMatchers(HttpMethod.PUT,"/api/user/update").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE,"/api/user/delete").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET,"/api/todolist/**").permitAll()
                        .requestMatchers(HttpMethod.POST,"/api/todolist/**").permitAll()
                        .requestMatchers(HttpMethod.PUT,"/api/todolist/**").permitAll()
                                .requestMatchers(HttpMethod.DELETE,"/api/todolist/**").permitAll()
                                .requestMatchers(HttpMethod.GET,"/api/category/**").permitAll()
                                .requestMatchers(HttpMethod.POST,"/api/category/**").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.PUT,"/api/category/**").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.DELETE,"/api/category/**").hasRole("ADMIN")
                                .anyRequest().authenticated()
                        )
                // ngatur session untuk tidak menyimpan informasi user di dalam session tapi pakai jwt
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                ).authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return  http.build();
    }
    // method untuk ontetikasi data base
    @Bean
    public  AuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider authProviderr = new DaoAuthenticationProvider();
        authProviderr.setUserDetailsService(userDetailsService());
        authProviderr.setPasswordEncoder(passwordEncoder());
        return authProviderr;
    }
    // ngambil data user dari database
    @Bean
    public UserDetailsService userDetailsService(){
        return userService::loadUserByUsername;
    }
    // buat ngencode password
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

}
