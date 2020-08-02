# Trivia

It is a fun trivia game in which players have to answer random additional problems. 
Players can play in real time with their friends.

##How to play
Trivia is hosted on heroku server which sleeps automatically after 30 mins of inactivity.
Therefore, prerequisite is to click https://trivia-sa.herokuapp.com before starting a new game every 30 mins; if not done game could behave differently.
Wait until message "I am awake now. Play Game" appears on the screen. Alright, lets begin.

- Click https://trivia-game-sa.herokuapp.com
- Enter your name and click start game. "Waiting for other players..." msg will be shown.
- Ask a friend or on a different window, perform above steps.
- Once minimum players joins the game, 10 second countdown will start.
- Quickly answer random math addition question to win the game.


Players join the game: 
![Alt text](src/main/resources/static/images/PlayersName.png?raw=true "Players join game")

Minimum players have joined and game is about to begin:
![Alt text](src/main/resources/static/images/GameBeginCountdown.png?raw=true "Game Countdown")

Random Question at each level:
![Alt text](src/main/resources/static/images/RandomQuestion.png?raw=true "Random Question")

Stats after each level:
![Alt text](src/main/resources/static/images/Stats.png?raw=true "Stats")

Winner and Looser messages:
![Alt text](src/main/resources/static/images/LastPage.png?raw=true "Title")

##Business Requirements
- The application should run in completely automatic mode - i.e no admin intervention should be required to start the game (once a
minimum number of players have joined) or advance the game to the next round.
- The application should allow running multiple games simultaneously.
- The application should allow a reasonably large number of players to participate in each game (think hundreds).
- The application should display statistics about player choices at the end of each round (how many players have chosen each answer).

## Game Flow

- Question will appear on screen. Once answer is selected, all other options gets disabled.
- 15 seconds will be given to choose a answer.
- After 15 seconds wait time, stats will be shown.
- On stats screen, wait for 10 seconds before 
    - Next question appear on screen, Or
    - Show winner page, Or
    - Show thank you page.

#### Multiple Games flow
- Once minimum players(say P1 & P2) join the game(say GAME_1), game countdown(10s) will start.
- All others players(say P3 & P4) joining within running 10s countdown will join GAME_1.
- GAME_1 will start quiz, say level1.
- Other player(say P5) wants to play the game then he will be added in new game(say GAME_2) and wait for other players to join.
- Other player(say P6) start the game, he will be added in GAME_2 and game countdown(10s) will start.
- GAME_1 and GAME_2 will have their own players,questions and winners.

#### Finding winner
- 5 levels can be played in one game.
- Fastest finger player in last round wins the game.
- If, only one player selects correct answer at any given level then he will be declared winner and game ends.
- If, none of the player selected correct answer at any given level then no winner is found such that game will end.
 
###### Scenario 1:  Fastest finger player

Game_1 | Level1 | Level2 | Level3 | Level4 | Level5 | Time_Taken | Result 
--- | --- | --- | --- |--- |--- |--- |--- 
Player1 | correct | correct | correct | correct | correct | 3200ms | Looser | 
Player2 | correct | correct | correct | correct | correct | 1200ms | Winner | 
Player3 | correct | correct | wrong | Looser | - | - | - | 
Player4 | correct | correct | correct | correct | correct | 2200ms | Looser | 

###### Scenario 2 : Only one player selects correct answer at any given level

Game_1 | Level1 | Level2 | Level3 | Result 
--- | --- | --- | --- |---
Player1 | correct | correct |  correct  | Winner  
Player2 | correct | correct |  wrong  |  Looser 
Player3 | correct | correct |  wrong  | Looser   
Player4 | correct | correct |  wrong  |  Looser

###### Scenario 3: None player selects correct answer at any given level

Game_1 | Level1 | Level2 | Level3 | Result 
--- | --- | --- | --- |---
Player1 | correct | correct |  wrong  | Looser  
Player2 | correct | correct |  wrong  |  Looser 
Player3 | correct | correct |  wrong  | Looser   
Player4 | correct | correct |  timeOut  |  Looser

## Tech Stack
Tiered Architecture:
- Web: Angular, websockets (https://github.com/shikhaagarawal/Trivia-UI)
- Server: Springboot, websockets (https://github.com/shikhaagarawal/Trivia)
- Database: MongoDB 
- Hosting : Heroku

## Known Issues
- No validation on player name.
- On UI, timer sometimes skip count by 2 instead of 1.
- Prev questions stats does not go away.

## Database

DB Name: trivia_db

Collection Names: GameInstance, QuestionBank

Indexing: GameInstance(GameId), QuestionBank(active)

######QuestionBank document:
 
`{
_id:ObjectId,
question: String,
choice:[choice: number, text: String],level:number,correctChoice:number,active:boolean
}`

######GameInstance document:
 
`{
_id:ObjectId,
gameId: long,
status:String,
winner: String,
players:[],
questions:[],
level:int,
gameFinishedTime:time;
gameStartTime:time;
}`

## Useful API's
- API to populate question bank.
           
      POST - https://trivia-sa.herokuapp.com/add/questions/{level}/{rangeFrom}/{rangeTo}
      
- API to restore questions from a archive and mark them available. Used a lot during testing.
           
      POST - https://trivia-sa.herokuapp.com/add/questions/{level}/{rangeFrom}/{rangeTo}
