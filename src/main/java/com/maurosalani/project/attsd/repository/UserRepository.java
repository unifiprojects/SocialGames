package com.maurosalani.project.attsd.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.maurosalani.project.attsd.model.Game;
import com.maurosalani.project.attsd.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

	Optional<User> findByUsername(String string);

	List<User> findByUsernameLike(String string);

	Optional<User> findByUsernameAndPassword(String string, String string2);

	@Query("select u.games from User u where u.username = ?1")
	List<Game> findGamesOfUserByUsername(String username);

	@Query("select u.followedUsers from User u where u.username = ?1")
	List<User> findFollowedOfUserByUsername(String string);

	@Query("select u.followerUsers from User u where u.username = ?1")
	List<User> findFollowerOfUserByUsername(String string);

}
