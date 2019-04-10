package com.netcracker.impl.database.repositories;

import com.netcracker.impl.database.entities.UserEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UserRepository extends CrudRepository<UserEntity, Integer> {

    @Query("SELECT u.email FROM UserEntity u ")
    List<String> findAllEmails();

    @Query("SELECT u.userId FROM UserEntity u ")
    List<Integer> findAllIds();

}