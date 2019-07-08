package com.maurosalani.project.attsd.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.maurosalani.project.attsd.exception.UserNotFoundException;
import com.maurosalani.project.attsd.model.User;

@Service
public class UserService {
	private static final String TEMPORARY_IMPLEMENTATION = "Temporary implementation";

	public List<User> getAllUsers() {
		throw new UnsupportedOperationException(TEMPORARY_IMPLEMENTATION);
	}

	public User getUserById(Long id) throws UserNotFoundException{
		throw new UnsupportedOperationException(TEMPORARY_IMPLEMENTATION);
	}

}
