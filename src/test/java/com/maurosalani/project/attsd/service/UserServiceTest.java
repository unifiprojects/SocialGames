package com.maurosalani.project.attsd.service;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.maurosalani.project.attsd.model.User;
import com.maurosalani.project.attsd.repository.UserRepository;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest {

	@Mock
	private UserRepository userRepository;

	@InjectMocks
	private UserService userService;

	@Test
	public void testFindAllUsersWhenDatabaseIsEmpty() {
		when(userRepository.findAllUsers()).thenReturn(Collections.emptyList());
		assertThat(userService.getAllUsers()).isEmpty();
	}

	@Test
	public void testFindAllUsersWhitExistingUsers() {
		User user1 = new User(1l, "username1", "pwd1");
		User user2 = new User(2l, "username2", "pwd2");
		when(userRepository.findAllUsers()).thenReturn(asList(user1, user2));
		assertThat(userService.getAllUsers()).containsExactly(user1, user2);
	}

	@Test
	public void testGetUserByIdWhenUserDoesNotExist() {
		when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
		assertThat(userService.getUserById(1L)).isNull();
	}

	@Test
	public void testGetUserByIdWhitExistingUser() {
		User user = new User(1L, "username", "pwd");
		when(userRepository.findById(1L)).thenReturn(Optional.of(user));
		assertThat(userService.getUserById(1L)).isEqualTo(user);
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
}
