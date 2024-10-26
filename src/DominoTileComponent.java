import javax.swing.*;
import java.awt.*;

// Класс для отображения компонента домино
public class DominoTileComponent extends JComponent {
    private tiles tile; // Доминошка
    private boolean isSelected; // Состояние выбора компонента
    private int degree = 0; // Угол поворота
    private int width = 100; // Ширина доминошки
    private int height = 50; // Высота доминошки

    // Конструктор компонента домино
    public DominoTileComponent(tiles tile, boolean state) {
        this.tile = tile;
        isSelected = state;
        degree = tile.getDegree();
        // Установить размеры в зависимости от угла поворота
        if (degree == 90 || degree == 270 || degree == -90 || degree == -270) {
            setPreferredSize(new Dimension(height, width));
        } else {
            setPreferredSize(new Dimension(width, height));
        }
    }

    // Метод для установки состояния выбора компонента
    public void setSelected(boolean state) {
        isSelected = state;
        repaint();
    }

    // Метод для получения состояния выбора компонента
    public boolean getSelected() {
        return isSelected;
    }

    // Метод для установки угла поворота
    public void setDegree(int value) {
        degree += value;
        // Обновление угла поворота для корректного значения
        if (degree >= 360) degree -= 360;
        if (degree <= -360) degree += 360;
        // Изменение размеров в зависимости от угла поворота
        if (degree == 90 || degree == 270 || degree == -90 || degree == -270) {
            setPreferredSize(new Dimension(height, width));
        } else {
            setPreferredSize(new Dimension(width, height));
        }
        revalidate();
        repaint();
        System.out.println("Degree: " + degree);
    }

    // Метод для получения доминошки
    public tiles getTile() {
        return tile;
    }

    // Переопределенный метод для отрисовки компонента
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int componentWidth = getWidth();
        int componentHeight = getHeight();

        // Смещение точки вращения в центр компонента
        g2d.rotate(Math.toRadians(degree), componentWidth / 2.0, componentHeight / 2.0);

        // Рисование доминошки по центру компонента
        int xOffset = (componentWidth - width) / 2;
        int yOffset = (componentHeight - height) / 2;

        if (isSelected) {
            g2d.setColor(Color.DARK_GRAY);
        } else {
            g2d.setColor(Color.WHITE);
        }
        g2d.fillRect(xOffset, yOffset, width, height);

        g2d.setColor(Color.BLACK);
        g2d.drawRect(xOffset, yOffset, width, height);
        g2d.drawLine(xOffset + width / 2, yOffset, xOffset + width / 2, yOffset + height);

        // Рисование точек на домино
        drawDots(g2d, tile.getFirst(), xOffset, yOffset, width / 2, height);
        drawDots(g2d, tile.getSecond(), xOffset + width / 2, yOffset, width / 2, height);

        g2d.dispose();
    }

    // Метод для рисования точек на доминошке
    private void drawDots(Graphics2D g2d, int value, int x, int y, int width, int height) {
        int dotSize = 10; // Размер точки
        int centerX = x + width / 2; // Центр по X
        int centerY = y + height / 2; // Центр по Y

        // Рисование точек в зависимости от значения
        switch (value) {
            case 1:
                g2d.fillOval(centerX - dotSize / 2, centerY - dotSize / 2, dotSize, dotSize);
                break;
            case 2:
                g2d.fillOval(centerX - width / 4 - dotSize / 2, centerY - height / 4 - dotSize / 2, dotSize, dotSize);
                g2d.fillOval(centerX + width / 4 - dotSize / 2, centerY + height / 4 - dotSize / 2, dotSize, dotSize);
                break;
            case 3:
                g2d.fillOval(centerX - width / 4 - dotSize / 2, centerY - height / 4 - dotSize / 2, dotSize, dotSize);
                g2d.fillOval(centerX - dotSize / 2, centerY - dotSize / 2, dotSize, dotSize);
                g2d.fillOval(centerX + width / 4 - dotSize / 2, centerY + height / 4 - dotSize / 2, dotSize, dotSize);
                break;
            case 4:
                g2d.fillOval(centerX - width / 4 - dotSize / 2, centerY - height / 4 - dotSize / 2, dotSize, dotSize);
                g2d.fillOval(centerX + width / 4 - dotSize / 2, centerY + height / 4 - dotSize / 2, dotSize, dotSize);
                g2d.fillOval(centerX - width / 4 - dotSize / 2, centerY + height / 4 - dotSize / 2, dotSize, dotSize);
                g2d.fillOval(centerX + width / 4 - dotSize / 2, centerY - height / 4 - dotSize / 2, dotSize, dotSize);
                break;
            case 5:
                g2d.fillOval(centerX - width / 4 - dotSize / 2, centerY - height / 4 - dotSize / 2, dotSize, dotSize);
                g2d.fillOval(centerX + width / 4 - dotSize / 2, centerY + height / 4 - dotSize / 2, dotSize, dotSize);
                g2d.fillOval(centerX - width / 4 - dotSize / 2, centerY + height / 4 - dotSize / 2, dotSize, dotSize);
                g2d.fillOval(centerX + width / 4 - dotSize / 2, centerY - height / 4 - dotSize / 2, dotSize, dotSize);
                g2d.fillOval(centerX - dotSize / 2, centerY - dotSize / 2, dotSize, dotSize);
                break;
            case 6:
                g2d.fillOval(centerX - width / 4 - dotSize / 2, centerY - height / 4 - dotSize / 2, dotSize, dotSize);
                g2d.fillOval(centerX + width / 4 - dotSize / 2, centerY + height / 4 - dotSize / 2, dotSize, dotSize);
                g2d.fillOval(centerX - width / 4 - dotSize / 2, centerY + height / 4 - dotSize / 2, dotSize, dotSize);
                g2d.fillOval(centerX + width / 4 - dotSize / 2, centerY - height / 4 - dotSize / 2, dotSize, dotSize);
                g2d.fillOval(centerX - dotSize / 2, centerY - height / 4 - dotSize / 2, dotSize, dotSize);
                g2d.fillOval(centerX - dotSize / 2, centerY + height / 4 - dotSize / 2, dotSize, dotSize);
                break;
        }
    }
}
