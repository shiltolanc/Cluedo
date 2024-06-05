import java.util.ArrayList;
import java.util.List;

public class Estate {
    private int x;
    private int y;
    private int x2;
    private int y2;
    private String name = "";
    private List<Coord> entrances;
    private List<Character> chars = new ArrayList<>();

    Estate(int x, int y, int x2, int y2, String name, List<Coord> entrances) {
        this.x = x;
        this.y = y;
        this.x2 = x2;
        this.y2 = y2;
        this.name = name;
        this.entrances = entrances;
    }

    /**
     * Getters and setters
     * @return The corresponding reference
     */
    public int getX() { return x; }

    public int getY() { return y; }

    public int getX2() { return x2; }

    public int getY2() { return y2; }

    public String getName() { return name; }

    public List<Coord> getEntrances() { return entrances; }

    public List<Character> getChars() { return chars; }

    public void addChars(Character character) { chars.add(character); }

    public void removeChars(Character character) { chars.remove(character); }
    
}