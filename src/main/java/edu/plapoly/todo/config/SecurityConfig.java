// package edu.plapoly.todo.config;

// import edu.plapoly.todo.security.AppUserDetailsService;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
// import org.springframework.security.config.annotation.web.builders.HttpSecurity;
// import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
// import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
// import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
// import org.springframework.security.crypto.password.PasswordEncoder;
// import org.springframework.security.web.SecurityFilterChain;

// @Configuration
// @EnableWebSecurity
// public class SecurityConfig {

//     private final AppUserDetailsService userDetailsService;

//     public SecurityConfig(AppUserDetailsService userDetailsService) {
//         this.userDetailsService = userDetailsService;
//     }

//     @Bean
//     public PasswordEncoder passwordEncoder() {
//         // BCrypt: salted, slow-by-design hashing appropriate for storing passwords.
//         return new BCryptPasswordEncoder();
//     }

//     @Bean
//     public DaoAuthenticationProvider authenticationProvider() {
//         DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
//         provider.setUserDetailsService(userDetailsService);
//         provider.setPasswordEncoder(passwordEncoder());
//         return provider;
//     }

//     @Bean
//     public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//         http
//             .authorizeHttpRequests(auth -> auth
//                 .requestMatchers("/", "/register", "/login", "/css/**", "/js/**", "/images/**", "/error").permitAll()
//                 .anyRequest().authenticated()
//             )
//             .formLogin(form -> form
//                 .loginPage("/login")
//                 .defaultSuccessUrl("/dashboard", true)
//                 .failureUrl("/login?error=true")
//                 .permitAll()
//             )
//             .logout(logout -> logout
//                 .logoutUrl("/logout")
//                 .logoutSuccessUrl("/login?logout=true")
//                 .permitAll()
//             )
//             // H2/DB consoles aside, default CSRF protection stays on for all state-changing routes.
//             .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin));

//         return http.build();
//     }
// }


package edu.plapoly.todo.config;

import edu.plapoly.todo.security.AppUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final AppUserDetailsService userDetailsService;

    public SecurityConfig(AppUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    /**
     * Password encoder used when storing and verifying passwords.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Database-backed authentication provider.
     *
     * AppUserDetailsService loads the user from the database
     * using the username submitted during login.
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider();

        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());

        return provider;
    }

    /**
     * Main Spring Security configuration.
     */
    @Bean
    public SecurityFilterChain filterChain(
            HttpSecurity http,
            AuthenticationProvider authenticationProvider) throws Exception {

        http
            /*
             * Explicitly tell Spring Security to use our
             * database-backed AuthenticationProvider.
             */
            .authenticationProvider(authenticationProvider)

            /*
             * Pages/resources that do not require authentication.
             */
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/",
                    "/register",
                    "/login",
                    "/css/**",
                    "/js/**",
                    "/images/**",
                    "/error"
                ).permitAll()

                /*
                 * Everything else requires a logged-in user.
                 */
                .anyRequest().authenticated()
            )

            /*
             * Form-based login.
             */
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/dashboard", true)
                .failureUrl("/login?error=true")
                .permitAll()
            )

            /*
             * Logout.
             */
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout=true")
                .permitAll()
            )

            /*
             * Keep CSRF protection enabled.
             *
             * frameOptions(sameOrigin) allows pages such as
             * an H2 console to be displayed in same-origin frames
             * if you use one during development.
             */
            .headers(headers -> headers
                .frameOptions(
                    HeadersConfigurer.FrameOptionsConfig::sameOrigin
                )
            );

        return http.build();
    }
}
