package com.game.trivia.repository.model;

public class Choices{
    private int choice;
    private String text;

    public Choices(int choice, String text){
        this.choice = choice;
        this.text = text;
    }

    public int getChoice() {
        return choice;
    }

    public void setChoice(int choice) {
        this.choice = choice;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
