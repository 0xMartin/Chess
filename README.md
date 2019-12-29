# Chess program
Chess program allows playing with some player or with AI (from external file). 
Program can record game and after that play record.

<img src="https://github.com/0xMartin/Chess/blob/master/img1.PNG" width=65%)>

## Menu
* Game
  * New game - start new game
  * Export game animation - export game animation of current played game
  * Undo
  * Redo
* Setting
  * Global - all seeting (player names, AI, color of board)
  * Default settings - set all settings on default
* Tools
  * Animation player - this tool can play animation of recorded game
  * AI info - show inforamtion about AI whitch is connected to program
* Help
  * About - information about program and author

## AI info
<img src="https://github.com/0xMartin/Chess/blob/master/img2.PNG" width=65%)>

## Animation player
This tool allows automatic playing of animation and step be step animating.
Animation speed cam be changed (default: 500 ms per on frame).

<img src="https://github.com/0xMartin/Chess/blob/master/img3.PNG" width=65%)>

# Chess AI

AI is programmed in java and use [Alpha–beta pruning](https://github.com/0xMartin/Chess/blob/master/src-ai/SEMPAI/src/sempai/Algorithm.java). The evaluator uses only the material score for the evaluation.
```java
private int minMax(int depth, int alpha, int beta, boolean isWhite) {
        if (depth == 0) {
            return this.evaluator.getScoreOfBoard();
        }
        ArrayList<Move> moves = this.moveManager.getPossibleMoves(isWhite);
        if (isWhite == Main.ISWHITE) {
            int best = -Main.MAX_VALUE;
            for (Move m : moves) {
                this.moveManager.moveForward(m);
                best = Math.max(best, minMax(depth - 1, alpha, beta, !isWhite));
                this.moveManager.movenBackward();
                alpha = Math.max(alpha, best);
                if (alpha >= beta) {
                    break;
                }
            }
            return best;
        } else {
            int best = Main.MAX_VALUE;
            for (Move m : moves) {
                this.moveManager.moveForward(m);
                best = Math.min(best, minMax(depth - 1, alpha, beta, !isWhite));
                this.moveManager.movenBackward();
                beta = Math.min(beta, best);
                if (alpha >= beta) {
                    break;
                }
            }
            return best;
        }
    }
```

# Author
* Martin Krčma

# License
* This project is licensed under GNU General Public License v3.0 - see the [LICENSE.md](https://github.com/0xMartin/Chess/blob/master/LICENSE) file for details
