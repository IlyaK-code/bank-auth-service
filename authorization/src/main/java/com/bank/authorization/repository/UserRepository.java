package com.bank.authorization.repository;

import com.bank.authorization.entity.User;
import feign.Param;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Query("SELECT u FROM User u WHERE u.profileId=:profileId")
    Optional<User> loadUserByProfileId(@Param("profileId") Long profileId);

    @Query("SELECT u FROM User u WHERE u.profileId=:profileId")
    @EntityGraph(attributePaths = {"tokens"})
    Optional<User> getUserByProfileId(@Param("profileId") Long profileId);

}
