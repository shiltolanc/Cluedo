public class Main {
    static Initialiser UI = new Initialiser();
    static Board board = new Board();
    public static boolean isGameOn = true;

    public static void main(String[] args) {
        board.determineCharacters();    // Comment out for testing
        UI.setup();

        while (Main.isGameOn) {
            for (Character character : board.characters) {
                character.performTurn(board);
                if (!Main.isGameOn)
                    break;    // Exit the loop if a player has won
            }
        }
        System.out.println("Game over, please close the window.");
    }
}
