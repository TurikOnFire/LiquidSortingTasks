package task1.config;

import java.util.*;

public class InitialStateGenerator {
    private int totalTubes;
    private int tubeCapacity;
    private int colorCount;

    public InitialStateGenerator(int totalTubes, int tubeCapacity, int colorCount) {
        this.totalTubes = totalTubes;
        this.tubeCapacity = tubeCapacity;
        this.colorCount = colorCount;
    }

    public String[][] generateState() {
        String[][] state = new String[totalTubes][tubeCapacity];
        String[] availableColors = {"R", "G", "B", "Y", "P", "O", "C", "M"}; // Буквенные обозначения для цветов

        // Создаем все капли
        List<String> allDrops = new ArrayList<>();
        for (int colorIdx = 0; colorIdx < colorCount; colorIdx++) {
            String color = availableColors[colorIdx % availableColors.length];
            for (int j = 0; j < tubeCapacity; j++) {
                allDrops.add(color);
            }
        }

        // Перемешиваем
        Collections.shuffle(allDrops);

        // Распределяем по пробиркам
        int dropIndex = 0;
        for (int tube = 0; tube < colorCount; tube++) {
            for (int pos = 0; pos < tubeCapacity; pos++) {
                state[tube][pos] = allDrops.get(dropIndex++);
            }
        }

        // Остальные пробирки оставляем пустыми
        for (int tube = colorCount; tube < totalTubes; tube++) {
            Arrays.fill(state[tube], " ");
        }

        return state;
    }

    public void printColorLegend() {
        System.out.println("Легенда цветов:");
        String[] colors = {"R", "G", "B", "Y", "P", "O", "C", "M"};
        String[] names = {"Красный", "Зеленый", "Синий", "Желтый", "Фиолетовый", "Оранжевый", "Голубой", "Пурпурный"};

        for (int i = 0; i < Math.min(colorCount, colors.length); i++) {
            System.out.println("  " + colors[i] + " - " + names[i]);
        }
    }
}