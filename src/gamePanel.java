import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.*;

// Класс для управления игровым полем, включая позиции и отрисовку
public class gamePanel {
    List<positions> pos; // Список позиций
    List<positions> allEverPositions = new ArrayList<>(); // Список всех когда-либо используемых позиций
    JPanel centerGamePanel; // Панель для центральной игровой зоны
    JLabel turnIndicator, myScore; // Метки для индикатора хода и счета игрока

    // Метод для создания игровой панели
    JPanel createGamePanel(BufferedImage myPicture, Habitat habitat, PlayerHandComponent handComponent, List<tiles> playerHand, List<positions> pos) {
        this.pos = pos;
        allEverPositions.add(pos.get(0)); // Добавить начальную позицию в список всех позиций
        JPanel game = new JPanel(); // Создать игровую панель
        game.setLayout(new BorderLayout()); // Установить BorderLayout для игровой панели

        // Создать верхнюю серую панель
        JPanel topGrayPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Color gray = new Color(153, 153, 153);
                g.setColor(gray);
                g.fillRect(0, 0, getWidth(), 150); // Заполнить серым цветом
            }
        };
        topGrayPanel.setPreferredSize(new Dimension(1920, 150)); // Установить размер верхней серой панели

        JPanel scorePanel = new JPanel(); // Панель для отображения счета
        scorePanel.setOpaque(false); // Прозрачный фон
        myScore = new JLabel("Счет: 0"); // Метка для отображения счета игрока
        turnIndicator = new JLabel("Ваш ход"); // Метка для индикатора хода
        scorePanel.add(myScore); // Добавить метку счета на панель счета
        scorePanel.add(turnIndicator); // Добавить метку индикатора хода на панель счета
        topGrayPanel.add(scorePanel, BorderLayout.CENTER); // Добавить панель счета на верхнюю серую панель

        // Создать центральную игровую зону
        centerGamePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(myPicture, 0, 0, this); // Отобразить изображение на игровой панели
                // Для отладки позиций (опционально)
                /*
                g.setColor(Color.RED);
                for (positions pos : pos) {
                    g.fillRect(pos.x - 5, pos.y - 5, 10, 10); // Нарисовать прямоугольники для представления позиций
                }
                */
            }
        };
        centerGamePanel.setPreferredSize(new Dimension(1920, 780)); // Установить размер центральной игровой зоны
        centerGamePanel.setLayout(null); // Установить нулевой макет для ручного размещения компонентов

        // Создать нижнюю серую панель
        JPanel bottomGrayPanel = new JPanel(new BorderLayout());
        bottomGrayPanel.setPreferredSize(new Dimension(1920, 150)); // Установить размер нижней серой панели

        // Создать панель кнопок
        JPanel buttonPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Color gray = new Color(153, 153, 153);
                g.setColor(gray);
                g.fillRect(0, 0, getWidth(), getHeight()); // Заполнить серым цветом
            }
        };
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS)); // Установить BoxLayout для панели кнопок
        buttonPanel.add(Box.createVerticalGlue()); // Добавить вертикальный клей

        // Создать кнопку "Назад"
        JButton backButton = new JButton("Назад");
        backButton.setMinimumSize(new Dimension(200, 50)); // Установить размер кнопки
        backButton.setMaximumSize(new Dimension(200, 50));
        backButton.setAlignmentX(Component.CENTER_ALIGNMENT); // Центрировать по горизонтали
        backButton.setFocusable(false); // Отключить фокус
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                habitat.setMainMenu(); // Вернуться в главное меню
            }
        });

        // Создать кнопку "Взять плитку"
        JButton drawButton = new JButton("Взять плитку");
        drawButton.setMaximumSize(new Dimension(200, 50)); // Установить размер кнопки
        drawButton.setAlignmentX(Component.CENTER_ALIGNMENT); // Центрировать по горизонтали
        drawButton.setFocusable(false); // Отключить фокус
        drawButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Позволить игроку взять плитку, если его ход
                if (habitat.myTurn) {
                    tiles drawnTile = habitat.drawTiles();  // Взять плитку из стопки
                    if (drawnTile != null) {
                        handComponent.updateHand();
                        habitat.dominoIndex = -1;
                        habitat.dominoChosen = false;
                        habitat.playable = isAnyMoves(playerHand);  // Обновить статус доступности ходов

                        if (!habitat.playable) {
                            if (habitat.tilesQue.isEmpty()) {
                                habitat.Lose();  // Закончить игру, если нет плиток для взятия
                            } else {
                                ChangeTurnState("Нет ходов, возьмите ещё одну плитку.");
                            }
                        } else {
                            ChangeTurnState("Ваш ход.");
                        }
                    } else {
                        habitat.Lose();  // Закончить игру, если нет плиток для взятия
                    }
                }
            }
        });

        // Создать кнопку "Повернуть на 90°"
        JButton rotateButton = new JButton("Повернуть на 90°");
        rotateButton.setMinimumSize(new Dimension(200, 50)); // Установить размер кнопки
        rotateButton.setMaximumSize(new Dimension(200, 50));
        rotateButton.setAlignmentX(Component.CENTER_ALIGNMENT); // Центрировать по горизонтали
        rotateButton.setFocusable(false); // Отключить фокус
        rotateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Проверить, выбрана ли плитка для вращения
                if (habitat.dominoChosen) {
                    handComponent.rotateTile(habitat.dominoIndex, 90);
                    playerHand.get(habitat.dominoIndex).addDegree(90);
                }
            }
        });

        // Добавить кнопки на панель кнопок
        buttonPanel.add(backButton); // Кнопка "Назад"
        buttonPanel.add(drawButton); // Кнопка "Взять плитку"
        buttonPanel.add(rotateButton); // Кнопка "Повернуть на 90°"

        // Создать компонент руки игрока и добавить его на игровое поле
        JPanel handPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Color gray = new Color(153, 153, 153);
                g.setColor(gray);
                g.fillRect(0, 0, getWidth(), 300); // Заполнить серым цветом
            }
        };
        handPanel.setPreferredSize(new Dimension(1620, 200)); // Установить размер панели руки
        handPanel.add(handComponent); // Добавить компонент руки игрока

        bottomGrayPanel.add(buttonPanel, BorderLayout.WEST); // Добавить панель кнопок слева от нижней серой панели
        bottomGrayPanel.add(handPanel, BorderLayout.CENTER); // Добавить панель руки в центр нижней серой панели

        // Добавить верхнюю серую панель, центральную игровую зону и нижнюю серую панель на игровое поле
        game.add(topGrayPanel, BorderLayout.NORTH); // Добавить верхнюю серую панель наверх игровой панели
        game.add(centerGamePanel, BorderLayout.CENTER); // Добавить центральную игровую зону в центр игровой панели
        game.add(bottomGrayPanel, BorderLayout.SOUTH); // Добавить нижнюю серую панель вниз игровой панели

        // Добавить слушатель движения мыши к центральной игровой зоне
        centerGamePanel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                // Проверить, выбрана ли доминошка и это ход игрока
                if (habitat.dominoChosen && habitat.myTurn) {
                    double min_len = 101;  // Минимальная дистанция для нахождения ближайшей позиции
                    int nearestIndex = -1;  // Индекс ближайшей позиции

                    // Найти ближайшую позицию к текущей позиции мыши
                    for (int i = 0; i < pos.size(); i++) {
                        int dx = pos.get(i).x - e.getX();  // Разница по X между позицией и мышью
                        int dy = pos.get(i).y - e.getY();  // Разница по Y между позицией и мышью
                        double len = Math.sqrt(dx * dx + dy * dy);  // Рассчитать евклидово расстояние

                        // Если расстояние меньше или равно 100 и оно меньше предыдущего минимума
                        if (len <= 100 && len < min_len) {
                            nearestIndex = i;  // Запомнить индекс ближайшей позиции
                            min_len = len;  // Обновить минимальную длину
                        }
                    }

                    // Если подходящее место для домино найдено
                    if (nearestIndex != -1) {
                        positions currentPos = pos.get(nearestIndex);  // Получить ближайшую позицию
                        DominoTileComponent dominoTileComponent_temp_1 = new DominoTileComponent(playerHand.get(habitat.dominoIndex), true);  // Создать компонент домино
                        tiles tile = playerHand.get(habitat.dominoIndex);  // Получить текущую домино из руки игрока
                        int first = tile.getFirst(), second = tile.getSecond();  // Получить значения домино
                        int degree = playerHand.get(habitat.dominoIndex).getDegree();  // Получить поворот домино
                        boolean isHorizontal = (degree == 0 || degree == 180 || degree == -180);  // Проверить, является ли домино горизонтальным
                        boolean invers = (degree == -90 || degree == 270 || degree == 180 || degree == -180);  // Проверить, является ли домино перевернутым
                        boolean isDooble = playerHand.get(habitat.dominoIndex).getDooble();  // Проверить, является ли домино двойным

                        // Создать прямоугольник для размещения домино на основе вычисленных параметров
                        Rectangle place_rect = createRect(currentPos, isHorizontal, isDooble, invers, first, second, null, null, e.getX(), e.getY());

                        // Если место для размещения домино корректное
                        if (place_rect != null) {
                            // Проверка на коллизии с другими доминошками
                            if (checkForMultiplyCollisions(place_rect, isHorizontal) == 1) {
                                dominoTileComponent_temp_1.setBounds(place_rect);  // Установить границы для компонента домино
                            } else {
                                DeleteTip(habitat);  // Удалить временное отображение домино
                                return;
                            }
                        } else {
                            DeleteTip(habitat);  // Удалить временное отображение домино
                            return;
                        }

                        // Если домино уже отображается
                        if (habitat.showingDomino) {
                            centerGamePanel.remove(centerGamePanel.getComponentCount() - 1);  // Удалить предыдущее отображаемое домино
                        }

                        habitat.showingIndex = nearestIndex;  // Обновить индекс отображаемого домино
                        habitat.showingDomino = true;  // Обновить статус отображаемого домино

                        centerGamePanel.add(dominoTileComponent_temp_1);  // Добавить новое домино на панель
                        centerGamePanel.revalidate();  // Перепроверить панель для обновления отображения
                        centerGamePanel.repaint();  // Перерисовать панель
                    } else {
                        DeleteTip(habitat);  // Удалить временное отображение домино
                    }
                }
            }
        });

        // Добавить слушатель мыши к центральной игровой зоне
        centerGamePanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                // Проверить, отображается ли домино, это ход игрока, и выбрана ли позиция
                if (habitat.showingDomino && habitat.myTurn && habitat.showingIndex != -1) {
                    // Снять выбор с компонента домино
                    ((DominoTileComponent) centerGamePanel.getComponent(centerGamePanel.getComponentCount() - 1)).setSelected(false);

                    // Если у игрока осталась одна доминошка
                    if (playerHand.size() == 1) {
                        playerHand.remove(habitat.dominoIndex);  // Удалить домино из руки игрока
                        habitat.Win();  // Вызвать метод победы игрока
                        return;
                    }

                    // Получить текущую домино и её границы
                    tiles tile = ((DominoTileComponent) centerGamePanel.getComponent(centerGamePanel.getComponentCount() - 1)).getTile();
                    Rectangle rect = ((DominoTileComponent) centerGamePanel.getComponent(centerGamePanel.getComponentCount() - 1)).getBounds(null);
                    int degree = playerHand.get(habitat.dominoIndex).getDegree();  // Получить поворот домино
                    int place_x = pos.get(habitat.showingIndex).x, place_y = pos.get(habitat.showingIndex).y;  // Координаты позиции размещения
                    int left = playerHand.get(habitat.dominoIndex).getFirst(), right = playerHand.get(habitat.dominoIndex).getSecond();  // Значения домино

                    pos.remove(habitat.showingIndex);  // Удалить использованную позицию
                    habitat.showingDomino = false;  // Обновить статус отображаемого домино
                    habitat.showingIndex = -1;  // Сбросить индекс отображаемого домино

                    // Разместить домино на игровом поле
                    Place(rect, left, right, degree, place_x, place_y);
                    drawDomino(rect, tile);  // Нарисовать домино на игровом поле

                    playerHand.remove(habitat.dominoIndex);  // Удалить домино из руки игрока
                    habitat.dominoIndex = -1;  // Сбросить индекс выбранного домино
                    habitat.dominoChosen = false;  // Обновить статус выбранного домино
                    handComponent.wasSelected = -1;  // Сбросить статус выбора в компоненте руки
                    handComponent.updateHand();  // Обновить отображение руки игрока
                    centerGamePanel.revalidate();  // Перепроверить панель
                    centerGamePanel.repaint();  // Перерисовать панель

                    // Обновить статус доступности ходов
                    habitat.playable = isAnyMoves(playerHand);  // Проверить наличие доступных ходов

                    if (!habitat.playable) {
                        if (habitat.tilesQue.isEmpty()) {
                            habitat.Lose();  // Закончить игру, если нет плиток для взятия
                        } else {
                            ChangeTurnState("Нет доступных ходов, возьмите плитку.");
                        }
                    } else {
                        ChangeTurnState("Ваш ход.");
                    }
                }
            }
        });
        game.setFocusable(true); // Позволить игре получать фокус для чтения ввода с клавиатуры
        game.requestFocusInWindow(); // Запросить фокус в текущем окне
        game.addKeyListener(new KeyAdapter() { // Обработчик ввода с клавиатуры
            @Override
            public void keyPressed(KeyEvent e) {
                if ((e.getKeyChar() == 'Q' || e.getKeyChar() == 'q') && habitat.dominoChosen) {
                    handComponent.rotateTile(habitat.dominoIndex, -90); // Поворот против часовой стрелки
                    playerHand.get(habitat.dominoIndex).addDegree(-90);
                }
                if ((e.getKeyChar() == 'E' || e.getKeyChar() == 'e') && habitat.dominoChosen) {
                    handComponent.rotateTile(habitat.dominoIndex, 90); // Поворот по часовой стрелке
                    playerHand.get(habitat.dominoIndex).addDegree(90);
                }
            }
        });
        return game;
    }

    public void ChangeTurnState(String str) {
        turnIndicator.setText(str); // Изменить статус хода
    }

    public void Redraw() {
        centerGamePanel.revalidate(); // Перерисовать игровое поле
        centerGamePanel.repaint();
    }

    public void ChangePoints(int left) {
        myScore.setText("Счет: " + left); // Обновить счет игрока
    }

    public void drawDomino(Rectangle rect, tiles tile) { // Нарисовать домино
        DominoTileComponent dominoTileComponent_temp_1 = new DominoTileComponent(tile, false);
        dominoTileComponent_temp_1.setBounds(rect);
        centerGamePanel.add(dominoTileComponent_temp_1);
    }

    // Метод для размещения домино на игровом поле
    public void Place(Rectangle rect, int left, int right, int degree, int place_x, int place_y) {
        int x = rect.x;
        int y = rect.y;

        if (degree == -90 || degree == 270 || degree == 180 || degree == -180) {
            int temp_val = left;
            left = right;
            right = temp_val;
        }

        degree = Math.abs(degree);
        boolean isHorizontal = (degree == 0 || degree == 180);
        boolean isVertical = (degree == 90 || degree == 270);

        List<positions> uncheckedPoses = new ArrayList<>();

        if (isHorizontal) {
            uncheckedPoses.add(new positions(x + 25, y, left, 1));
            uncheckedPoses.add(new positions(x, y + 25, left, 3));
            uncheckedPoses.add(new positions(x + 25, y + 50, left, 2));
            uncheckedPoses.add(new positions(x + 75, y, right, 1));
            uncheckedPoses.add(new positions(x + 100, y + 25, right, 4));
            uncheckedPoses.add(new positions(x + 75, y + 50, right, 2));
        } else if (isVertical) {
            uncheckedPoses.add(new positions(x, y + 25, left, 3));
            uncheckedPoses.add(new positions(x + 25, y, left, 1));
            uncheckedPoses.add(new positions(x + 50, y + 25, left, 4));
            uncheckedPoses.add(new positions(x, y + 75, right, 3));
            uncheckedPoses.add(new positions(x + 25, y + 100, right, 2));
            uncheckedPoses.add(new positions(x + 50, y + 75, right, 4));
        }

        Iterator<positions> iterator = uncheckedPoses.iterator();
        while (iterator.hasNext()) {
            positions uncheckedPos = iterator.next();
            if (uncheckedPos.x == place_x && uncheckedPos.y == place_y) {
                iterator.remove();
                break;
            }
        }

        allEverPositions.addAll(uncheckedPoses);
        removeWrongPoses(uncheckedPoses);
        pos.addAll(uncheckedPoses);
        removeImpossiblePositions();
    }

    // Метод для проверки доступных ходов
    public boolean isAnyMoves(List<tiles> playerHand) {
        boolean isPossibleToPlay = false;
        Iterator<positions> iterator = pos.iterator();
        while (iterator.hasNext()) {
            positions currentPos = iterator.next();
            Iterator<tiles> iterator_hand = playerHand.iterator();
            while (iterator_hand.hasNext()) {
                tiles currentTile = iterator_hand.next();
                if (currentPos.value == currentTile.getFirst() || currentPos.value == currentTile.getSecond()) {
                    isPossibleToPlay = true;
                    break;
                }
            }
            if (isPossibleToPlay) break;
        }
        return isPossibleToPlay;
    }

    public void DeleteTip(Habitat habitat) {
        if (habitat.showingDomino) {
            habitat.showingIndex = -1;
            habitat.showingDomino = false;
            centerGamePanel.remove(centerGamePanel.getComponentCount() - 1);
            Redraw();
        }
    }

    // Метод для проверки множественных коллизий
    public int checkForMultiplyCollisions(Rectangle place_rect, boolean isHorizontal) {
        int collisionCount = 0;  // Инициализация счетчика коллизий
        int x = place_rect.x;  // Получение координаты x из прямоугольника
        int y = place_rect.y;  // Получение координаты y из прямоугольника

        Iterator<positions> iterator = allEverPositions.iterator();  // Создание итератора для всех позиций
        while (iterator.hasNext()) {
            positions poses = iterator.next();  // Получение следующей позиции

            // Проверка коллизий для горизонтального домино
            if (isHorizontal) {
                if (poses.x == x + 25 && poses.y == y) collisionCount++;  // Проверка и увеличение счетчика при коллизии
                if (poses.x == x + 75 && poses.y == y) collisionCount++;
                if (poses.x == x && poses.y == y + 25) collisionCount++;
                if (poses.x == x + 100 && poses.y == y + 25) collisionCount++;
                if (poses.x == x + 25 && poses.y == y + 50) collisionCount++;
                if (poses.x == x + 75 && poses.y == y + 50) collisionCount++;

                if (poses.x == x && poses.y == y) collisionCount++;
                if (poses.x == x + 100 && poses.y == y) collisionCount++;
                if (poses.x == x && poses.y == y + 50) collisionCount++;
                if (poses.x == x + 100 && poses.y == y + 50) collisionCount++;

                if (poses.x == x + 50 && poses.y == y) collisionCount++;
                if (poses.x == x + 50 && poses.y == y + 50) collisionCount++;

                if (poses.x == x + 50 && poses.y == y + 25) collisionCount++;
            }
            // Проверка коллизий для вертикального домино
            else {
                if (poses.x == x + 25 && poses.y == y) collisionCount++;
                if (poses.x == x && poses.y == y + 25) collisionCount++;
                if (poses.x == x + 50 && poses.y == y + 25) collisionCount++;
                if (poses.x == x && poses.y == y + 75) collisionCount++;
                if (poses.x == x + 50 && poses.y == y + 75) collisionCount++;
                if (poses.x == x + 25 && poses.y == y + 100) collisionCount++;

                if (poses.x == x && poses.y == y) collisionCount++;
                if (poses.x == x + 50 && poses.y == y) collisionCount++;
                if (poses.x == x && poses.y == y + 100) collisionCount++;
                if (poses.x == x + 50 && poses.y == y + 100) collisionCount++;

                if (poses.x == x && poses.y == y + 50) collisionCount++;
                if (poses.x == x + 50 && poses.y == y + 50) collisionCount++;

                if (poses.x == x + 25 && poses.y == y + 50) collisionCount++;
            }
        }

        return collisionCount;  // Возврат общего числа коллизий
    }

    public Rectangle createRect(positions currentPos, boolean isHorizontal, boolean isDooble, boolean invers, int first, int second, MouseEvent e, Rectangle place_rect, int x, int y) {
        // Проверка типа текущей позиции и создание соответствующего прямоугольника
        switch (currentPos.type) {
            case 1: // Верхняя позиция
                if (isHorizontal) {
                    if (!isDooble) {
                        int pos = CheckAviability(currentPos, 0, x);  // Проверка доступности позиции
                        if (pos == 0) {
                            int connecting = invers ? first : second;
                            if (currentPos.value == connecting) place_rect = new Rectangle(currentPos.x - 75, currentPos.y - 50, 100, 50);  // Создание прямоугольника для левой половины
                            else return null;  // Возврат null, если значения не совпадают
                        } else if (pos == 1) {
                            int connecting = invers ? second : first;
                            if (currentPos.value == connecting) place_rect = new Rectangle(currentPos.x - 25, currentPos.y - 50, 100, 50);  // Создание прямоугольника для правой половины
                            else return null;
                        }
                    } else if (currentPos.value == first) place_rect = new Rectangle(currentPos.x - 50, currentPos.y - 50, 100, 50);  // Создание прямоугольника для двойного домино
                } else {
                    int connecting = invers ? first : second;
                    if (currentPos.value == connecting) place_rect = new Rectangle(currentPos.x - 25, currentPos.y - 100, 50, 100);  // Создание вертикального прямоугольника
                    else return null;
                }
                break;
            case 2: // Нижняя позиция
                if (isHorizontal) {
                    if (!isDooble) {
                        int pos = CheckAviability(currentPos, 0, x);
                        if (pos == 0) {
                            int connecting = invers ? first : second;
                            if (currentPos.value == connecting) place_rect = new Rectangle(currentPos.x - 75, currentPos.y, 100, 50);
                            else return null;
                        } else if (pos == 1) {
                            int connecting = invers ? second : first;
                            if (currentPos.value == connecting) place_rect = new Rectangle(currentPos.x - 25, currentPos.y, 100, 50);
                            else return null;
                        }
                    } else if (currentPos.value == first) place_rect = new Rectangle(currentPos.x - 50, currentPos.y, 100, 50);
                } else {
                    int connecting = invers ? second : first;
                    if (currentPos.value == connecting) place_rect = new Rectangle(currentPos.x - 25, currentPos.y, 50, 100);
                    else return null;
                }
                break;
            case 3: // Левая позиция
                if (isHorizontal) {
                    int connecting = invers ? first : second;
                    if (currentPos.value == connecting) place_rect = new Rectangle(currentPos.x - 100, currentPos.y - 25, 100, 50);
                    else return null;
                } else {
                    if (!isDooble) {
                        int pos = CheckAviability(currentPos, 1, y);
                        if (pos == 0) {
                            int connecting = invers ? first : second;
                            if (currentPos.value == connecting) place_rect = new Rectangle(currentPos.x - 50, currentPos.y - 75, 50, 100);
                            else return null;
                        } else if (pos == 1) {
                            int connecting = invers ? second : first;
                            if (currentPos.value == connecting) place_rect = new Rectangle(currentPos.x - 50, currentPos.y - 25, 50, 100);
                            else return null;
                        }
                    } else if (currentPos.value == first) place_rect = new Rectangle(currentPos.x - 50, currentPos.y - 50, 50, 100);
                }
                break;
            case 4: // Правая позиция
                if (isHorizontal) {
                    int connecting = invers ? second : first;
                    if (currentPos.value == connecting) place_rect = new Rectangle(currentPos.x, currentPos.y - 25, 100, 50);
                    else return null;
                } else {
                    if (!isDooble) {
                        int pos = CheckAviability(currentPos, 1, y);
                        if (pos == 0) {
                            int connecting = invers ? first : second;
                            if (currentPos.value == connecting) place_rect = new Rectangle(currentPos.x, currentPos.y - 75, 50, 100);
                            else return null;
                        } else if (pos == 1) {
                            int connecting = invers ? second : first;
                            if (currentPos.value == connecting) place_rect = new Rectangle(currentPos.x, currentPos.y - 25, 50, 100);
                            else return null;
                        }
                    } else if (currentPos.value == first) place_rect = new Rectangle(currentPos.x, currentPos.y - 50, 50, 100);
                }
                break;
            default: // Универсальная позиция (0)
                if (isHorizontal) {
                    place_rect = new Rectangle(currentPos.x - 50, currentPos.y - 25, 100, 50);  // Создание горизонтального прямоугольника
                } else {
                    place_rect = new Rectangle(currentPos.x - 25, currentPos.y - 50, 50, 100);  // Создание вертикального прямоугольника
                }
                break;
        }
        return place_rect;  // Возврат созданного прямоугольника
    }

    public void removeWrongPoses(List<positions> uncheckedPositions) {
        Iterator<positions> iterator = uncheckedPositions.iterator();  // Создание итератора для списка непроверенных позиций
        while (iterator.hasNext()) {  // Проход по всем непроверенным позициям
            positions uncheckedPos = iterator.next();  // Получение следующей позиции
            Iterator<positions> iterator_inner = allEverPositions.iterator();  // Создание итератора для всех когда-либо проверенных позиций
            boolean found = false;  // Флаг для отслеживания найденных неправильных позиций
            while (iterator_inner.hasNext()) {  // Проход по всем когда-либо проверенным позициям
                positions poses = iterator_inner.next();  // Получение следующей позиции
                // Проверка типа текущей позиции и удаление неправильных позиций
                switch (uncheckedPos.type) {
                    case 1:  // Тип 1: Верхняя позиция
                        if ((poses.x == uncheckedPos.x - 25 && poses.y == uncheckedPos.y - 25) || (poses.x == uncheckedPos.x + 25 && poses.y == uncheckedPos.y - 25) || (poses.x == uncheckedPos.x && poses.y == uncheckedPos.y - 25)) {
                            int index = pos.indexOf(poses);  // Получение индекса позиции в основном списке позиций
                            if (index != -1) pos.remove(index);  // Удаление позиции, если она найдена
                            found = true;  // Установка флага найденной позиции
                        }
                        break;
                    case 2:  // Тип 2: Нижняя позиция
                        if ((poses.x == uncheckedPos.x - 25 && poses.y == uncheckedPos.y + 25) || (poses.x == uncheckedPos.x + 25 && poses.y == uncheckedPos.y + 25) || (poses.x == uncheckedPos.x && poses.y == uncheckedPos.y + 25)) {
                            int index = pos.indexOf(poses);
                            if (index != -1) pos.remove(index);
                            found = true;
                        }
                        break;
                    case 3:  // Тип 3: Левая позиция
                        if ((poses.x == uncheckedPos.x - 25 && poses.y == uncheckedPos.y - 25) || (poses.x == uncheckedPos.x - 25 && poses.y == uncheckedPos.y + 25) || (poses.x == uncheckedPos.x - 25 && poses.y == uncheckedPos.y)) {
                            int index = pos.indexOf(poses);
                            if (index != -1) pos.remove(index);
                            found = true;
                        }
                        break;
                    case 4:  // Тип 4: Правая позиция
                        if ((poses.x == uncheckedPos.x + 25 && poses.y == uncheckedPos.y - 25) || (poses.x == uncheckedPos.x + 25 && poses.y == uncheckedPos.y + 25) || (poses.x == uncheckedPos.x + 25 && poses.y == uncheckedPos.y)) {
                            int index = pos.indexOf(poses);
                            if (index != -1) pos.remove(index);
                            found = true;
                        }
                        break;
                }
            }
            if (found) iterator.remove();  // Удаление непроверенной позиции, если найдена неправильная позиция
        }
    }

    // Метод для проверки доступности позиции
    public int CheckAviability(positions position, int type, int mousePos) {
        int situation = 0;  // Инициализация переменной ситуации

        // Проверка типа и установка ситуации в зависимости от позиции мыши
        if (type == 0) {
            if(mousePos < position.x) situation = 0;  // Если мышь слева от позиции, ситуация 0
            else situation = 1;  // Если мышь справа от позиции, ситуация 1
        } else {
            if(mousePos < position.y) situation = 0;  // Если мышь выше позиции, ситуация 0
            else situation = 1;  // Если мышь ниже позиции, ситуация 1
        }

        return situation;  // Возврат значения ситуации
    }

    // Метод для удаления невозможных позиций из основного списка позиций
    public void removeImpossiblePositions() {
        Iterator<positions> iterator = pos.iterator();  // Создание итератора для основного списка позиций
        while (iterator.hasNext()) {  // Проход по всем позициям
            positions currentPos = iterator.next();  // Получение следующей позиции
            boolean placable = false;  // Флаг для отслеживания возможных для размещения позиций
            // Проверка типа текущей позиции и удаление невозможных позиций
            if (currentPos.type == 1 || currentPos.type == 2) {
                Rectangle place_rect = createRect(currentPos, true, false, false, currentPos.value, currentPos.value, null, null, currentPos.x - 10, 10);
                int collisionCount = checkForMultiplyCollisions(place_rect, true);
                if (place_rect != null) {
                    if ((collisionCount == 1)) placable = true;
                }
                place_rect = createRect(currentPos, true, false, false, currentPos.value, currentPos.value, null, null, currentPos.x + 10, 10);
                collisionCount = checkForMultiplyCollisions(place_rect, true);
                if (place_rect != null) {
                    if ((collisionCount == 1)) placable = true;
                }
                place_rect = createRect(currentPos, false, false, false, currentPos.value, currentPos.value, null, null, currentPos.x - 10, 10);
                collisionCount = checkForMultiplyCollisions(place_rect, false);
                if (place_rect != null) {
                    if ((collisionCount == 1)) placable = true;
                }
            } else {
                Rectangle place_rect = createRect(currentPos, true, false, false, currentPos.value, currentPos.value, null, null, currentPos.x - 10, 10);
                int collisionCount = checkForMultiplyCollisions(place_rect, true);
                if (place_rect != null) {
                    if ((collisionCount == 1)) placable = true;
                }
                place_rect = createRect(currentPos, false, false, false, currentPos.value, currentPos.value, null, null, currentPos.x - 10, 10 - 10);
                collisionCount = checkForMultiplyCollisions(place_rect, false);
                if (place_rect != null) {
                    if ((collisionCount == 1)) placable = true;
                }
                place_rect = createRect(currentPos, false, false, false, currentPos.value, currentPos.value, null, null, currentPos.x - 10, 10 + 10);
                collisionCount = checkForMultiplyCollisions(place_rect, false);
                if (place_rect != null) {
                    if ((collisionCount == 1)) placable = true;
                }
            }
            if (!placable) {
                iterator.remove();  // Удаление позиции, если она невозможна для размещения
            }
        }
    }
}
