package dev.mixsource.adapter.components;

import dev.mixsource.model.entity.UserEntity;
import dev.mixsource.port.output.repository.Users;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private Users users;

    public CustomUserDetailsService(Users users) {
        this.users = users;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity user = this.users.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username or email: " + username));

        Set<GrantedAuthority> authorities = Set.of(new SimpleGrantedAuthority("USER"));

        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), authorities);
    }
}