package com.uexcel.authorizationserver.entity;

import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "user", uniqueConstraints = @UniqueConstraint(name = "email_unique", columnNames = "email"))
@AllArgsConstructor
@NoArgsConstructor
public class SecurityUsers {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;
    private String name;
    @Column(nullable = false)
    private String email;
    @Column(nullable = false)
    private String password;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(name = "user_authorities", joinColumns = {
            @JoinColumn(name = "user_id", referencedColumnName = "userid") }, inverseJoinColumns = {
                    @JoinColumn(name = "authority_id", referencedColumnName = "Id") })

    private Set<Authority> authorities;
    private boolean accountNonExpired;
    private boolean accountNonLocked;
    private boolean credentialsNonExpired;
    private boolean isEnabled;

}
