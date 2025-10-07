package task1;
import task1.config.Configuration;

import java.util.*;

public class BaseSolver {
    private List<Deque<String>> tubes;
    private int tubeCapacity;
    private List<Move> solution;
    private int totalMoves;

    public BaseSolver(String[][] initialState, int capacity) {
        this.tubeCapacity = capacity;
        this.tubes = new ArrayList<>();
        this.solution = new ArrayList<>();
        this.totalMoves = 0;

        // Инициализируем пробирки
        for (String[] tube : initialState) {
            Deque<String> tubeDeque = new ArrayDeque<>();
            for (String liquid : tube) {
                if (liquid != null && !liquid.trim().isEmpty() && !liquid.equals(" ")) {
                    tubeDeque.addLast(liquid);
                }
            }
            tubes.add(tubeDeque);
        }
    }

    private boolean isTubeComplete(Deque<String> tube) {
        if (tube.isEmpty()) return true;
        if (tube.size() < tubeCapacity) return false;

        String firstColor = tube.peekFirst();
        for (String color : tube) {
            if (!color.equals(firstColor)) return false;
        }
        return true;
    }

    private String getTopColor(Deque<String> tube) {
        return tube.isEmpty() ? null : tube.peekLast();
    }

    private int getTopColorAmount(Deque<String> tube) {
        if (tube.isEmpty()) return 0;

        String topColor = getTopColor(tube);
        int count = 0;

        Iterator<String> descendingIterator = tube.descendingIterator();
        while (descendingIterator.hasNext()) {
            if (descendingIterator.next().equals(topColor)) {
                count++;
            } else {
                break;
            }
        }
        return count;
    }

    private boolean canPour(int fromTube, int toTube) {
        Deque<String> from = tubes.get(fromTube);
        Deque<String> to = tubes.get(toTube);

        if (from.isEmpty()) return false;
        if (to.size() == tubeCapacity) return false;

        String fromColor = getTopColor(from);
        String toColor = getTopColor(to);

        return to.isEmpty() || (toColor != null && toColor.equals(fromColor));
    }

    private int findSuitableTube(int fromTube) {
        String fromColor = getTopColor(tubes.get(fromTube));
        if (fromColor == null) return -1;

        // Приоритет 1: пробирки, которые можно завершить
        for (int i = 0; i < tubes.size(); i++) {
            if (i != fromTube && !tubes.get(i).isEmpty()) {
                Deque<String> targetTube = tubes.get(i);
                String targetColor = getTopColor(targetTube);
                if (targetColor != null && targetColor.equals(fromColor) &&
                        targetTube.size() + getTopColorAmount(tubes.get(fromTube)) >= tubeCapacity &&
                        canPour(fromTube, i)) {
                    return i;
                }
            }
        }

        // Приоритет 2: пробирки с тем же цветом сверху
        for (int i = 0; i < tubes.size(); i++) {
            if (i != fromTube && !tubes.get(i).isEmpty()) {
                String topColor = getTopColor(tubes.get(i));
                if (topColor != null && topColor.equals(fromColor) && canPour(fromTube, i)) {
                    return i;
                }
            }
        }

        // Приоритет 3: пустые пробирки
        for (int i = 0; i < tubes.size(); i++) {
            if (i != fromTube && tubes.get(i).isEmpty() && canPour(fromTube, i)) {
                return i;
            }
        }

        return -1;
    }

    private boolean isSolved() {
        for (Deque<String> tube : tubes) {
            if (!isTubeComplete(tube)) {
                return false;
            }
        }
        return true;
    }

    public void solve() {
        System.out.println("Начальное состояние:");
        printState();

        int maxIterations = 1000;
        int iterations = 0;

        while (!isSolved() && iterations < maxIterations) {
            boolean moved = false;

            for (int fromTube = 0; fromTube < tubes.size(); fromTube++) {
                if (isTubeComplete(tubes.get(fromTube))) continue;

                int toTube = findSuitableTube(fromTube);
                if (toTube != -1) {
                    performPour(fromTube, toTube);
                    moved = true;
                    break;
                }
            }

            if (!moved) {
                System.out.println("Не удалось найти подходящий ход");
                break;
            }
            iterations++;
        }

        if (isSolved()) {
            System.out.println("Головоломка решена за " + totalMoves + " ходов!");
            printSolution();
        } else {
            System.out.println("Решение не найдено за " + iterations + " итераций");
        }
    }

    private void performPour(int fromTube, int toTube) {
        Deque<String> from = tubes.get(fromTube);
        Deque<String> to = tubes.get(toTube);

        String topColor = getTopColor(from);
        int availableSpace = tubeCapacity - to.size();
        int topColorAmount = getTopColorAmount(from);

        int amountToPour = Math.min(topColorAmount, availableSpace);

        // Выполняем переливание
        Deque<String> tempStack = new ArrayDeque<>();
        for (int i = 0; i < amountToPour; i++) {
            tempStack.push(from.removeLast());
        }
        for (int i = 0; i < amountToPour; i++) {
            to.addLast(tempStack.pop());
        }

        // Сохраняем ход
        solution.add(new Move(fromTube, toTube, topColor, amountToPour));
        totalMoves++;

        System.out.printf("Ход %d: %s%n", totalMoves, solution.get(solution.size() - 1));
        printState();
    }

    public void printState() {
        System.out.println("\nТекущее состояние пробирок:");

        for (int i = 0; i < tubes.size(); i++) {
            System.out.printf("Пробирка %d: [", i + 1);
            Deque<String> tube = tubes.get(i);

            // Создаем список для печати
            List<String> elements = new ArrayList<>(tube);
            for (int j = 0; j < elements.size(); j++) {
                System.out.print(elements.get(j));
                if (j < elements.size() - 1) {
                    System.out.print(", ");
                }
            }

            String topColor = getTopColor(tube);
            if (topColor != null) {
                System.out.print("] (верх: " + topColor + ")");
            } else {
                System.out.print("] (пустая)");
            }
            System.out.println();
        }
        System.out.println();
    }

    public void printSolution() {
        System.out.println("\nПолная последовательность ходов:");
        for (int i = 0; i < solution.size(); i++) {
            System.out.printf("%d. %s%n", i + 1, solution.get(i));
        }
    }

    public int getTotalMoves() {
        return totalMoves;
    }

    public List<Move> getSolution() {
        return new ArrayList<>(solution);
    }
}