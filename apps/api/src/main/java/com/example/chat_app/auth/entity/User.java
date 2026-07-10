package com.example.chat_app.auth.entity;

import jakarta.persistence.*;  //making it jpa entity;
import java.time.LocalDateTime;
import lombok.*;

@Entity //This Java class should be stored in the database."
@Table(name = "users")  //it hibernate to users,User is a reserved name in db,
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {  //need to have a primary key

    @Id //This field uniquely identifies a user.
    @GeneratedValue(strategy = GenerationType.IDENTITY)  //Instead of manually assigning ID 1 2 3
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
}
    @Column(nullable = false)
    @Builder.Default
    private Boolean enabled = true;
}