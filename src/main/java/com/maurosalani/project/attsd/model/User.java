package com.maurosalani.project.attsd.model;

import java.util.Arrays;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

import org.hibernate.validator.constraints.Length;

@Entity
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Length(max = 40)
	@Column(unique = true)
	@Basic(optional = false)
	private String username;

	@Basic(optional = false)
	private String password;

	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "followers_relation", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "followed_id"))
	private List<User> followedUsers;

	@ManyToMany(mappedBy = "followedUsers", cascade = CascadeType.ALL)
	private List<User> followerUsers;

	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "user_game_relation", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "game_id"))
	private List<Game> games;

	public User() {

	}

	public User(Long id, String username, String password) {
		this.id = id;
		this.username = username;
		this.password = password;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public List<User> getFollowedUsers() {
		return followedUsers;
	}
		
	public void setFollowedUsers(List<User> followedUsers) {
		this.followedUsers = followedUsers;
	}
	
	public List<User> getFollowerUsers() {
		return followerUsers;
	}
		
	public void setFollowerUsers(List<User> followerUsers) {
		this.followerUsers = followerUsers;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((password == null) ? 0 : password.hashCode());
		result = prime * result + ((username == null) ? 0 : username.hashCode());
		return result;
	}

	public List<Game> getGames() {
		return games;
	}

	public void setGames(List<Game> games) {
		this.games = games;
	}

	public void addFollowedUser(User followedUser) {
		if (followedUser != null) {
			if (this.followedUsers == null)
				this.setFollowedUsers(Arrays.asList(followedUser));
			else
				this.followedUsers.add(followedUser);
		}
	}

	public void addFollowerUser(User followerUser) {
		if (followerUser != null) {
			if (this.followerUsers == null)
				this.setFollowerUsers(Arrays.asList(followerUser));
			else
				this.followerUsers.add(followerUser);
		}
	}

	public void addGame(Game game) {
		if (game != null) {
			if (this.games == null)
				this.setGames(Arrays.asList(game));
			else
				this.games.add(game);
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (password == null) {
			if (other.password != null)
				return false;
		} else if (!password.equals(other.password))
			return false;
		if (username == null) {
			if (other.username != null)
				return false;
		} else if (!username.equals(other.username))
			return false;
		return true;
	}

	@Override
	public String toString() {
		String followedUserString;
		String followerUserString;
		String gamesString;
		if (followedUsers == null || followedUsers.isEmpty()) {
			followedUserString = "None";
		} else {
			followedUserString = followedUsers.stream().map(User::toStringReducedInfo).reduce("", String::concat);
		}
		if (followerUsers == null || followerUsers.isEmpty()) {
			followerUserString = "None";
		} else {
			followerUserString = followerUsers.stream().map(User::toStringReducedInfo).reduce("", String::concat);
		}
		if (games == null || games.isEmpty()) {
			gamesString = "None";
		} else {
			gamesString = games.toString();
		}
		return "\nUser [id=" + id + ", username=" + username + ", password=" + password + ", \n\tfollowed="
				+ followedUserString + ", \n\tfollower=" + followerUserString + ", \n\tgames=" + gamesString + "]";
	}

	private String toStringReducedInfo() {
		return "User [id=" + id + ", username=" + username + "]";
	}

}
