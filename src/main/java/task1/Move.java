package task1;// task1.Move.java
import java.util.Map;

public class Move {
    int fromTube;
    int toTube;
    String color;
    int amount;

    public Move(int fromTube, int toTube, String color, int amount) {
        this.fromTube = fromTube;
        this.toTube = toTube;
        this.color = color;
        this.amount = amount;
    }

    @Override
    public String toString() {
        return String.format("Перелить %d капель '%s' из %d в %d",
                amount, color, fromTube + 1, toTube + 1);
    }

    public int getFromTube() {
        return fromTube;
    }

    public int getToTube() {
        return toTube;
    }

    public String getColor() {
        return color;
    }

    public int getAmount() {
        return amount;
    }
}