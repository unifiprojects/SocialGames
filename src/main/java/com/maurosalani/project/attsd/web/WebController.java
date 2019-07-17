package com.maurosalani.project.attsd.web;

import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.maurosalani.project.attsd.dto.ChangePasswordForm;
import com.maurosalani.project.attsd.dto.Credentials;
import com.maurosalani.project.attsd.dto.RegistrationForm;
import com.maurosalani.project.attsd.exception.GameNotFoundException;
import com.maurosalani.project.attsd.exception.LoginFailedException;
import com.maurosalani.project.attsd.exception.NewPasswordRequiredException;
import com.maurosalani.project.attsd.exception.OldPasswordErrorException;
import com.maurosalani.project.attsd.exception.PasswordRequiredException;
import com.maurosalani.project.attsd.exception.PasswordsRegistrationDoNotMatchException;
import com.maurosalani.project.attsd.exception.UnauthorizedOperationException;
import com.maurosalani.project.attsd.exception.UserNotFoundException;
import com.maurosalani.project.attsd.exception.UsernameAlreadyExistingException;
import com.maurosalani.project.attsd.exception.UsernameRequiredException;
import com.maurosalani.project.attsd.model.Game;
import com.maurosalani.project.attsd.model.User;
import com.maurosalani.project.attsd.service.GameService;
import com.maurosalani.project.attsd.service.UserService;

@Controller
public class WebController {

	private static final String GAMES_LIST = "gamesList";

	private static final String USERS_LIST = "usersList";

	private static final String MESSAGE = "message";

	private static final String DISABLE_INPUT_FLAG = "disableInput";

	private static final int COUNT_LATEST_RELEASES = 4;

	@Autowired
	private UserService userService;

	@Autowired
	private GameService gameService;

	@GetMapping("/")
	public String index(Model model, HttpSession session) {
		if (isAlreadyLogged(session)) {
			User user = (User) session.getAttribute("user");
			model.addAttribute("username", user.getUsername());
		}
		model.addAttribute("latestReleases", gameService.getLatestReleasesGames(COUNT_LATEST_RELEASES));
		return "index";
	}

	@GetMapping("/login")
	public String login(Model model, HttpSession session) {
		if (isAlreadyLogged(session)) {
			model.addAttribute(MESSAGE, "You are already logged! Try to log out from homepage.");
			model.addAttribute(DISABLE_INPUT_FLAG, true);
		} else {
			model.addAttribute(MESSAGE, "");
			model.addAttribute(DISABLE_INPUT_FLAG, false);
		}
		model.addAttribute("credentials", new Credentials());
		return "login";
	}

	@PostMapping(value = "/verifyLogin")
	public String verifyLoginUser(Model model, Credentials credentials, HttpSession session)
			throws LoginFailedException {
		User result = userService.verifyLogin(credentials);
		session.setAttribute("user", result);
		return "redirect:/";
	}

	@GetMapping("/logout")
	public String logout(HttpSession session) {
		if (isAlreadyLogged(session))
			session.invalidate();
		return "redirect:/";
	}

	@GetMapping("/registration")
	public String registration(Model model, HttpSession session) {
		if (isAlreadyLogged(session)) {
			model.addAttribute(MESSAGE, "You are already logged! Try to log out from homepage.");
			model.addAttribute(DISABLE_INPUT_FLAG, true);
		} else {
			model.addAttribute(MESSAGE, "");
			model.addAttribute(DISABLE_INPUT_FLAG, false);
		}
		model.addAttribute("registrationForm", new RegistrationForm());
		return "registration";
	}

	@PostMapping("/save")
	public String save(Model model, HttpSession session, RegistrationForm form) throws UsernameAlreadyExistingException,
			UsernameRequiredException, PasswordRequiredException, PasswordsRegistrationDoNotMatchException {

		if (form.isValid()) {
			User userSaved = userService.insertNewUser(new User(null, form.getUsername(), form.getPassword()));
			model.addAttribute("user", userSaved);
		}
		return "registrationSuccess";
	}

	@GetMapping("/search")
	public String search(Model model, HttpSession session,
			@RequestParam(value = "content_search", required = true) String content) {
		if (contentIsNotValid(content)) {
			model.addAttribute(MESSAGE, "Error: search field was empty.");
		} else {
			model.addAttribute(MESSAGE, "");
			String trimmedContent = content.trim();
			List<User> usersFound = userService.getUsersByUsernameLike(trimmedContent);
			List<Game> gamesFound = gameService.getGamesByNameLike(trimmedContent);
			model.addAttribute(USERS_LIST, usersFound);
			model.addAttribute(GAMES_LIST, gamesFound);
		}
		return "search";
	}

	private boolean contentIsNotValid(String content) {
		return StringUtils.isBlank(content);
	}

	@GetMapping("/profile/{username}")
	public String profile(@PathVariable String username, Model model, HttpSession session)
			throws UserNotFoundException {
		User user = userService.getUserByUsername(username);
		model.addAttribute("user", user);
		if (!isAlreadyLogged(session)) {
			model.addAttribute("isLogged", false);
			model.addAttribute("isMyProfile", false);
			model.addAttribute("isAlreadyFollowed", false);
		} else {
			User loggedUser = (User) session.getAttribute("user");
			boolean isMyProfile = loggedUser.getUsername().equals(user.getUsername());
			boolean isAlreadyFollowed = false;
			if (loggedUser.getFollowedUsers() != null)
				isAlreadyFollowed = loggedUser.getFollowedUsers().contains(user);
			model.addAttribute("isLogged", true);
			model.addAttribute("isMyProfile", isMyProfile);
			model.addAttribute("isAlreadyFollowed", isAlreadyFollowed);
			if(isMyProfile)
				model.addAttribute("changePasswordForm", new ChangePasswordForm());
		}
		return "profile";
	}

	@GetMapping("/game/{name}")
	public String game(@PathVariable String name, Model model) throws GameNotFoundException {
		Game game = gameService.getGameByName(name);
		model.addAttribute("game", game);
		return "game";
	}

	@PostMapping("/addUser")
	public String addFollowedUserToUser(@RequestParam(name = "followedToAdd") String followedToAdd, Model model,
			HttpSession session) throws UserNotFoundException, UnauthorizedOperationException {
		if (!isAlreadyLogged(session)) {
			throw new UnauthorizedOperationException();
		}
		User user = (User) session.getAttribute("user");
		User followed = userService.getUserByUsername(followedToAdd);
		User result = userService.addFollowedUser(user, followed);
		session.setAttribute("user", result);
		return "redirect:/profile/" + followed.getUsername();
	}

	@PostMapping("/addGame")
	public String addGameToUser(@RequestParam(name = "gameToAdd") String gameToAdd, Model model, HttpSession session)
			throws GameNotFoundException, UnauthorizedOperationException {
		if (!isAlreadyLogged(session)) {
			throw new UnauthorizedOperationException();
		}
		User user = (User) session.getAttribute("user");
		Game toAdd = gameService.getGameByName(gameToAdd);
		User result = userService.addGame(user, toAdd);
		session.setAttribute("user", result);
		return "redirect:/game/" + toAdd.getName();
	}

	@PostMapping("/changePassword")
	public String changePassword(ChangePasswordForm form, Model model, HttpSession session)
			throws UnauthorizedOperationException, OldPasswordErrorException, NewPasswordRequiredException {
		if (!isAlreadyLogged(session)) {
			throw new UnauthorizedOperationException();
		}
		User user = (User) session.getAttribute("user");
		if (!user.getPassword().equals(form.getOldPassword())) {
			throw new OldPasswordErrorException();
		}
		if (StringUtils.isWhitespace(form.getNewPassword())) {
			throw new NewPasswordRequiredException();
		}
		User result = userService.changePassword(user, form.getNewPassword());
		session.setAttribute("user", result);
		return "passwordChanged";
	}

	private boolean isAlreadyLogged(HttpSession session) {
		Optional<User> opt = Optional.ofNullable((User) session.getAttribute("user"));
		return opt.isPresent();
	}

}