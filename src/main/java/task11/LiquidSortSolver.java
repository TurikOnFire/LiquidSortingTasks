package task11;

import java.util.*;

public class LiquidSortSolver {

    record Move(int from, int to, int amount) {}

    public static List<Move> solve(String[][] initial) {
        int N = initial.length;
        int V = initial[0].length;

        String startKey = serialize(initial);
        Queue<String[][]> queue = new ArrayDeque<>();
        Queue<List<Move>> movesQueue = new ArrayDeque<>();
        Set<String> visited = new HashSet<>();

        queue.add(copyState(initial));
        movesQueue.add(new ArrayList<>());
        visited.add(startKey);

        while (!queue.isEmpty()) {
            String[][] state = queue.poll();
            List<Move> path = movesQueue.poll();

            if (isSolved(state))
                return path;

            for (int i = 0; i < N; i++) {
                for (int j = 0; j < N; j++) {
                    if (i == j)
                        continue;
                    if (canPour(state, i, j)) {
                        int amount = pourableAmount(state, i, j);
                        if (amount <= 0)
                            continue;

                        String[][] next = copyState(state);
                        performPour(next, i, j, amount);
                        String key = serialize(next);
                        if (visited.add(key)) {
                            List<Move> newPath = new ArrayList<>(path);
                            newPath.add(new Move(i, j, amount));
                            queue.add(next);
                            movesQueue.add(newPath);
                        }
                    }
                }
            }
        }

        return null;
    }

    private static boolean isSolved(String[][] state) {
        int N = state.length;
        int V = state[0].length;
        for (int i = 0; i < N; i++) {
            String top = null;
            int count = 0;
            for (int p = 0; p < V; p++) {
                String s = state[i][p];
                if (s == null)
                    continue;
                if (top == null)
                    top = s;
                if (!top.equals(s))
                    return false;
                count++;
            }
            if (count == 0) continue;
            if (count != V) return false;
        }
        return true;
    }

    private static boolean canPour(String[][] state, int from, int to) {
        int V = state[0].length;

        int topFromIdx = topIndex(state[from]);
        if (topFromIdx < 0)
            return false;

        int topToIdx = topIndex(state[to]);
        int filledTo = (topToIdx < 0) ? 0 : (topToIdx + 1);
        if (filledTo >= V)
            return false;

        String color = state[from][topFromIdx];
        if (topToIdx < 0)
            return true;

        String toColor = state[to][topToIdx];

        return color.equals(toColor);
    }

    private static int pourableAmount(String[][] state, int from, int to) {
        int V = state[0].length;
        int topFromIdx = topIndex(state[from]);
        if (topFromIdx < 0)
            return 0;

        String color = state[from][topFromIdx];
        int contiguous = 0;
        for (int p = topFromIdx; p >= 0; p--) {
            String s = state[from][p];
            if (s == null || !s.equals(color))
                break;
            contiguous++;
        }
        int topToIdx = topIndex(state[to]);
        int filledTo = (topToIdx < 0) ? 0 : (topToIdx + 1);
        int space = V - filledTo;

        return Math.min(contiguous, space);
    }

    private static int topIndex(String[] tube) {
        for (int p = tube.length - 1; p >= 0; p--) {
            if (tube[p] != null) return p;
        }
        return -1;
    }

    private static void performPour(String[][] state, int from, int to, int amount) {
        int fromTop = topIndex(state[from]);
        int toTop = topIndex(state[to]);
        int toPos = (toTop < 0) ? 0 : (toTop + 1);
        String color = state[from][fromTop];
        for (int k = 0; k < amount; k++) {
            state[to][toPos + k] = color;
            state[from][fromTop - k] = null;
        }
    }

    private static String serialize(String[][] state) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < state.length; i++) {
            if (i > 0) sb.append('|');
            for (int p = 0; p < state[i].length; p++) {
                if (p > 0) sb.append(',');
                sb.append(state[i][p] == null ? "." : escape(state[i][p]));
            }
        }
        return sb.toString();
    }

    private static String escape(String s) {
        return s.replace("|", "\\|").replace(",", "\\,");
    }

    private static String[][] copyState(String[][] st) {
        String[][] res = new String[st.length][st[0].length];
        for (int i = 0; i < st.length; i++) System.arraycopy(st[i], 0, res[i], 0, st[i].length);
        return res;
    }

    private static void printState(String[][] st) {
        int V = st[0].length;
        for (int p = V - 1; p >= 0; p--) {
            for (int i = 0; i < st.length; i++) {
                String x = st[i][p];
                System.out.print((x == null ? "." : x) + (i + 1 == st.length ? "" : " "));
            }
            System.out.println();
        }
        for (int i = 0; i < st.length; i++) System.out.print((i) + (i + 1 == st.length ? "" : " "));
        System.out.println("\n");
    }

    public static void main(String[] args) {
        String[][] example = new String[][]{
                {"A", "A", "B", "C"},
                {"A", "B", "B", "C"},
                {"C", "A", "B", "C"},
                {"D", "D", null, null},
                {"D", "D", null, null},
                {null, null, null, null},
                {null, null, null, null}
        };

        System.out.println("Начальное состояние:");
        printState(example);

        List<Move> solution = solve(example);
        if (solution == null) {
            System.out.println("Решение не найдено.");
            return;
        }

        System.out.println("Ход решения (из -> в : кол-во капель):\n");
        String[][] state = copyState(example);
        for (int i = 0; i < solution.size(); i++) {
            Move m = solution.get(i);
            System.out.printf("%2d) %d -> %d : %d\n", i + 1, m.from(), m.to(), m.amount());
            performPour(state, m.from(), m.to(), m.amount());
            printState(state);
        }

        System.out.println("Рещено за " + solution.size() + " ходов.");
    }
}

