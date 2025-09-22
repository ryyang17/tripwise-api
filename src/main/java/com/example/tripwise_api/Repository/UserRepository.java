package com.example.tripwise_api.Repository;

import com.example.tripwise_api.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
