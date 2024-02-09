package com.alibou.security.user;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 访问用户表的 JPA 接口
 */
public interface UserRepository extends JpaRepository<User, Integer> {

  Optional<User> findByEmail(String email);

}
