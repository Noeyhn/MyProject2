package com.github.cupangclone.repository.userPrincipalRoles;

import com.github.cupangclone.repository.roles.Roles;
import com.github.cupangclone.repository.userPrincipal.UserPrincipal;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "user_principal_roles")
public class UserPrincipalRoles {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_principal_roles")
    private Long userPrincipalRoles;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_principal_id")
    private UserPrincipal userPrincipal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id")
    private Roles roles;

}
