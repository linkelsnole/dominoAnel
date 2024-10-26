import com.google.gson.Gson;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;
import javax.sound.sampled.*;
import javax.swing.*;

// Основной класс приложения, отвечающий за логику игры и GUI
public class Habitat extends JPanel implements KeyListener {
    private boolean playMusic = true;
    public boolean dominoChosen = false, showingDomino = false, myTurn = true, playable = true, running = false;
    private String[] music;
    private BufferedImage backgroundImage_small, backgroundImage_big;
    public FloatControl volumeControl;
    public int volume = 100, showingIndex = -1, dominoIndex = -1, myPoints = 0, max_points = 200;
    private JFrame frame;
    private gamePanel gamePanel;
    ArrayList<tiles> tilesQue = new ArrayList<>();
    private List<tiles> playerHand = new ArrayList<>();
    private List<positions> pos = new ArrayList<>();
    private PlayerHandComponent handComponent;
    private Gson gson = new Gson();

    // Основной метод запуска приложения
    public static void main(String[] args) {
        try {
            new Habitat();
        } catch (IOException err) {
            System.out.println(err.getMessage());
        }
    }

    // Конструктор для инициализации игры
    public Habitat() throws IOException {
        // Создание игрового окна и задание его параметров
        frame = new JFrame("Domino");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(626, 470);
        frame.setLayout(new BorderLayout());

        // Загрузка фоновых изображений
        backgroundImage_small = ImageIO.read(new File("/Users/dmitry/Desktop/domino/background.jpg"));
        backgroundImage_big = ImageIO.read(new File("/Users/dmitry/Desktop/domino/background_big.jpg"));
        readAllTiles();

        // Создание главного меню и добавление его в центр окна
        mainMenu menu = new mainMenu();
        frame.add(menu.createMainMenu(backgroundImage_small, this), BorderLayout.CENTER);

        // Выровнять окно по экрану
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setVisible(true);

        // Загрузка музыки и начало воспроизведения
        getAllMusic();
        startMusic();
    }

    // Метод загрузки всех плиток домино в игру
    public void readAllTiles() {
        for (int i = 0; i <= 6; i++) {
            for (int j = i; j <= 6; j++) {
                tiles tile;
                if (i == j) tile = new tiles(i, j, 0, true);
                else tile = new tiles(i, j, 0, false);
                tilesQue.add(tile);
            }
        }
    }

    // Метод загрузки всех музыкальных файлов
    public void getAllMusic() {
        int error = 0, count = 0; // Количество музыкальных треков
        while (error != 1) {
            String audioFilePath = "/Users/dmitry/Desktop/domino/Музяка/track" + String.valueOf(count) + ".wav"; // В папке проходятся все файлы, которые называются track + число
            File audioFile = new File(audioFilePath); // Проверяется существование этого файла
            if (audioFile.exists()) { // Если файл существует
                count++;
            } else {
                error = 1;
            }
        }
        music = new String[count];
        for (int i = 0; i < count; i++) { // Проходится каждый музыкальный трек и сохраняется в массив
            music[i] = "/Users/dmitry/Desktop/domino/Музяка/track" + String.valueOf(i) + ".wav";
        }
    }

    // Метод для воспроизведения музыки на фоне
    public void startMusic() {
        new Thread(() -> {
            int i = 0;
            while (playMusic) {
                if (i >= music.length) i = 0;
                String audioFilePath = music[i];
                playAudioTrack(audioFilePath);
                i++;
            }
        }).start();
    }

    // Метод для воспроизведения конкретного аудиотрека
    public void playAudioTrack(String audioFilePath) {
        try {
            // Открытие аудиофайла
            File audioFile = new File(audioFilePath);
            if (!audioFile.exists()) {
                System.err.println("Аудиофайл не найден: " + audioFilePath);
                return;
            }

            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(audioFile);

            // Получение формата аудиофайла
            AudioFormat audioFormat = audioInputStream.getFormat();

            // Создание объекта DataLine.Info для звуковой карты
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);

            // Получение звуковой карты
            SourceDataLine sourceDataLine = (SourceDataLine) AudioSystem.getLine(info);

            // Открытие звуковой карты для воспроизведения
            sourceDataLine.open(audioFormat);

            // Установка громкости воспроизведения
            if (sourceDataLine.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                volumeControl = (FloatControl) sourceDataLine.getControl(FloatControl.Type.MASTER_GAIN);
                if (volume == 0) volumeControl.setValue(-80);
                else volumeControl.setValue(volume / 2 - 50);
            }

            // Начало воспроизведения
            sourceDataLine.start();

            // Чтение данных из аудиофайла и запись их на звуковую карту
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = audioInputStream.read(buffer, 0, buffer.length)) != -1) {
                sourceDataLine.write(buffer, 0, bytesRead);
            }

            // Завершение воспроизведения
            sourceDataLine.drain();
            sourceDataLine.close();
            audioInputStream.close();
        } catch (UnsupportedAudioFileException ex) {
            System.err.println("Неподдерживаемый аудиофайл: " + ex.getMessage());
        } catch (LineUnavailableException ex) {
            System.err.println("Аудиолиния недоступна: " + ex.getMessage());
        } catch (IOException ex) {
            System.err.println("Ошибка ввода/вывода: " + ex.getMessage());
        }
    }

    public void setMainMenu() {
        // Удалить все компоненты из основного окна
        frame.getContentPane().removeAll();
        // Настроить размер окна и параметры, если необходимо
        if (frame.getSize().width > 900) {
            frame.setSize(626, 470);
            frame.setLocationRelativeTo(null); // Центрировать окно
            frame.dispose();
            frame.setUndecorated(false);
            frame.setVisible(true);
        }
        // Создать новое главное меню и добавить его в центр окна
        mainMenu menu = new mainMenu();
        frame.add(menu.createMainMenu(backgroundImage_small, this), BorderLayout.CENTER);
        // Перерисовать и обновить окно
        frame.revalidate();
        frame.repaint();
    }

    public void setSettingMenu() {
        // Удалить все компоненты из основного окна
        frame.getContentPane().removeAll();
        // Настроить размер окна и параметры, если необходимо
        if (frame.getSize().width > 900) {
            frame.setSize(626, 470);
            frame.setLocationRelativeTo(null); // Центрировать окно
            frame.dispose();
            frame.setUndecorated(false);
            frame.setVisible(true);
        }
        // Создать новое меню настроек и добавить его в центр окна
        settingMenu menu = new settingMenu();
        frame.add(menu.createSettingMenu(backgroundImage_small, this), BorderLayout.CENTER);
        // Перерисовать и обновить окно
        frame.revalidate();
        frame.repaint();
    }

    public void setGameMenu() {
        // Сброс индексов и флагов для новой игры
        showingIndex = -1;
        dominoIndex = -1;
        showingDomino = false;
        dominoChosen = false;
        playerHand.clear(); // Очистить руку игрока
        pos.clear(); // Очистить позиции домино
        tilesQue.addAll(playerHand); // Вернуть взятые домино в очередь
        playerHand.clear(); // Очистить список взятых домино

        // Установить поворот всех домино на 0
        for (tiles tile : tilesQue) {
            tile.setDegree(0);
        }

        // Перемешать все домино
        Collections.shuffle(tilesQue);

        // Раздать домино игроку
        for (int i = 0; i < 7; i++) drawTiles();

        frame.setVisible(false); // Скрыть окно
        frame.getContentPane().removeAll(); // Удалить все компоненты из окна

        // Создать новый компонент руки игрока
        handComponent = new PlayerHandComponent(playerHand, this);
        pos.add(new positions(960, 390, 0, 0)); // Добавить начальную позицию домино

        // Создать и добавить игровую панель
        gamePanel = new gamePanel();
        frame.add(gamePanel.createGamePanel(backgroundImage_big, this, handComponent, playerHand, pos), BorderLayout.CENTER);
        Maximize(); // Развернуть окно
    }

    public tiles drawTiles() {
        // Проверить наличие доступных домино в очереди
        if (tilesQue.size() >= 1) {
            tiles temp = tilesQue.get(0); // Получить первое домино
            playerHand.add(temp); // Добавить его в руку игрока
            tilesQue.remove(0); // Удалить его из очереди
            return temp;
        } else {
            return null; // Если нет домино в очереди, вернуть null
        }
    }

    public void Win() {
        // Отобразить окно с сообщением о победе
        Object[] options = {"OK"};
        JOptionPane.showOptionDialog(
                null,
                "Вы выиграли!", // Содержимое окна
                "Окно победы", // Заголовок окна
                JOptionPane.OK_OPTION, // Тип кнопки
                JOptionPane.PLAIN_MESSAGE, // Тип сообщения
                null,
                options,
                options[0]
        );
    }

    public void Lose() {
        // Отобразить окно с сообщением о проигрыше
        Object[] options = {"OK"};
        JOptionPane.showOptionDialog(
                null,
                "Ходов больше нет!", // Содержимое окна
                "Конец игры", // Заголовок окна
                JOptionPane.OK_OPTION, // Тип кнопки
                JOptionPane.PLAIN_MESSAGE, // Тип сообщения
                null,
                options,
                options[0]
        );
    }

    public void Maximize() {
        // Развернуть окно на весь экран
        frame.setExtendedState(Frame.MAXIMIZED_BOTH);
        frame.dispose();
        frame.setUndecorated(true);
        frame.setVisible(true);
    }

    // Метод для подсчета очков (если необходимо)
    public void countPoints() {
        // Подсчет очков на основе оставшихся домино в руке игрока
        Iterator<tiles> iterator = playerHand.iterator();
        while (iterator.hasNext()) {
            tiles currentTile = iterator.next();
            myPoints += currentTile.getFirst();
            myPoints += currentTile.getSecond();
        }
    }

    // Реализованные методы интерфейса KeyListener
    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        // Обработка нажатий клавиш (если необходимо)
    }

    @Override
    public void keyReleased(KeyEvent e) {}
}
