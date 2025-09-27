package com.zhvavyy.backend.model;


import com.zhvavyy.backend.model.enums.Role;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.Reference;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
@Table(name = "users")
public class User{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true,length = 50, nullable = false)
    private String email;

    @Column(unique = true,nullable = false)
    private String firstname;

    @Column(unique = true,nullable = false)
    private String lastname;

    @Column(unique = true,nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(unique = true,nullable = false)
    private Role role;

    @Builder.Default
    @OneToMany(mappedBy = "user")
    private List<Task> tasks = new ArrayList<>();

}
