package com.game.trivia.util;

import com.game.trivia.repository.model.Choices;
import com.game.trivia.repository.model.QuestionBank;

import java.util.*;

public class QuestionGenerator {
    Random r = new Random();

    public QuestionBank generate(int upperBound, int lowerBound, int totalChoices) {

        //Generate 2 numbers to add
        int num1 = r.nextInt(upperBound - lowerBound) + lowerBound;
        int num2 = r.nextInt(upperBound - lowerBound) + lowerBound;
        int res = num1 + num2;

        //Assign correct choice to random option
        int option = r.nextInt(totalChoices) + 1;
        List<Choices> choicesList = new ArrayList<>();
        Set<Integer> generateChoices = new HashSet<>();
        generateChoices.add(res);
        for (int i = 1; i <= 4; i++) {
            int num = r.nextInt(upperBound + lowerBound);
            while (generateChoices.contains(num)) {
                num = r.nextInt(upperBound + lowerBound);
            }
            if (i == option) {
                choicesList.add(new Choices(i, "Is it " + res + " ?"));
            } else {
                choicesList.add(new Choices(i, "Is it " + num + " ?"));
                generateChoices.add(num);
            }
        }

        //Prepare question
        QuestionBank questionBank = new QuestionBank();
        questionBank.setQuestion("What is " + num1 + "+" + num2 + "?");
        questionBank.setChoices(choicesList);
        questionBank.setCorrectChoice(option);
        return questionBank;
    }


}
