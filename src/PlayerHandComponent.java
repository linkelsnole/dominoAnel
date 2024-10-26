import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

// Класс компонента руки игрока
public class PlayerHandComponent extends JPanel {
    // Список плиток в руке игрока
    private List<tiles> hand;
    // Список компонентов для отображения плиток
    private List<DominoTileComponent> tiles = new ArrayList<>();
    // Индекс выбранной плитки, -1 означает, что плитка не выбрана
    public int wasSelected = -1;

    // Конструктор компонента руки игрока
    public PlayerHandComponent(List<tiles> hand, Habitat habitat) {
        this.hand = hand; // Инициализация списка плиток
        setLayout(new FlowLayout(FlowLayout.CENTER, 10, 5)); // Установка макета с отступами
        setSize(new Dimension(1620, 200)); // Установка размера компонента
        updateHand(); // Обновление руки (добавление плиток на панель)

        // Добавление слушателя мыши для обработки кликов
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                // Получение индекса плитки, по которой был произведен клик
                int clickedIndex = getClickedTileIndex(e.getX(), e.getY());
                if (clickedIndex != -1) {
                    System.out.println("БЫЛ НАЖАТ: " + clickedIndex);

                    // Если выбрана другая плитка, снять выделение с предыдущей
                    if (wasSelected != clickedIndex && wasSelected != -1) {
                        tiles.get(wasSelected).setSelected(false);
                        wasSelected = -1;
                    }

                    // Изменение состояния выделения текущей плитки
                    boolean state = tiles.get(clickedIndex).getSelected();
                    if (!state) {
                        habitat.dominoChosen = true;
                        habitat.dominoIndex = clickedIndex;
                        wasSelected = clickedIndex;
                    } else {
                        habitat.dominoChosen = false;
                        habitat.dominoIndex = -1;
                        wasSelected = -1;
                    }
                    tiles.get(clickedIndex).setSelected(!state); // Переключение состояния выделения
                    redraw(); // Перерисовка компонента для отображения изменений
                }
            }
        });
    }

    // Метод для определения индекса плитки, по которой был произведен клик
    private int getClickedTileIndex(int mouseX, int mouseY) {
        int x = 10; // Начальная позиция по оси X
        for (int i = 0; i < hand.size(); i++) {
            int size = tiles.get(i).getWidth();
            if (mouseX >= x && mouseX <= x + size &&
                    mouseY >= 5 && mouseY <= 105) {
                return i; // Возвращает индекс плитки
            }
            x += size + 10; // Переход к следующей позиции
        }
        return -1; // Если клик не попал ни на одну плитку, возвращает -1
    }

    // Метод для обновления руки игрока
    public void updateHand() {
        removeAll(); // Удалить все компоненты
        tiles.clear(); // Очистить список компонентов плиток
        for (tiles tile : hand) {
            DominoTileComponent temp = new DominoTileComponent(tile, false); // Создать новый компонент для плитки
            add(temp); // Добавить компонент на панель
            tiles.add(temp); // Добавить компонент в список
        }
        redraw(); // Перерисовка компонента
    }

    // Метод для перерисовки компонента
    public void redraw() {
        revalidate(); // Перепроверить макет
        repaint(); // Перерисовать компонент
    }

    // Метод для поворота плитки на определенный угол
    public void rotateTile(int index, int value) {
        tiles.get(index).setDegree(value); // Установить угол поворота
        redraw(); // Перерисовать компонент
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); // Вызов родительского метода для стандартной перерисовки
        Color gray = new Color(153, 153, 153); // Создание цвета
        g.setColor(gray); // Установка цвета
        g.fillRect(0, 0, 1920, 300); // Заполнение фона компонента серым цветом
    }
}
