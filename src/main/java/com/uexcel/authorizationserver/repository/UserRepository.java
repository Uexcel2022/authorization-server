package com.uexcel.authorizationserver.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.uexcel.authorizationserver.entity.SecurityUsers;

@Repository
public interface UserRepository extends JpaRepository<SecurityUsers, Long> {

    SecurityUsers findByEmail(String email);

}
