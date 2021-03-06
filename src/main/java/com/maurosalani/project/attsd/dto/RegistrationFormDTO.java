package com.maurosalani.project.attsd.dto;

import org.apache.commons.lang3.StringUtils;

import com.maurosalani.project.attsd.exception.PasswordRequiredException;
import com.maurosalani.project.attsd.exception.PasswordsRegistrationDoNotMatchException;
import com.maurosalani.project.attsd.exception.UsernameRequiredException;

public class RegistrationFormDTO {

	private String username;
	private String password;
	private String confirmPassword;

	public RegistrationFormDTO() {
	}

	public RegistrationFormDTO(String username, String password, String confirmPassword) {
		super();
		this.username = username;
		this.password = password;
		this.confirmPassword = confirmPassword;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getConfirmPassword() {
		return confirmPassword;
	}

	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}

	public void checkValidity()
			throws UsernameRequiredException, PasswordRequiredException, PasswordsRegistrationDoNotMatchException {
		if (username == null || StringUtils.isWhitespace(username)) {
			throw new UsernameRequiredException();
		}
		if (password == null || confirmPassword == null || StringUtils.isWhitespace(password)
				|| StringUtils.isWhitespace(confirmPassword)) {
			throw new PasswordRequiredException();
		}
		if (!password.equals(confirmPassword)) {
			throw new PasswordsRegistrationDoNotMatchException();
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((password == null) ? 0 : password.hashCode());
		result = prime * result + ((confirmPassword == null) ? 0 : confirmPassword.hashCode());
		result = prime * result + ((username == null) ? 0 : username.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (this == obj)
			return true;
		if (getClass() != obj.getClass())
			return false;
		RegistrationFormDTO other = (RegistrationFormDTO) obj;
		if (password == null) {
			if (other.password != null)
				return false;
		} 
		else if (!password.equals(other.password))
			return false;
		if (confirmPassword == null) {
			if (other.confirmPassword != null)
				return false;
		} 
		else if (!confirmPassword.equals(other.confirmPassword))
			return false;
		if (username == null) {
			if (other.username != null)
				return false;
		} 
		else if (!username.equals(other.username))
			return false;
		return true;
	}

}
