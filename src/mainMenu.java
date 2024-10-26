import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

// Класс для создания главного меню
public class mainMenu {

    // Метод для создания главного меню с фоновым изображением и кнопками
    JPanel createMainMenu(BufferedImage myPicture, Habitat habitat) {
        // Создание JPanel с переопределенным методом paintComponent для рисования фонового изображения
        JPanel menu = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Отрисовка изображения на фоне
                g.drawImage(myPicture, 0, 0, this);
            }
        };
        // Установка вертикального BoxLayout для размещения кнопок друг под другом
        menu.setLayout(new BoxLayout(menu, BoxLayout.Y_AXIS));

        // Создание кнопки "Новая игра"
        JButton newGameButton = new JButton("Новая игра");
        // Установка минимального и максимального размера кнопки
        newGameButton.setMinimumSize(new Dimension(200, 50));
        newGameButton.setMaximumSize(new Dimension(200, 50));
        // Центрирование кнопки по горизонтали
        newGameButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        // Добавление слушателя действий для кнопки
        newGameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Действия при нажатии на кнопку: начать новую игру
                habitat.setGameMenu();
            }
        });

        // Создание кнопки "Настройки"
        JButton settingButton = new JButton("Настройки");
        settingButton.setMinimumSize(new Dimension(200, 50));
        settingButton.setMaximumSize(new Dimension(200, 50));
        settingButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        settingButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Действия при нажатии на кнопку: открыть меню настроек
                habitat.setSettingMenu();
            }
        });

        // Создание кнопки "Выход"
        JButton exitButton = new JButton("Выход");
        exitButton.setMinimumSize(new Dimension(200, 50));
        exitButton.setMaximumSize(new Dimension(200, 50));
        exitButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Действия при нажатии на кнопку: выход из программы
                System.exit(0);
            }
        });

        // Добавление кнопок на панель меню и установка отступов между ними
        menu.add(Box.createVerticalGlue()); // Добавление гибкого вертикального пространства сверху
        menu.add(newGameButton); // Добавление кнопки "Новая игра"
        menu.add(Box.createRigidArea(new Dimension(0, 10))); // Добавление фиксированного вертикального пространства
        menu.add(settingButton); // Добавление кнопки "Настройки"
        menu.add(Box.createRigidArea(new Dimension(0, 10))); // Добавление фиксированного вертикального пространства
        menu.add(exitButton); // Добавление кнопки "Выход"
        menu.add(Box.createVerticalGlue()); // Добавление гибкого вертикального пространства снизу

        return menu; // Возвращение панели меню
    }
}
