package task1.config;

// Configuration.java
import java.util.*;

public class Configuration {
    private int totalTubes;  // Общее количество пробирок
    private int tubeCapacity; // Вместимость каждой пробирки
    private int colorCount; // Количество цветов в игре

    public Configuration() {
        this.totalTubes = 5;
        this.tubeCapacity = 4;
        this.colorCount = 3;
    }

    public void configureFromUserInput() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Настройка головоломки 'Сортировка жидкостей'");
        System.out.println("=============================================");

        System.out.println("Хочешь быстрый запуск? y/n");
        if (scanner.next().toLowerCase().startsWith("y")) {
            configureQuickStart();
        } else {
            // Настраиваем количество пробирок
            System.out.print("Введите общее количество пробирок (минимум 3): ");
            this.totalTubes = Math.max(3, scanner.nextInt());

            // Настраиваем вместимость пробирки
            System.out.print("Введите вместимость каждой пробирки (4-6): ");
            this.tubeCapacity = Math.max(4, Math.min(6, scanner.nextInt()));

            // Настраиваем количество цветов в игре
            int maxColors = totalTubes - 1;
            System.out.printf("Введите количество цветов (1-%d): ", maxColors);
            this.colorCount = Math.max(1, Math.min(maxColors, scanner.nextInt()));
        }

        scanner.close();

        printConfiguration();
    }

    public void configureQuickStart() {
        // Быстрая настройка игры
        this.totalTubes = 5;
        this.tubeCapacity = 4;
        this.colorCount = 3;
    }

    private void printConfiguration() {
        System.out.println("\nКонфигурация установлена:");
        System.out.println("   • Пробирок: " + totalTubes);
        System.out.println("   • Вместимость пробирок: " + tubeCapacity);
        System.out.println("   • Цветов: " + colorCount);

    }

    // Геттеры
    public int getTotalTubes() {
        return totalTubes;
    }

    public int getTubeCapacity() {
        return tubeCapacity;
    }

    public int getColorCount() {
        return colorCount;
    }
}
