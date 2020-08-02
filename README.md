# Trivia

It is a multiplayer online game in which players have to quickly answer random problems.
The game consist of multiple levels and as the player make right choice to the question, they advance to the next level.

## How to play
- Navigate to the Home Page via https://trivia-sa.herokuapp.com
- Click on the link that redirects to https://trivia-game-sa.herokuapp.com
- Enter your name and click start game. "Waiting for other players..." msg will be shown.
- Ask a friend or on a different window, perform above steps.
- Once minimum players joins the game, 10 second countdown will start.
- Quickly answer random math addition question in order to win the game.

## Requirements
- The application should run in completely automatic mode - i.e no admin intervention should be required to start the game (once a
minimum number of players have joined) or advance the game to the next round.
- The application should allow running multiple games simultaneously.
- The application should allow a reasonably large number of players to participate in each game (think hundreds).
- The application should display statistics about player choices at the end of each round (how many players have chosen each answer).

## Game Flow
- Game starts, once minimum number of player have joined a game session. For demo purposes, minimum number of player is configured to 2.
- Any new player, who joins in the waiting time before the 1st question is diplayed also get to play in the same game session.
- Any player who joins after an existing game has started, will have to wait for the next game to begin.
- Question will appear on player's screen. Once an answer is chosen, all other choices gets disabled.
- 15 seconds will be given to make a choice of a right answer. If, a player makes no choice within 15 sec, they get disqualified.
- Stats are displayed for 10 seconds at the end of each question.

### Finding winner
- A game session can go upto N levels. For demo purposes, the number of levels is set to 5.
- In case no player makes to the final level, then the game declares the last standing player, with a correct answer, as the winner.
- In case multiple players submits correct answer in the last round, then the player answering the question in the minimum amount of time is declared as the winner. 
- In case none of the player submits a correct answer at any given level then no winner is found, and the game session will end.
 
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


### Screenshots
Players join the game
![Alt text](src/main/resources/static/images/PlayersName.png?raw=true "Players join game")

Minimum players have joined and game is about to begin
![Alt text](src/main/resources/static/images/GameBeginCountdown.png?raw=true "Game Countdown")

Random Question at each level
![Alt text](src/main/resources/static/images/RandomQuestion.png?raw=true "Random Question")

Stats after each level
![Alt text](src/main/resources/static/images/Stats.png?raw=true "Stats")

Winner and Looser messages
![Alt text](src/main/resources/static/images/LastPage.png?raw=true "Title")

## Known Behavior
- Game boot time is slow due to server hosting environment.

## Tech Stack
Tiered Architecture:
- Web: Angular, websockets (https://github.com/shikhaagarawal/Trivia-UI)
- Server: Springboot, websockets (https://github.com/shikhaagarawal/Trivia)
- Database: MongoDB 
- Hosting : Heroku

## Database

DB Name: trivia_db

Collection Names: GameInstance, QuestionBank

Indexing: GameInstance(GameId), QuestionBank(active)

###### QuestionBank document:
 
`{
_id:ObjectId,
question: String,
choice:[choice: number, text: String],level:number,correctChoice:number,active:boolean
}`

###### GameInstance document:
 
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
- API to add questions.
           
      POST - https://trivia-sa.herokuapp.com/add/questions/{level}/{rangeFrom}/{rangeTo}
      
- API to restore questions from archive.
           
      POST - https://trivia-sa.herokuapp.com/add/questions/{level}/{rangeFrom}/{rangeTo}
