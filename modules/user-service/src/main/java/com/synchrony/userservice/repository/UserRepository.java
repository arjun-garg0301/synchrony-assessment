package com.synchrony.userservice.repository;

import com.synchrony.userservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for User entity operations.
 * Enhanced with additional query methods and performance optimizations.
 * 
 * @author Synchrony Team
 * @version 1.0.0
 * @since 1.0.0
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Finds a user by username.
     * 
     * @param username the username to search for
     * @return Optional containing the user if found
     */
    Optional<User> findByUsername(String username);

    /**
     * Finds a user by email.
     * 
     * @param email the email to search for
     * @return Optional containing the user if found
     */
    Optional<User> findByEmail(String email);

    /**
     * Checks if a user exists by username.
     * 
     * @param username the username to check
     * @return true if user exists, false otherwise
     */
    boolean existsByUsername(String username);

    /**
     * Checks if a user exists by email.
     * 
     * @param email the email to check
     * @return true if user exists, false otherwise
     */
    boolean existsByEmail(String email);

    /**
     * Finds an active user by username.
     * 
     * @param username the username to search for
     * @return Optional containing the active user if found
     */
    @Query("SELECT u FROM User u WHERE u.username = :username AND u.isActive = true")
    Optional<User> findActiveUserByUsername(@Param("username") String username);

    /**
     * Finds a user by username or email.
     * 
     * @param username the username to search for
     * @param email the email to search for
     * @return Optional containing the user if found
     */
    @Query("SELECT u FROM User u WHERE u.username = :username OR u.email = :email")
    Optional<User> findByUsernameOrEmail(@Param("username") String username, @Param("email") String email);

    /**
     * Finds users by active status.
     * 
     * @param isActive the active status
     * @return list of users with the specified active status
     */
    @Query("SELECT u FROM User u WHERE u.isActive = :isActive")
    java.util.List<User> findByIsActive(@Param("isActive") Boolean isActive);

    /**
     * Counts active users.
     * 
     * @return number of active users
     */
    @Query("SELECT COUNT(u) FROM User u WHERE u.isActive = true")
    long countActiveUsers();

    /**
     * Finds users created after a specific date.
     * 
     * @param date the date to search after
     * @return list of users created after the specified date
     */
    @Query("SELECT u FROM User u WHERE u.createdAt > :date")
    java.util.List<User> findUsersCreatedAfter(@Param("date") java.time.LocalDateTime date);
}