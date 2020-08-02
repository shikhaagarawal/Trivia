package com.game.trivia;

import com.game.trivia.controller.GameController;
import com.game.trivia.repository.model.GameInstance;
import com.game.trivia.repository.model.Player;
import com.game.trivia.repository.model.QuestionBank;
import com.game.trivia.service.GameInstanceService;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

public class GameControllerTest {

    @Mock
    GameInstanceService gameInstanceServiceMock;

    @Mock
    SimpMessagingTemplate templateMock;

    QuestionBank qb = new QuestionBank();

    @Test
    public void broadcast_quiz_template_never_called_NoPlayers() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        MockitoAnnotations.initMocks(this);
        //Set data
        GameInstance game = new GameInstance();
        Player p = new Player();
        p.setPlaying(false);
        game.setPlayers(Arrays.asList(p));

        Mockito.when(gameInstanceServiceMock.findGame(2)).thenReturn(game);
        GameController controller = new GameController();
        controller.gameInstanceService = gameInstanceServiceMock;
        Method privateMethod = GameController.class.getDeclaredMethod("broadcastQuiz", long.class, QuestionBank.class);
        privateMethod.setAccessible(true);
        privateMethod.invoke(controller, 2, qb);
        Mockito.verify(templateMock, Mockito.never()).convertAndSendToUser(null, "/queue/play/game", qb);

    }

    @Test
    public void broadcast_quiz_to_players() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        MockitoAnnotations.initMocks(this);
        //Set data
        GameInstance game = new GameInstance();
        Player p = new Player();
        p.setPlaying(true);
        game.setPlayers(Arrays.asList(p));

        Mockito.when(gameInstanceServiceMock.findGame(2)).thenReturn(game);

        GameController controller = new GameController();
        controller.gameInstanceService = gameInstanceServiceMock;
        controller.template = templateMock;

        Method privateMethod = GameController.class.getDeclaredMethod("broadcastQuiz", long.class, QuestionBank.class);
        privateMethod.setAccessible(true);
        privateMethod.invoke(controller, 2, qb);
        Mockito.verify(templateMock).convertAndSendToUser(null, "/queue/play/game", qb);
    }
}


