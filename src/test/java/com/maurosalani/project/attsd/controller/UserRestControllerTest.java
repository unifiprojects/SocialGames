package com.maurosalani.project.attsd.controller;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.context.support.StaticApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import com.maurosalani.project.attsd.exception.UserNotFoundException;
import com.maurosalani.project.attsd.exception_handler.GlobalExceptionHandler;
import com.maurosalani.project.attsd.model.User;
import com.maurosalani.project.attsd.service.UserService;

import io.restassured.module.mockmvc.RestAssuredMockMvc;

@RunWith(MockitoJUnitRunner.class)
public class UserRestControllerTest {

	@InjectMocks
	private UserRestController userRestController;

	@Mock
	private UserService userService;

	@Before
	public void setup() {
		HandlerExceptionResolver handlerExceptionResolver = initGlobalExceptionHandlerResolvers();

		RestAssuredMockMvc.standaloneSetup(MockMvcBuilders.standaloneSetup(userRestController)
				.setHandlerExceptionResolvers(handlerExceptionResolver));
	}

	/**
	 * Necessary to register the exception handler for these unit tests
	 * @return
	 */
	private HandlerExceptionResolver initGlobalExceptionHandlerResolvers() {
		StaticApplicationContext applicationContext = new StaticApplicationContext();
		applicationContext.registerSingleton("exceptionHandler", GlobalExceptionHandler.class);

		WebMvcConfigurationSupport webMvcConfigurationSupport = new WebMvcConfigurationSupport();
		webMvcConfigurationSupport.setApplicationContext(applicationContext);

		return webMvcConfigurationSupport.handlerExceptionResolver();
	}

	@Test
	public void testFindAllUsersWithEmptyDatabase() {
		when(userService.getAllUsers()).thenReturn(Collections.emptyList());

		given().
		when().
			get("/api/users").
		then().
			statusCode(200).
			assertThat().
				body(is(equalTo("[]")));
	}

	@Test
	public void testFindAllUsersWithExistingUsers() {
		User user1 = new User(1L, "user1", "pwd1");
		User user2 = new User(2L, "user2", "pwd2");
		when(userService.getAllUsers()).thenReturn(asList(user1, user2));

		given().
		when().
			get("/api/users").
		then().
			statusCode(200).
			contentType(MediaType.APPLICATION_JSON_UTF8_VALUE).
			assertThat().
			body("id[0]", equalTo(1), 
				"username[0]", equalTo("user1"), 
				"password[0]", equalTo("pwd1"),
				"id[1]", equalTo(2), 
				"username[1]", equalTo("user2"), 
				"password[1]", equalTo("pwd2"));

	}

	@Test
	public void testFindUserByIdWhenNotFound() throws Exception {
		when(userService.getUserById(anyLong())).thenThrow(UserNotFoundException.class);

		given().
		when().
			get("/api/users/id/1").
		then().	
			statusCode(404).
			statusLine(containsString("User Not Found"));
	}
	
	@Test
	public void testFindUserByIdWithExistingUser() throws Exception {
		when(userService.getUserById(anyLong())).thenReturn(new User(1L, "username", "pwd"));
		
		given().
		when().
			get("/api/users/id/1").
		then().	
			statusCode(200).
			body("id", equalTo(1), 
				"username", equalTo("username"), 
				"password", equalTo("pwd"));
	}
	
	@Test
	public void testFindUserByUsernameWhenNotFound() throws Exception {
		when(userService.getUserByUsername(anyString())).thenThrow(UserNotFoundException.class);
		
		given().
		when().
			get("/api/users/username/testName").
		then().	
			statusCode(404).
			statusLine(containsString("User Not Found"));
	}
	
	@Test
	public void testFindUserByUsernameWithExistingUser() throws Exception {
		when(userService.getUserByUsername(anyString())).thenReturn(new User(1L, "testName", "pwd"));
		
		given().
		when().
			get("/api/users/username/testName").
		then().	
			statusCode(200).
			body("id", equalTo(1), 
				"username", equalTo("testName"), 
				"password", equalTo("pwd"));
	}
	
	@Test
	public void testGetUsersByUsernameLikeWhenNotFound()  {
		when(userService.getUsersByUsernameLike("user")).thenReturn(Collections.emptyList());
		
		given().
		when().
			get("/api/users/usernamelike/user").
		then().
			statusCode(200).
			contentType(MediaType.APPLICATION_JSON_UTF8_VALUE).
		assertThat().
			body(is(equalTo("[]")));
	}
	
	@Test
	public void testGetUsersByUsernameLikeWithExistingUsers()  {
	    User user1 = new User(1L, "user1", "pwd1");
	    User user2 = new User(2L, "user2", "pwd2");
	    
	    when(userService.getUsersByUsernameLike("user")).thenReturn(asList(user1,user2));
	    
	    given().
	    when().
	      	get("/api/users/usernamelike/user").
	    then().
	      	statusCode(200).
	      	contentType(MediaType.APPLICATION_JSON_UTF8_VALUE).
	    assertThat().
	      	body(
		        "id[0]", equalTo(1),
		        "username[0]", equalTo("user1"),
		        "password[0]", equalTo("pwd1"),
		        "id[1]", equalTo(2),
		        "username[1]", equalTo("user2"),
		        "password[1]", equalTo("pwd2"));
	}
	
	@Test
	public void testGetUserByIdWithEmptyId()  {		
		given().
		when().
			get("/api/users/id").
		then().	
			statusCode(400).
			statusLine(containsString("Bad Request"));
	}
	
	@Test
	public void testGetUserByUsernameWithEmptyUsername()  {		
		given().
		when().
			get("/api/users/username").
		then().	
			statusCode(400).
			statusLine(containsString("Bad Request"));
	}
	
	@Test
	public void testGetUsersByUsernameLikeWithEmptyUsername()  {		
		given().
		when().
			get("/api/users/usernamelike").
		then().	
			statusCode(400).
			statusLine(containsString("Bad Request"));
	}
	
	@Test
	public void testPost_InsertNewUser() {
		User requestBodyUser = new User(null, "testUsername", "pwd");
		when(userService.insertNewUser(requestBodyUser)).
			thenReturn(new User(1L, "testUsername", "pwd"));

		given().
			contentType(MediaType.APPLICATION_JSON_VALUE).
			body(requestBodyUser).
		when().
			post("/api/users/new").
		then().
			statusCode(200).
			body(
				"id", equalTo(1),
				"username", equalTo("testUsername"),
				"password", equalTo("pwd"));
	}
	
	@Test
	public void testPut_UpdatePasswordOfUser() throws Exception {
		User requestBodyUser = new User(null, "testUsername", "new_password");
		when(userService.updateUserById(1L, requestBodyUser)).
			thenReturn(new User(1L, "testUsername", "new_password"));

		given().
			contentType(MediaType.APPLICATION_JSON_VALUE).
			body(requestBodyUser).
		when().
			put("/api/users/update/1").
		then().
			statusCode(200).
			body(
				"id", equalTo(1),
				"username", equalTo("testUsername"),
				"password", equalTo("new_password"));
	}
	
}