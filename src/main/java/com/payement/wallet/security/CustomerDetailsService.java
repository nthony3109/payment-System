package com.payement.wallet.security;

import com.payement.wallet.Entity.UserEntity;
import com.payement.wallet.Repo.UserRepo;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@RequiredArgsConstructor
public class CustomerDetailsService implements UserDetailsService {
    private final UserRepo repo;
    @Override
    public UserDetails loadUserByUsername(@NonNull String phoneNumber) throws UsernameNotFoundException {
        UserEntity user = repo.findByPhoneNumber(phoneNumber).orElseThrow(() -> new UsernameNotFoundException(" customer not found with phone number " + phoneNumber));
        return new CustomerPrincipal(user);
    }
}
