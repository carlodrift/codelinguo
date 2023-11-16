package unilim.but.sae;

// GameplayContext.java
public class Gameplay {
    private Board board;
    private String activePlayer;

    // Constructeur initialisant le plateau et le joueur actif
    public Gameplay(String player1, String player2) {
        this.board = new Board(player1, player2);
        this.activePlayer = player1;
    }

    // Méthode pour effectuer un mouvement
    public boolean makeMove(int column) {
        boolean success = board.placeToken(column, activePlayer);
        if (success) {
            if (board.checkVictory(column)) {
                System.out.println("Player " + activePlayer + " wins!");
                return true;
            } else if (board.isFull()) {
                System.out.println("The game is a draw!");
                return true;
            } else {
                activePlayer = (activePlayer.equals(board.getPlayer1())) ? board.getPlayer2() : board.getPlayer1();
            }
        }
        return false;
    }

    // Getter pour le plateau
    public Board getBoard() {
        return board;
    }
}

