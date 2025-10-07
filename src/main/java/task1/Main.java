package task1;// task1.Main.java
import task1.config.Configuration;
import task1.config.InitialStateGenerator;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("=== СОРТИРОВКА ЖИДКОСТЕЙ ===");
        System.out.println("Версия с буквенными обозначениями\n"); // Версию с красивыми цветами сделать не успеваю

        Configuration config = new Configuration();
        config.configureFromUserInput();
        int totalTubes = config.getTotalTubes();
        int tubeCapacity = config.getTubeCapacity();
        int colorCount = config.getColorCount();

//        // Настройка начальных параметров
//        System.out.print("Введите количество пробирок: ");
//        int totalTubes = scanner.nextInt();
//
//        System.out.print("Введите вместимость пробирок: ");
//        int tubeCapacity = scanner.nextInt();
//
//        System.out.print("Введите количество цветов: ");
//        int colorCount = scanner.nextInt();

        if (colorCount >= totalTubes) {
            System.out.println("Ошибка: количество цветов должно быть меньше количества пробирок!");
            return;
        }

        // Генерация начального состояния
        InitialStateGenerator generator = new InitialStateGenerator(totalTubes, tubeCapacity, colorCount);
        generator.printColorLegend();

        String[][] initialState = generator.generateState();

        // Создание и запуск решателя
        BaseSolver solver = new BaseSolver(initialState, tubeCapacity);

        System.out.println("\nНачинаем решение...");
        solver.solve();

        // Статистика
        System.out.println("\n=== СТАТИСТИКА ===");
        System.out.println("Всего ходов: " + solver.getTotalMoves());
        System.out.println("Пробирок: " + totalTubes);
        System.out.println("Вместимость: " + tubeCapacity);
        System.out.println("Цветов: " + colorCount);

        scanner.close();
    }
}