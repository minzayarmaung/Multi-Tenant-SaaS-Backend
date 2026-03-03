package com.project.Multi_Tenant_SaaS_Backend.security.JWT;

import com.project.Multi_Tenant_SaaS_Backend.data.models.User;
import com.project.Multi_Tenant_SaaS_Backend.data.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found"));

        Long companyId = user.getCompany() != null
                ? user.getCompany().getId()
                : null;


        return new UserPrincipal(
                user.getId(),
                companyId,
                user.getRole(),
                user.getEmail(),
                user.getPassword()
        );
    }
}