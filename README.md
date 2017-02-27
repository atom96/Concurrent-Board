Board - simple concurent program. </br>
</br>
Author: Arkadiusz Tomczak</br>
Version: 1.0</br>
Date: 8-12-2016</br>
Language:
 - Variable names: Polish
 - Comments: Polish

This program is a simple game. Player (each player can use another thread to make his move)
can put pawn on board and make it move into one of four directions. If someone tries to move a pawn into taken field, he will be asleep (as a thread) until the field will be empty.
Board detects deadlocks and throws suitable exception.</br></br>
The main goal of this application was to make Board with pawns usable for more than one thread which would move a pawn.
