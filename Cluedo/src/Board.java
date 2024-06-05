import java.lang.reflect.Array;
import java.util.*;

public class Board {

    private int boardHeight = 24;
    private int boardWidth = 24;

    Character Bert = new Character(1, 9, "B", Character.Direction.BERT);
    Character Lucilla = new Character(11, 1, "L", Character.Direction.LUCILLA);
    Character Percy = new Character(22, 14, "P", Character.Direction.PERCY);
    Character Malina = new Character(9, 22, "M", Character.Direction.MALINA);
    List<Coord> hhEntrances = List.of(new Coord(5, 6), new Coord(6, 3));
    List<Coord> mmEntrances = List.of(new Coord(17, 5), new Coord(20, 6));
    List<Coord> ccEntrances = List.of(new Coord(3, 17), new Coord(6, 18));
    List<Coord> ppEntrances = List.of(new Coord(18, 17), new Coord(17, 21));
    List<Coord> vvEntrances = List.of(new Coord(9, 12), new Coord(12, 10), new Coord(14, 11), new Coord(11, 13));
    Estate HH = new Estate(2, 2, 6, 6, "Haunted House", hhEntrances);
    Estate MM = new Estate(17, 2, 21, 6, "Manic Manor", mmEntrances);
    Estate CC = new Estate(2, 17, 6, 21, "Calamity Castle", ccEntrances);
    Estate PP = new Estate(17, 17, 21, 21, "Peril Palace", ppEntrances);
    Estate VV = new Estate(9, 10, 14, 13, "Visitation Villa", vvEntrances);
    GreyArea Left = new GreyArea(5, 11, 6, 12, "GREY AREA");
    GreyArea Right = new GreyArea(17, 11, 18, 12, "GREY AREA");
    GreyArea Top = new GreyArea(11, 5, 12, 6, "GREY AREA");
    GreyArea Bot = new GreyArea(11, 17, 12, 18, "GREY AREA");
    ArrayList<Estate> estates = new ArrayList<>(List.of(HH, MM, CC, PP, VV, Left, Right, Top, Bot));
    ArrayList<Card> deck = new ArrayList<>();
    Set<Card> murderCards = new HashSet<>();
    ArrayList<String> Weapons = new ArrayList<>(List.of("Broom", "Scissors", "Knife", "Shovel", "iPad"));
    ArrayList<Character> characters = new ArrayList<>(List.of(Lucilla, Bert, Malina, Percy));

    /**
     * Determine the number of players in the game
     * @return The list of active characters
     */
    public void determineCharacters() {
        int rand = new Random().nextInt(Main.UI.getPlayerCount());

        if (Main.UI.getPlayerCount() == 3) {
            characters = new ArrayList<>(List.of(Lucilla, Bert, Malina));
        } else if (Main.UI.getPlayerCount() == 4) {
            characters = new ArrayList<>(List.of(Lucilla, Bert, Malina, Percy));
        } else {
            throw new IllegalArgumentException("Case exception: player count is " + Main.UI.getPlayerCount()); // Dead code
        }

        for (int i = 0; i < rand; i++) {
            Character c = characters.remove(characters.size() - 1);
            characters.add(0 ,c );
        }

        for (Character c : characters) {
            System.out.println(c.getName());
        }
    }

    /**
     * Creates and distributes all the cards
     */
    Board() {
        for (Estate e: estates) {
            if(!(e instanceof GreyArea)) {
                deck.add(new Card(e.getName(), Card.CardType.ESTATE));
            }
        }
        for (String name: Weapons) {
            deck.add(new Card(name, Card.CardType.WEAPON));
        }
        for (Character c: characters) {
            deck.add(new Card(c.getName().toString(), Card.CardType.CHARACTER));
        }

        setMurderCards();
        distributeCards();

        // These are used for debugging
        for (Character c: characters) {
            System.out.println(c.getName()+" cards:");
            System.out.println(c.getCards() + "\n");
        }
            System.out.println("Murder cards :");
            System.out.println(murderCards);
    }

    /**
     * Chooses 1 card from each category at random
     */
    public void setMurderCards() {
        Collections.shuffle(deck);

        int countWeapon = 0;
        int countCharacter = 0;
        int countEstate = 0;

        for (Card c : deck) {
            if (countWeapon < 1 && c.getType() == Card.CardType.WEAPON) {
                murderCards.add(c);
                countWeapon++;
            } else if (countCharacter < 1 && c.getType() == Card.CardType.CHARACTER) {
                murderCards.add(c);
                countCharacter++;
            } else if (countEstate < 1 && c.getType() == Card.CardType.ESTATE) {
                murderCards.add(c);
                countEstate++;
            }

            // Stop the loop when one card of each type has been added to murderCards
            if (countWeapon == 1 && countCharacter == 1 && countEstate == 1) {
                break;
            }
        }
        for(Card c: murderCards){
            deck.remove(c);
        }
    }

    /**
     * distributes cards as equally as possible between the players
     */
    public void distributeCards(){
        int playerCount = Main.UI.getPlayerCount();

        if (playerCount < 3 || playerCount > 4) {
            throw new IllegalArgumentException("The number of characters must be between 3 and 4.");
        }

        // Shuffle the deck of cards
        Collections.shuffle(deck);

        int numCardsPerPlayer = deck.size() / playerCount;
        int remainingCards = deck.size() % playerCount;

        int cardIndex = 0;
        while (cardIndex < deck.size()) {
            int numCardsToDeal = numCardsPerPlayer + (remainingCards > 0 ? 1 : 0);
            for (int i = 0; i < numCardsToDeal; i++) {
                characters.get(cardIndex % playerCount).addCards(deck.get(cardIndex));
                cardIndex++;
            }
            remainingCards--;
        }
    }

    // Getters for board dimensions
    public int getBoardHeight() {
        return boardHeight;
    }
    
    public int getBoardWidth() {
        return boardWidth;
    }

    public String toString(){

        //Draw the first line
        StringBuilder r = new StringBuilder();
        r.append("+");
        for (int j = 0; j != boardWidth; ++j) {
            r.append("---");
            r.append("+");
        }
        r.append("\n");

        //Draw the rest of the lines
        for (int i = 0; i < boardHeight; ++i) {
            StringBuilder d = new StringBuilder();

            d.append("|");
            for (int j = 0; j != boardWidth; ++j) {

                boolean f = true;
                for(Character c : characters) {
                    if (c.isHere(j, i)) {
                        d.append(" " + c.getInitial() + " ");
                        f = false;
                        break;
                    }
                }

                for(Estate e : estates){
                    if(j >= e.getX() && j <= e.getX2() &&
                            i >= e.getY() && i <= e.getY2()) {
                        if(e instanceof GreyArea) {
                            d.append(" x ");
                            f = false;
                            break;
                        }
                    }
                }

                if(f){ d.append("   "); }

                    f = true;
                    for(Estate e : estates) {
                        if(j >= e.getX() && j < e.getX2() &&
                                i >= e.getY() && i <= e.getY2()) {
                            d.append(" ");
                            f = false;
                            break;
                        }
                    }

                    if(f){
                        if((i == 3 && j == 6)   ||
                           (i == 5 && j == 16)  ||
                           (i == 18 && j == 6)  ||
                           (i == 21 && j == 16) ||
                           (i == 12 && j == 8)  ||
                           (i == 11 && j == 14)) {
                            d.append(" ");
                        } else {
                            d.append("|");
                        }
                    }

            }
            d.append("\n");
            d.append("+");

            for (int j = 0; j != boardWidth; ++j) {
                boolean f = true;
                for(Estate e: estates) {
                    if(j >= e.getX() && j <= e.getX2() &&
                       i >= e.getY() && i < e.getY2()) {
                        d.append("   ");
                        f = false;
                        break;
                    }
                }

                if(f) {
                    if((i == 6 && j == 5)   ||
                       (i == 6 && j == 20)  ||
                       (i == 16 && j == 3)  ||
                       (i == 16 && j == 18) ||
                       (i == 9 && j == 12)  ||
                       (i == 13 && j == 11)) {
                        d.append("   ");
                    } else {
                        d.append("---");
                    }
                }
                f = true;
                for(Estate e : estates){
                    if(j >= e.getX() && j < e.getX2() &&
                       i >= e.getY() && i < e.getY2()) {
                        d.append(" ");
                        f = false;
                        break;
                    }
                }
                if(f) { d.append("+"); }
            }
            d.append("\n");
            r.append(d);
        }
        return r.toString();
    }

}
