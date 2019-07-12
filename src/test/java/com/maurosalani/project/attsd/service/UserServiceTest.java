package com.maurosalani.project.attsd.service;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.ignoreStubs;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.Date;
import java.util.Optional;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.maurosalani.project.attsd.exception.UserNotFoundException;
import com.maurosalani.project.attsd.model.Game;
import com.maurosalani.project.attsd.model.User;
import com.maurosalani.project.attsd.repository.UserRepository;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest {

	@Mock
	private UserRepository userRepository;

	@InjectMocks
	private UserService userService;

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void testFindAllWhenDatabaseIsEmpty() {
		when(userRepository.findAll()).thenReturn(Collections.emptyList());
		assertThat(userService.getAllUsers()).isEmpty();
	}

	@Test
	public void testFindAllWithExistingUsers() {
		User user1 = new User(1l, "username1", "pwd1");
		User user2 = new User(2l, "username2", "pwd2");
		when(userRepository.findAll()).thenReturn(asList(user1, user2));
		assertThat(userService.getAllUsers()).containsExactly(user1, user2);
	}

	@Test
	public void testGetUserByIdWhenUserDoesNotExist_ShouldThrowException() throws Exception {
		when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
		assertThatExceptionOfType(UserNotFoundException.class).isThrownBy(() -> userService.getUserById(1L));
	}

	@Test
	public void testGetUserByIdWithExistingUser() throws Exception {
		User user = new User(1L, "username", "pwd");
		when(userRepository.findById(1L)).thenReturn(Optional.of(user));
		assertThat(userService.getUserById(1L)).isEqualTo(user);
	}

	@Test
	public void testGetUserByIdWithIdNull() throws Exception {
		assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> userService.getUserById(null));
	}

	@Test
	public void testGetUserByUsernameWhenUserDoesNotExist() {
		when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());
		assertThatExceptionOfType(UserNotFoundException.class)
				.isThrownBy(() -> userService.getUserByUsername("username"));
	}

	@Test
	public void testGetUserByUsernameWithUsernameNull() {
		assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> userService.getUserByUsername(null));
	}

	@Test
	public void testGetUserByUsernameWithExistingUser() throws Exception {
		User user = new User(1L, "username", "pwd");
		when(userRepository.findByUsername("username")).thenReturn(Optional.of(user));
		assertThat(userService.getUserByUsername("username")).isEqualTo(user);
	}

	@Test
	public void testGetUserByUsernameAndPassword_WhenUserDoesNotExist() {
		when(userRepository.findByUsernameAndPassword(anyString(), anyString())).thenReturn(Optional.empty());
		assertThatExceptionOfType(UserNotFoundException.class)
				.isThrownBy(() -> userService.getUserByUsernameAndPassword("username", "password"));
	}

	@Test
	public void testGetUserByUsernameAndPassword_WithUsernameOrPasswordNull() {
		assertThatExceptionOfType(IllegalArgumentException.class)
				.isThrownBy(() -> userService.getUserByUsernameAndPassword("username", null));
		assertThatExceptionOfType(IllegalArgumentException.class)
				.isThrownBy(() -> userService.getUserByUsernameAndPassword(null, "password"));
	}

	@Test
	public void testGetUsersByUsernameLikeWhenUserDoesNotExist() {
		when(userRepository.findByUsernameLike(anyString())).thenReturn(Collections.emptyList());
		assertThat(userService.getUsersByUsernameLike("username")).isEmpty();
	}

	@Test
	public void testGetUsersByUsernameLikeWithExistingUsers() {
		User user1 = new User(1L, "username1", "pwd1");
		User user2 = new User(2L, "username2", "pwd2");
		when(userRepository.findByUsernameLike("username")).thenReturn(asList(user1, user2));
		assertThat(userService.getUsersByUsernameLike("username")).containsExactly(user1, user2);
	}

	@Test
	public void testGetUsersByUsernameLikeWithUsernameNull() {
		assertThatExceptionOfType(IllegalArgumentException.class)
				.isThrownBy(() -> userService.getUsersByUsernameLike(null));
	}

	@Test
	public void testInsertNewUser_setsIdToNull_returnsSavedUser() {
		User toSave = spy(new User(99L, "toSaveUsername", "toSavePwd"));
		User saved = new User(1L, "savedUsername", "savedPwd");

		when(userRepository.save(any(User.class))).thenReturn(saved);

		User result = userService.insertNewUser(toSave);

		assertThat(result).isEqualTo(saved);
		InOrder inOrder = inOrder(toSave, userRepository);
		inOrder.verify(toSave).setId(null);
		inOrder.verify(userRepository).save(toSave);
	}

	@Test
	public void testInsertNewUser_UserIsNull_ShouldThrowException() {
		assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> userService.insertNewUser(null));
		verifyNoMoreInteractions(userRepository);
	}

	@Test
	public void testUpdateUserById_setsIdToArgument_ShouldReturnSavedUser() throws Exception {
		User replacement = spy(new User(null, "replacement_user", "replacement_pwd"));
		User replaced = new User(1L, "replaced_user", "replaced_user");
		when(userRepository.save(any(User.class))).thenReturn(replaced);
		when(userRepository.findById(1L)).thenReturn(Optional.of(replaced));

		User result = userService.updateUserById(1L, replacement);

		assertThat(result).isEqualTo(replaced);
		InOrder inOrder = inOrder(replacement, userRepository);
		inOrder.verify(replacement).setId(1L);
		inOrder.verify(userRepository).save(replacement);
	}

	@Test
	public void testUpdateUserById_UserIsNull_ShouldThrowException() {
		assertThatExceptionOfType(IllegalArgumentException.class)
				.isThrownBy(() -> userService.updateUserById(1L, null));
		verifyNoMoreInteractions(userRepository);
	}

	@Test
	public void testUpdateUserById_IdNotFound_ShouldThrowException() {
		User user = new User(1L, "username", "pwd");
		when(userRepository.findById(user.getId())).thenReturn(Optional.empty());
		assertThatExceptionOfType(UserNotFoundException.class)
				.isThrownBy(() -> userService.updateUserById(user.getId(), user));
	}

	@Test
	public void testUpdateUserById_IdIsNull_ShouldThrowException() {
		User user = new User(1L, "username", "pwd");
		assertThatExceptionOfType(IllegalArgumentException.class)
				.isThrownBy(() -> userService.updateUserById(null, user));
		verifyNoMoreInteractions(userRepository);
	}

	@Test
	public void testDeleteById_IdIsNull() {
		assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> userService.deleteById(null));
		verifyNoMoreInteractions(userRepository);
	}

	@Test
	public void testDeleteById_IdNotFound_ShouldThrowException() {
		when(userRepository.findById(1L)).thenReturn(Optional.empty());
		assertThatExceptionOfType(UserNotFoundException.class).isThrownBy(() -> userService.deleteById(1L));
		verifyNoMoreInteractions(ignoreStubs(userRepository));
	}

	@Test
	public void testDeleteById_IdFound() {
		User user = new User(1L, "username", "pwd");
		when(userRepository.findById(1L)).thenReturn(Optional.of(user));
		assertThatCode(() -> userService.deleteById(1L)).doesNotThrowAnyException();
		verify(userRepository, times(1)).deleteById(1L);
	}

	@Test
	public void testAddUserToFollowedUsersList_UserIsNull_ShouldThrowException() {
		User user = new User(1L, "username", "pwd");
		assertThatExceptionOfType(IllegalArgumentException.class)
				.isThrownBy(() -> userService.addFollowedUser(null, user));
	}

	@Test
	public void testAddUserToFollowedUsersList_ToAddUserIsNull_ShouldThrowException() {
		User user = new User(1L, "username", "pwd");
		assertThatExceptionOfType(IllegalArgumentException.class)
				.isThrownBy(() -> userService.addFollowedUser(user, null));
	}

	@Test
	public void testAddUserToFollowedUsersList_ShouldReturnModifiedUser() {
		User user1 = spy(new User(1L, "username", "pwd"));
		User user2 = spy(new User(2L, "username", "pwd"));
		User resulted = new User(1L, "username", "pwd");
		resulted.addFollowedUser(user2);
		when(userRepository.save(any(User.class))).thenReturn(resulted);

		User saved = userService.addFollowedUser(user1, user2);
		assertThat(saved).isEqualTo(resulted);
		InOrder inOrder = inOrder(user1, user2, userRepository);
		inOrder.verify(user1).addFollowedUser(user2);
		inOrder.verify(user2).addFollowerUser(user1);
		inOrder.verify(userRepository).save(user1);
	}

	@Test
	public void testAddGameToGamesList_UserIsNull_ShouldThrowException() {
		Game game = new Game(1L, "game name", "game description", new Date(1000));
		assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> userService.addGame(null, game));
	}

	@Test
	public void testAddGameToGamesList_GameIsNull_ShouldThrowException() {
		User user = new User(1L, "username", "pwd");
		assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> userService.addGame(user, null));
	}

	@Test
	public void testAddGameToGamesList_ShouldReturnModifiedUser() {
		User user = spy(new User(1L, "username", "pwd"));
		Game game = spy(new Game(2L, "game name", "game description", new Date(1000)));
		User resulted = new User(1L, "username", "pwd");
		resulted.addGame(game);
		when(userRepository.save(any(User.class))).thenReturn(resulted);

		User saved = userService.addGame(user, game);
		assertThat(saved).isEqualTo(resulted);
		InOrder inOrder = inOrder(user, game, userRepository);
		inOrder.verify(user).addGame(game);
		inOrder.verify(game).addUser(user);
		inOrder.verify(userRepository).save(user);
	}
}
