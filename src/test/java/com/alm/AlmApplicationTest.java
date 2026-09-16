package com.alm;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import static org.junit.jupiter.api.Assertions.assertTrue;
class AlmApplicationTest {
 @Test void applicationClassLoads(){ AlmApplication.class.getName(); }
 @Test void seedPasswordHashMatchesPassword(){assertTrue(new BCryptPasswordEncoder().matches("password","$2a$10$5hDDFMtwrzvP.teYQ6I/VOoSflVd/2ct/AJbRrRIRjxAj.ageqZPm"));}
}
