package com.uexcel.authorizationserver.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.uexcel.authorizationserver.entity.AuthorizationConsent;

// import org.springframework.data.jpa.repository.JpaRepository;
// import org.springframework.stereotype.Repository;

@Repository
public interface AuthorizationConsentRepository
                extends JpaRepository<AuthorizationConsent, String> {

        Optional<AuthorizationConsent> findByRegisteredClientIdAndPrincipalName(String registeredClientId,
                        String principalName);

        void deleteByRegisteredClientIdAndPrincipalName(String registeredClientId, String principalName);
}
