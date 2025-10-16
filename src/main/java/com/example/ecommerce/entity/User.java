package com.example.ecommerce.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Table(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @NotBlank(message = "E-Mail darf nicht leer sein")
        @Email(message = "Bitte geben Sie eine gültige E-Mail-Adresse ein")
        @Column(nullable = false, unique = true, length = 100)
        private String email;

        @NotBlank(message = "Passwort darf nicht leer sein")
        @Size(min = 6, message = "Das Passwort muss mindestens 6 Zeichen lang sein")
        @Column(nullable = false)
        private String password;

        @NotBlank(message = "Vorname darf nicht leer sein")
        @Column(nullable = false, length = 50)
        private String firstName;

        @NotBlank(message = "Nachname darf nicht leer sein")
        @Column(nullable = false, length = 50)
        private String lastName;

        @Column(length = 50)
        private String roles = "ROLE_USER";
}
