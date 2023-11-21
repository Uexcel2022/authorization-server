package com.uexcel.authorizationserver.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import com.uexcel.authorizationserver.entity.Client;

@Repository
public interface ClientRepository extends JpaRepository<Client, String> {

    Optional<Client> findByClientId(String clientId);

}
