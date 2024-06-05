import java.util.Objects;

public class Card {
    public enum CardType { WEAPON, CHARACTER, ESTATE }

    private String name;
    private CardType type;

    public Card(String name, CardType type) {
        this.name = name;
        this.type = type;
    }

    public CardType getType() { return type; }

    @Override
    public String toString() {
        return name + " (" + type.toString() + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Card card = (Card) o;
        return Objects.equals(name, card.name) &&
                type == card.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, type);
    }

}