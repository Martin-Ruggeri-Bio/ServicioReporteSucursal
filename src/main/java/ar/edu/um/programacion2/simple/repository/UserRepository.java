package ar.edu.um.programacion2.simple.repository;
import java.util.Optional;

import ar.edu.um.programacion2.simple.model.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByUserName(String userName);
    Optional<User> findByToken(String token);
    boolean existsByUserName(String userName);
}
