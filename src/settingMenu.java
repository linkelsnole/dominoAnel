import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

// Класс для создания меню настроек
public class settingMenu {
    // Метод для создания окна настроек с фоновым изображением и кнопками
    JPanel createSettingMenu(BufferedImage myPicture, Habitat habitat) {
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

        // Создание метки "Громкость музыки:"
        JLabel label = new JLabel("Громкость музыки:");
        // Центрирование текста
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        label.setForeground(Color.WHITE); // Цвет текста
        label.setFont(new Font("Arial", Font.BOLD, 16)); // Шрифт текста

        // Создание слайдера для регулировки громкости
        JSlider musicValue = new JSlider(0, 100, habitat.volume);
        // Установка предпочтительного и максимального размеров слайдера
        musicValue.setPreferredSize(new Dimension(200, 20));
        musicValue.setMaximumSize(new Dimension(200, 50));
        // Добавление слушателя изменений для слайдера
        musicValue.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                habitat.volume = ((JSlider) e.getSource()).getValue(); // Получение текущего значения слайдера
                float volume = (float) ((JSlider) e.getSource()).getValue() / 2 - 50; // Расчет новой громкости музыки
                if (volume == -50) habitat.volumeControl.setValue(-80); // Если громкость -50, отключить звук
                else habitat.volumeControl.setValue(volume); // Иначе установить рассчитанную громкость
                System.out.println("Значение громкости: " + habitat.volumeControl.getValue());
            }
        });

        // Создание кнопки "Назад"
        JButton backButton = new JButton("Назад");
        // Установка минимального и максимального размеров кнопки
        backButton.setMinimumSize(new Dimension(200, 50));
        backButton.setMaximumSize(new Dimension(200, 50));
        // Центрирование кнопки по горизонтали
        backButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        // Добавление слушателя действий для кнопки
        backButton.addActionListener(e -> habitat.setMainMenu()); // Возврат в главное меню

        // Добавление кнопок и отступов между ними
        menu.add(Box.createVerticalGlue()); // Гибкое вертикальное пространство сверху
        menu.add(label); // Добавление метки
        menu.add(Box.createRigidArea(new Dimension(0, 5))); // Фиксированный вертикальный отступ
        menu.add(musicValue); // Добавление слайдера громкости
        menu.add(Box.createRigidArea(new Dimension(0, 10))); // Фиксированный вертикальный отступ
        menu.add(backButton); // Добавление кнопки "Назад"
        menu.add(Box.createVerticalGlue()); // Гибкое вертикальное пространство снизу

        return menu; // Возвращение панели меню
    }
}
