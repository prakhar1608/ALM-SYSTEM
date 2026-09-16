package com.alm.config;

import java.util.Map;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
public class DatabaseUserDetailsService implements UserDetailsService {
 private final JdbcTemplate jdbc;
 public DatabaseUserDetailsService(JdbcTemplate jdbc){this.jdbc=jdbc;}
 public UserDetails loadUserByUsername(String email) {
   try { Map<String,Object> u=jdbc.queryForMap("select email,password_hash,role,status from users where email=?",email);
     return User.withUsername((String)u.get("EMAIL")).password((String)u.get("PASSWORD_HASH"))
       .authorities(new SimpleGrantedAuthority("ROLE_"+u.get("ROLE"))).disabled(!"ACTIVE".equals(u.get("STATUS"))).build();
   } catch(Exception e){ throw new UsernameNotFoundException(email); }
 }
}
