package com.maurosalani.project.attsd;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.sql.Date;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.maurosalani.project.attsd.model.Game;
import com.maurosalani.project.attsd.repository.GameRepository;
import com.maurosalani.project.attsd.service.GameService;

@RunWith(SpringRunner.class)
@DataJpaTest
@Import(GameService.class)
@AutoConfigureTestDatabase(replace = Replace.NONE)
@ActiveProfiles("mysql")
public class GameServiceRepositoryIT {

	@Autowired
	private GameService gameService;

	@Autowired
	private GameRepository gameRepository;

	@Before
	public void setup() {
		gameRepository.deleteAll();
		gameRepository.flush();
	}

	@Test
	public void testServiceCanInsertIntoRepository() throws Exception {
		Game saved = gameService.insertNewGame(new Game(null, "gameToInsert", "description", new Date(1000)));
		assertTrue(gameRepository.findById(saved.getId()).isPresent());
	}

	@Test
	public void testServiceCanDeleteFromRepository() throws Exception {
		Game saved = gameService.insertNewGame(new Game(null, "gameToDelete", "description", new Date(1000)));
		assertTrue(gameRepository.findById(saved.getId()).isPresent());
		gameService.deleteById(saved.getId());
		assertFalse(gameRepository.findById(saved.getId()).isPresent());
	}

	@Test
	public void testServiceCanUpdateIntoRepository() throws Exception {
		Game gameToUpdate = new Game(null, "gameToUpdate", "description", new Date(1000));
		gameRepository.save(gameToUpdate);
		Game gameResulted = gameService.updateGameById(gameToUpdate.getId(),
				new Game(gameToUpdate.getId(), "newGame", "newDescription", new Date(1001)));

		assertThat(gameRepository.findById(gameToUpdate.getId()).get()).isEqualTo(gameResulted);
	}

}
