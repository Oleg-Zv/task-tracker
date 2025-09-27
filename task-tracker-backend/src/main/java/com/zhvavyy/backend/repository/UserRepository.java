package com.zhvavyy.backend.repository;

import com.zhvavyy.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long>,
        JpaSpecificationExecutor<User> {

Optional<User>findByEmailAndPassword(String email,String password);
Optional<User>findByEmail(String email);
}
