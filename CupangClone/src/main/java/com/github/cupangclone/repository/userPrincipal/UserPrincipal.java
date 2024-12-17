package com.github.cupangclone.repository.userPrincipal;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "user_principal")
public class UserPrincipal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_principal_id")
    private Long userPrincipalId;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "phone_num", length = 13, nullable = false)
    private String phoneNum;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "address", length = 100, nullable = false)
    private String address;

    @Column(name = "gender", length = 2, nullable = false)
    private String gender;

    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Column(name = "is_deleted")
    private Boolean isDeleted;

}
