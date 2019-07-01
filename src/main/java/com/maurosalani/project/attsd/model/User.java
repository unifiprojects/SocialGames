package com.maurosalani.project.attsd.model;

import java.util.List;
import java.util.Objects;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

@Entity
public class User {

	@Id
	@GeneratedValue
	private Long id;

	@Column(unique = true)
	@Basic(optional = false)
	private String username;

	@Basic(optional = false)
	private String password;

	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "followers_relation")
	@JoinColumn(name = "follower_id", referencedColumnName = "id")
	@JoinColumn(name = "followed_id", referencedColumnName = "id")
	private List<User> followedUsers;

	@ManyToMany(mappedBy = "followedUsers")
	private List<User> followerUsers;

	@OneToMany(cascade = CascadeType.ALL)
	private List<Game> games;

	public User() {

	}

	public User(Long id, String username, String password, List<User> followedUsers, List<User> followerUsers,
			List<Game> games) {
		this.id = id;
		this.username = username;
		this.password = password;
		this.followedUsers = followedUsers;
		this.followerUsers = followerUsers;
		this.games = games;
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

	public List<Game> getGames() {
		return games;
	}

	public void setGames(List<Game> games) {
		this.games = games;
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
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((password == null) ? 0 : password.hashCode());
		result = prime * result + ((username == null) ? 0 : username.hashCode());
		return result;
	}

	@Override
	public String toString() {
		String followedUserString;
		String followerUserString;
		String gamesString;
		if (followedUsers == null || followedUsers.isEmpty()) {
			followedUserString = "None";
		} else {
			followedUserString = followedUsers.stream().map(user -> user.toStringReducedInfo()).reduce("",
					String::concat);
		}
		if (followerUsers == null || followerUsers.isEmpty()) {
			followerUserString = "None";
		} else {
			followerUserString = followerUsers.stream().map(user -> user.toStringReducedInfo()).reduce("",
					String::concat);
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
