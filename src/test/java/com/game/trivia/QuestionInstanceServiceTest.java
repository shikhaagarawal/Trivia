package com.game.trivia;

import com.game.trivia.repository.QuestionRepository;
import com.game.trivia.repository.model.QuestionBank;
import com.game.trivia.service.QuestionInstanceService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.mockito.Mockito.when;

public class QuestionInstanceServiceTest {

    @Mock
    private QuestionRepository questionRepositoryMock;
    QuestionBank q1 = new QuestionBank();
    QuestionBank q2 = new QuestionBank();
    QuestionBank q3 = new QuestionBank();
    QuestionBank q4 = new QuestionBank();

    private QuestionInstanceService qs;
    private List<QuestionBank> ques = Arrays.asList(q1, q2, q3, q4);

    //All questions should be returned randomly from database
    @Test
    public void testFetchQuestion() {
        MockitoAnnotations.initMocks(this);
        QuestionInstanceService qs = new QuestionInstanceService();
        qs.questionRepository = questionRepositoryMock;
        when(questionRepositoryMock.findByLevelAndActive(2, true))
                .thenReturn(ques);
        HashSet<QuestionBank> set = new HashSet<>();
        int count = 100;
        while (count > 0) {
            set.add(qs.fetchQuestion(2));
            count--;
        }
        assert set.size() == 4;
    }

    //For no matching criteria questions, exception should not be thrown
    @Test
    public void testNoErrorFetchQuestion() {
        MockitoAnnotations.initMocks(this);
        QuestionInstanceService qs = new QuestionInstanceService();
        qs.questionRepository = questionRepositoryMock;
        when(questionRepositoryMock.findByLevelAndActive(2, true))
                .thenReturn(new ArrayList<QuestionBank>());
        HashSet<QuestionBank> set = new HashSet<>();
        int count = 100;
        while (count > 0) {
            set.add(qs.fetchQuestion(2));
            count--;
        }
        assertNoException();
    }

    private void assertException() {
        Assertions.assertThatThrownBy(this::doNotThrowException).isInstanceOf(Exception.class);
    }

    private void assertNoException() {
        Assertions.assertThatThrownBy(() -> assertException()).isInstanceOf(AssertionError.class);
    }

    private void doNotThrowException() {
        //This method will never throw exception
    }
}
