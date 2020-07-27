package com.game.trivia.util;

import com.game.trivia.repository.model.Choices;
import com.game.trivia.repository.model.QuestionBank;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class QuestionGenerator {
    Random r = new Random();

    private QuestionBank generate(int upperBound, int lowerBound, int totalChoices) {
        int num1 = r.nextInt(upperBound-lowerBound)+lowerBound;
        int num2 = r.nextInt(upperBound-lowerBound)+lowerBound;
        int res = num1 + num2;

        //Assign correct choice to random option
        int option = r.nextInt(totalChoices)+1;
        List<Choices> choicesList = new ArrayList<>();
        for (int i = 1; i <= 4; i++) {
            if (i == option) {
                choicesList.add(new Choices(i, res + ""));
            } else {
                choicesList.add(new Choices(i, r.nextInt(upperBound + lowerBound) + ""));
            }
        }

        QuestionBank questionBank = new QuestionBank();
        questionBank.setQuestion("What is " + num1 + "+" + num2 + "?");
        questionBank.setChoices(choicesList);
        questionBank.setCorrectChoice(option);
        return questionBank;
    }


}
