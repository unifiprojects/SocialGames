package com.maurosalani.project.attsd.repositories;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.junit4.SpringRunner;

import com.maurosalani.project.attsd.model.Game;
import com.maurosalani.project.attsd.model.User;

@DataJpaTest
@RunWith(SpringRunner.class)
public class UserRepositoryTest {

	@Autowired
	private UserRepository repository;

	@Autowired
	private TestEntityManager entityManager;

	@Test
	public void testFindAllWithExistingUser() {
		User user = new User(null, "test", "pwd", null, null, null);
		User saved = entityManager.persistFlushFind(user);
		Collection<User> users = repository.findAll();

		assertThat(users).containsExactly(saved);
	}

	@Test
	public void testFindByUsername() {
		User user = new User(null, "test", "pwd", null, null, null);
		User saved = entityManager.persistFlushFind(user);
		User userFound = repository.findByUsername("test");

		assertThat(userFound).isEqualTo(saved);
	}

	@Test
	public void testUsernameAndPasswordAreMandatoryWhenUserIsSaved() {
		User userNoUsername = new User(null, null, "pwd", null, null, null);
		User userNoPassword = new User(null, "username", null, null, null, null);
		User userNoUsernameAndPassword = new User(null, null, "pwd", null, null, null);

		assertThatExceptionOfType(DataIntegrityViolationException.class)
				.isThrownBy(() -> repository.saveAndFlush(userNoUsername));
		assertThatExceptionOfType(DataIntegrityViolationException.class)
				.isThrownBy(() -> repository.saveAndFlush(userNoPassword));
		assertThatExceptionOfType(DataIntegrityViolationException.class)
				.isThrownBy(() -> repository.saveAndFlush(userNoUsernameAndPassword));
	}

	@Test
	public void testFollowedListIsPersistedWhenUserIsSaved() {
		List<User> followed = new LinkedList<User>();
		followed.add(new User(null, "one", "pwd", null, null, null));
		followed.add(new User(null, "two", "pwd", null, null, null));
		User user = new User(null, "test", "pwd", followed, null, null);

		User saved = repository.save(user);

		assertThat(followed).isEqualTo(saved.getFollowedUsers());
	}

	@Test
	public void testFollowerListIsSetWhenUserIsSaved() {
		List<User> followed = new LinkedList<User>();
		followed.add(new User(null, "one", "pwd", null, null, null));
		followed.add(new User(null, "two", "pwd", null, null, null));
		User user = new User(null, "test", "pwd", followed, null, null);

		User saved = entityManager.persistFlushFind(user);

		List<User> follower1 = saved.getFollowedUsers().get(0).getFollowerUsers();
		List<User> follower2 = saved.getFollowedUsers().get(1).getFollowerUsers();
		assertThat(follower1).containsExactly(saved);
		assertThat(follower2).containsExactly(saved);
	}

	@Test
	public void testGamesListIsPersistedWhenUserIsSaved() {
		List<Game> games = new LinkedList<Game>();
		games.add(new Game(null, "game1", "description1", new Date(), null));
		games.add(new Game(null, "game2", "description2", new Date(), null));
		User user = new User(null, "test", "pwd", null, null, games);

		User saved = repository.save(user);

		assertThat(games).isEqualTo(saved.getGames());
	}

	@Test
	public void testFollowedUsersAreUpdatedWithExistingUser() {
		List<User> followed = new LinkedList<User>();
		followed.add(new User(null, "followed_one", "pwd_one", null, null, null));
		followed.add(new User(null, "followed_two", "pwd_two", null, null, null));
		User user = new User(null, "user", "pwd", followed, null, null);

		User savedAndToBeUpdated = entityManager.persistFlushFind(user);

		List<User> replacementFollowedUsers = new LinkedList<User>();
		replacementFollowedUsers
				.add(new User(null, "followed_one_replacement", "pwd_one_replacement", null, null, null));
		replacementFollowedUsers
				.add(new User(null, "followed_two_replacement", "pwd_two_replacement", null, null, null));
		savedAndToBeUpdated.setFollowedUsers(replacementFollowedUsers);

		User updated = repository.save(savedAndToBeUpdated);

		assertThat(replacementFollowedUsers).isEqualTo(updated.getFollowedUsers());
	}

	@Test
	public void testDeleteSavedUser() {
		User user = new User(null, "test", "pwd", null, null, null);
		User saved = entityManager.persistFlushFind(user);
		repository.delete(saved);
		User found = entityManager.find(User.class, saved.getId());

		assertThat(found).isEqualTo(null);
	}
}
