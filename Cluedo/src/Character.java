import java.util.*;
import java.util.stream.Collectors;

public class Character {

    /**
     * Fields
     */
    private int x;
    private int y;
    private boolean inEstate = false;
    private List<Coord> list = new ArrayList<>();
    private String initial = "";
    private Board board;
    private Direction direction;
    private Set<Card> cards = new HashSet<>();
    private boolean hasMadeFalseAccusation = false;
    Scanner sc = new Scanner(System.in);
    Estate e;

    public Estate getEstate(){
        return e;
    }

    public String getInitial() { return initial; }

    /**
     * Order of rotation for characters following an anti-clockwise rotation.
     */
    public enum Direction {
        /* Topmost position on the table. (NORTH) */
        LUCILLA,
        /* Rightmost position on the table. (EAST) */
        BERT,
        /* Bottom position on the table. (SOUTH) */
        MALINA,
        /* Leftmost position on the table. (WEST) */
        PERCY;

        /**
         * Returns the next direction to play after this one
         * @return The rotated direction.
         */

        public Direction next() {
            if (this.equals(LUCILLA)) {
                return BERT;
            }
            if (this.equals(BERT)) {
                return MALINA;
            }
            if (this.equals(MALINA)) {
                return (Main.UI.getPlayerCount() == 3) ? LUCILLA : PERCY;
            }
            return LUCILLA;
        }
    }


    Character(int x, int y, String initial, Direction direction) {
        this.x = x;
        this.y = y;
        this.initial += initial;
        this.direction = direction;
    }

    public Direction getName(){
        return this.direction;
    }

    public void setEstate(boolean b){
        inEstate = b;
    }

    public void addCards(Card c){
        cards.add(c);
    }

    public Set<Card> getCards(){
        return cards;
    }

    public void setX(int i){
        this.x = i;
    }
    public void setY(int i){
        this.y = i;
    }

    /**
     * Checks if a character exists at a given coordinate
     * @param x
     * @param y
     * @return
     */
    public boolean isHere(int x, int y){
        return this.x == x && this.y == y;
    }

    public void performTurn(Board b) {

        if (!hasMadeFalseAccusation) {
            String in;
            Initialiser.panel2Text.selectAll();
            Initialiser.panel2Text.replaceSelection(" ");
            System.out.println("Players turn: " + this.getName() + "\n");
            System.out.println("please enter \"x\" ");
            in = sc.nextLine();
            in = in.toLowerCase();
            while(!in.equals("x")){
                in = sc.nextLine();
                in = in.toLowerCase();
            }

            Random rand = new Random();
            int moves = rand.nextInt(6) + 1 + rand.nextInt(6) + 1; // sum of two six-sided dice
            System.out.println('\n' + "================================");
            System.out.println("Players turn: " + this.getName() + "\n");
            System.out.println("your cards:");
            for(Card c : cards){
                System.out.println(c.toString());
            }
            System.out.println("\n" + this.getName() + " rolled " + moves + "!");
            move(moves, b);  // Perform the character's move

            // Check if the character can make a guess
            if (this.inEstate && !(this.e instanceof GreyArea)) {
                System.out.println("\nYou have entered " + e.getName() + ". You can make a guess.");
                makeGuess(b);
                if (!Main.isGameOn) {
                    return;
                }
                System.out.println("Guess completed. Please hand the tablet to the next player.");
            } else {
                System.out.println("You can't make a guess right now.");
            }

            // Update about the next turn
            System.out.println("Next player's turn: " + this.getName().next());

        } else {
            System.out.println("\nIt's " + this.getName() + "'s turn, but they made a false accusation. Skipping their turn.");
        }
        System.out.println("\nNext turn for: " + this.getName().next() + ". Hand over the device.");

    }


    public boolean isEstateEntrance(int x, int y) {
        for(Estate estate : board.estates) {
            if (estate.getEntrances() != null) {
                for (Coord entrance : estate.getEntrances()) {
                    if (x == entrance.getX() && y == entrance.getY()){
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean enterEstate(int x, int y){
        for (Estate e : board.estates) {
            if (!(e instanceof GreyArea)) {
                if (x >= e.getX() && x <= e.getX2() &&
                        y >= e.getY() && y <= e.getY2()) {
                    return true;
                }
            }
        }
        return false;
    }

    public Estate findEstate(int x, int y){
        for (Estate e : board.estates) {
            if (!(e instanceof GreyArea)) {
                if (x >= e.getX() && x <= e.getX2() &&
                        y >= e.getY() && y <= e.getY2()) {
                    return e;
                }
            }
        }
        return null;
    }

    /**
     * Keyboard input conversion for character movement on the board
     * @param moves
     * @param b
     */
    public void move(int moves, Board b) {
        board = b;

        String in;
        list.add(new Coord(x, y));




        System.out.println("W = North");
        System.out.println("A = West");
        System.out.println("S = South");
        System.out.println("D = East");
        System.out.println("Please input a direction to move: ");

        while(moves > 0){
            System.out.println(moves + " moves remaining. ");
            in = sc.nextLine();
            in = in.toLowerCase();

            int newX = x;
            int newY = y;

            switch (in) {
                case "w" -> newY--;
                case "a" -> newX--;
                case "s" -> newY++;
                case "d" -> newX++;
            }

            if(inEstate){
                if(checkEstateMoveRules(newX, newY)){
                    x = newX;
                    y = newY;
                    list.add(new Coord(x, y));
                    if (!enterEstate(x, y)) {

                        inEstate = false;
                    }
                    Initialiser.text.setText(board.toString());
                }
                else {
                    System.out.println("Please try a valid movement");
                }
            }
            else if (checkMoveRules(newX, newY)) {
                x = newX;
                y = newY;
                moves--;
                list.add(new Coord(x, y));
                if (enterEstate(x, y)) {
                    e = findEstate(x, y);
                    e.addChars(this);
                    moves = 0;
                    inEstate = true;
                }
                Initialiser.text.setText(board.toString());
            } else {
                // System.out.println("Please try a valid movement");
            }
        }
        list.clear();
        b.toString();
    }

    public boolean checkMoveRules(int x, int y) {
        String errorMessage = "";

        // Check if next move is inside the estate but isnt an entrance
        if (!isEstateEntrance(x, y) && enterEstate(x, y)) {
            errorMessage = "You hit a wall";
            // return false;
        }

        // Check boundaries
        if(x < 0 || x >= board.getBoardWidth() || y < 0 || y >= board.getBoardHeight()) {
            errorMessage = "Thats out of bounds";
            // return false;
        }

        // Check if square is occupied by another character
        for(Character c: board.characters) {
            if(c != this && c.isHere(x, y) && !enterEstate(x, y)) { // If occupied character is not in estate
                errorMessage = "There is another character in your way";
                // return false;
            }
        }

        // Prevent users entering grey areas
        if (checkGrey(x, y)) {
            errorMessage = "You are trying to enter a grey area";
            // return false;
        }

        //prevents user from revisiting tiles
        for(Coord c : list){
            if(c.getX() == x && c.getY() == y){
                errorMessage = "You cannot backtrack";
                // return false;
            }
        }

        // Invalid movement, print relevant error message
        if (!errorMessage.isEmpty()){
            System.out.println("ERROR: " + errorMessage + ", please try a valid movement.");
            return false;
        }

        return true;
    }

    public boolean checkEstateMoveRules(int x, int y){

        if(isEstateEntrance(list.get(list.size()-1).getX(),list.get(list.size()-1).getY()) && !enterEstate(x, y)){
            return true;
        }

        if (!isEstateEntrance(x, y) && !enterEstate(x, y)) {
            return false;
        }

        return true;
    }

    /**
     * If within a grey area
     * @param x
     * @param y
     * @return
     */
    public boolean checkGrey(int x , int y) {
        for (Estate e : board.estates) {
            if (e instanceof GreyArea) {
                if (x >= e.getX() && x <= e.getX2()) {
                    if (y >= e.getY() && y <= e.getY2()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Asks the player to make a guess
     * @param board
     */
    public void makeGuess(Board board) {
        Scanner scanner = new Scanner(System.in);
        Character characterGuessed = null;
        String weaponGuessed = null;
        Estate estateGuessed = this.e;

        // Ask for guess type
        System.out.println("Would you like to hypothesize or guess");
        System.out.println("1.Hypothesize \n2.Guess");
        String guessType = scanner.nextLine().toLowerCase();

        while (!(guessType.equals("1") || guessType.equals("1"))) {
            System.out.println("Invalid choice. Enter '1' or '2'.");
            guessType = scanner.nextLine().toLowerCase();
        }

        System.out.println("Guess a character:");
        int i = 1;
        for (Character c : board.characters) {
            System.out.println(i++ + ". " + c.getName());
        }
        int characterChoice = scanner.nextInt();
        characterGuessed = board.characters.get(characterChoice);
        System.out.println("Character Guessed: " + characterGuessed.toString());

        System.out.println("Guess a weapon:");
        i = 1;
        for (String w : board.Weapons) {
            System.out.println(i++ + ". " + w);
        }
        int weaponChoice = scanner.nextInt();
        weaponGuessed = board.Weapons.get(weaponChoice - 1);

        // The current location of the player is the guessed estate
        //this.e = estateGuessed;
        board.characters.get(characterChoice).setX((int)(this.e.getX2()-this.e.getX()));
        board.characters.get(characterChoice).setY((int)(this.e.getY2()-this.e.getY()));
        //board.characters.get(characterChoice - 1).setEstate(true);
        Initialiser.text.setText(board.toString());

        // Refute the guess
        if (guessType.equals("1")) {
            refuteGuess(characterGuessed, weaponGuessed, estateGuessed, board);
        } else { // Final guess
            if (!refuteFinalGuess(characterGuessed, weaponGuessed, estateGuessed, board)) {
                // Final guess was correct and no refutation was made. Guessing player wins.
                Initialiser.panel2Text.selectAll();
                Initialiser.panel2Text.replaceSelection(" ");
                System.out.println(this.getName() + " has won the game!");
                Main.isGameOn = false;    // Set isGameOn to false when a player wins
                return;
            } else { // Final guess was incorrect
                System.out.println(this.getName() + " has been eliminated!");
                hasMadeFalseAccusation = true;
                //board.characters.remove(this);
            }
        }
    }

    /**
     * Asks the next player to refute the guess
     * @param characterGuessed
     * @param weaponGuessed
     * @param estateGuessed
     * @param board
     */
    public void refuteGuess(Character characterGuessed, String weaponGuessed, Estate estateGuessed, Board board) {
        Direction nextPlayerDirection = this.getName().next();
        boolean found = false;
        Card refute = null;
        String charName = "";
        ArrayList<Character> chars = board.characters;
        int displace = 0;
        for(int i = 0; i  < chars.size()-1; i++){
            if(chars.get(i).getName().equals(this.getName())){
                displace = i;
            }
        }
        for(int i = 0; i  < displace; i++){
            chars.add(chars.get(0));
            chars.remove(0);
        }

        for(Character c : chars){
            if (c.getName() == nextPlayerDirection) {
                Initialiser.panel2Text.selectAll();
                Initialiser.panel2Text.replaceSelection(" ");
                if(found){
                    System.out.println("\nHand the device to " + nextPlayerDirection
                            + "\n. Confirm you have taken the device and type 'yes': ");

                    String confirmation = new Scanner(System.in).nextLine().toLowerCase();

                    while (!confirmation.equals("yes")) {
                        System.out.println("Please type 'yes' when you are ready:");
                        confirmation = new Scanner(System.in).nextLine().toLowerCase();
                    }

                    System.out.println("\n" + charName
                            + " has refuted using one of these cards \n" +
                            characterGuessed.getName() + "\n"+
                            weaponGuessed + "\n" +
                            estateGuessed.getName() + "\n");


                    System.out.println("\n Please type 'yes' to confirm");
                    confirmation = new Scanner(System.in).nextLine().toLowerCase();

                    while (!confirmation.equals("yes")) {
                        System.out.println("Please type 'yes' when you are ready:");
                        confirmation = new Scanner(System.in).nextLine().toLowerCase();
                    }
                    nextPlayerDirection = nextPlayerDirection.next();

                }
                else{

                    System.out.println("\nIt's time for " + nextPlayerDirection
                            + " to potentially refute the guess. \nConfirm you have taken the device and type 'yes': ");

                    String confirmation = new Scanner(System.in).nextLine().toLowerCase();

                    while (!confirmation.equals("yes")) {
                        System.out.println("Please type 'yes' when you are ready:");
                        confirmation = new Scanner(System.in).nextLine().toLowerCase();
                    }

                    List<Card> chosenCards = List.of(
                            new Card(characterGuessed.getName().toString(), Card.CardType.CHARACTER),
                            new Card(weaponGuessed, Card.CardType.WEAPON),
                            new Card(estateGuessed.getName(), Card.CardType.ESTATE));

                    List<Card> matchingCards = chosenCards.stream().filter(card ->
                            c.getCards().contains(card)).collect(Collectors.toList());

                    if (!matchingCards.isEmpty()) {
                        System.out.println("You have these cards. Which one do you want to reveal?");

                        for (int i = 0; i < matchingCards.size(); i++) {
                            System.out.println((i+1) + ": " + matchingCards.get(i));
                        }

                        int revealedCardChoice = new Scanner(System.in).nextInt();
                        System.out.println("You have revealed: " + matchingCards.get(revealedCardChoice - 1));

                        found = true;
                        charName = nextPlayerDirection.toString();
                        refute = matchingCards.get(revealedCardChoice - 1);
                    } else {
                        System.out.println("\n" +  this.direction
                                + " has guessed: \n" + " - " +
                                characterGuessed.getName() + "\n" + " - " +
                                weaponGuessed + "\n" + " - " +
                                estateGuessed.getName() + "\n");
                        System.out.println("You don't have any of these cards.");
                        System.out.println("\n Please type 'yes' to confirm");
                        confirmation = new Scanner(System.in).nextLine().toLowerCase();
                        while (!confirmation.equals("yes")) {
                            System.out.println("Please type 'yes' when you are ready:");
                            confirmation = new Scanner(System.in).nextLine().toLowerCase();
                        }

                    }
                    nextPlayerDirection = nextPlayerDirection.next();
                }
            }
        }
        Initialiser.panel2Text.selectAll();
        Initialiser.panel2Text.replaceSelection(" ");
        System.out.println("Please pass to: " + this.getName() + "\n");
        System.out.println("please enter \"x\" to see your guess results");
        String in;
        in = sc.nextLine();
        in = in.toLowerCase();
        while(!in.equals("x")){
            System.out.println("please enter \"x\" to see your guess results");
            in = sc.nextLine();
            in = in.toLowerCase();
        }
        if(!found){
            System.out.println("No player was able to refute the guess!");
        }
        else{
            System.out.println(charName + " refuted your guess using " + refute);
            System.out.println("please enter \"x\" to end your turn");
            in = sc.nextLine();
            while(!in.equals("x")){
                System.out.println("please enter \"x\" to end your turn");
                in = sc.nextLine();
                in = in.toLowerCase();
            }
        }
    }

    /**
     * Checks if the final guess is correct
     * @param characterGuessed
     * @param weaponGuessed
     * @param estateGuessed
     * @param board
     * @return
     */
    public boolean refuteFinalGuess(Character characterGuessed, String weaponGuessed, Estate estateGuessed, Board board) {
        Direction thisPlayerDirection = this.getName();
        Direction nextPlayerDirection = thisPlayerDirection.next();

        // Return true, signifying the guess was refuted
        for (Character character : board.characters) {
            if (character.getName() == nextPlayerDirection) {
                for (Card c : character.getCards()) {
                    if (c.equals(new Card(characterGuessed.getName().toString(), Card.CardType.CHARACTER)) ||
                            c.equals(new Card(weaponGuessed, Card.CardType.WEAPON)) ||
                            c.equals(new Card(estateGuessed.getName(), Card.CardType.ESTATE))) {
                        System.out.println(character.getInitial() + " refuted your guess!");
                        return true;
                    }
                }
                nextPlayerDirection = nextPlayerDirection.next();
            }
        }

        // Return false, indicating that the final guess is correct
        for (Card c : board.murderCards) {
            if (!c.equals(new Card(characterGuessed.getName().toString(), Card.CardType.CHARACTER)) &&
                    !c.equals(new Card(weaponGuessed, Card.CardType.WEAPON)) &&
                    !c.equals(new Card(estateGuessed.getName(), Card.CardType.ESTATE))) {
                return true;
            }
        }
        return false;
    }

}