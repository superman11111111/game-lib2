package com.mkypr.gl2.client;

import com.mkypr.gl2.middleware.Constants;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.*;

public class GamePane extends JLayeredPane {

    ArrayList<Ship> player1Ships;
    int[][][] player1Grid;
    ArrayList<Ship> player2Ships;
    int[][][] player2Grid;
    int pixelPerBlock;
    int gridSize = 8;
    boolean rotate = false;
    GameState gameState;

    BufferedImage bigShip;
    int[] cPlayer1Tile = new int[]{-1, -1};
    int[] cPlayer2Tile = new int[]{-1, -1};

    static class GameState {
        private static final ArrayList<GameState> allStates = new ArrayList<>();

        public static final GameState INIT = GameState.create();
        public static final GameState PLACING = GameState.create();

        final int state;
        String data;

        protected GameState(int state) {
            this.state = state;
        }

        public static GameState create() {
            GameState gameState = new GameState(allStates.size());
            allStates.add(gameState);
            return gameState;
        }
    }

    static class Ship {
        int x;
        int y;
        boolean r;
        boolean[] h; // health

        protected Ship(int x, int y, boolean r, boolean[] h) {
            this.x = x;
            this.y = y;
            this.r = r;
            this.h = h;
        }

        public static Ship create(int x, int y, boolean rotation, int length) {
            return new Ship(x, y, rotation, new boolean[length]);
        }

        public static ArrayList<int[]> collisions(Ship ship1, Ship ship2) {
            ArrayList<int[]> collisions = new ArrayList<>();
            int[][] tiles = new int[ship1.h.length][2];
            HashSet<int[]> tmp = new HashSet<>();
            int a = ship1.y;
            int b = ship1.x;
            if (ship1.r) {
                a = ship1.x;
                b = ship1.y;
            }
            for (int i = 0; i < ship1.h.length; i++) {
                tiles[i][0] = b;
                tiles[i][1] = a + i;
            }
            Collections.addAll(tmp, tiles);
            tiles = new int[ship1.h.length][2];
            for (int i = 0; i < ship2.h.length; i++) {
                tiles[i][0] = b;
                tiles[i][1] = a + i;
            }
            for (int[] t : tiles) {
                if (tmp.contains(t)) {
                    collisions.add(t);
                }
            }
            return collisions;
        }

        @Override
        public String toString() {
            return "Ship{" +
                    "x=" + x +
                    ", y=" + y +
                    ", r=" + r +
                    ", h=" + Arrays.toString(h) +
                    '}';
        }
    }

    public GamePane() {
        setFocusable(false);
        setLayout(null);
        setOpaque(true);
        player1Ships = new ArrayList<>();
        player1Ships.add(Ship.create(1, 2, true, 4));
        player1Ships.add(Ship.create(5, 2, false, 2));
        player1Grid = new int[gridSize][gridSize][2];
        player2Ships = new ArrayList<>();
        player2Ships.add(Ship.create(1, 1, false, 3));
        player2Grid = new int[gridSize][gridSize][2];

        JPanel p = new JPanel();
        p.setFocusable(false);
        FlowLayout flowLayout = new FlowLayout();
        flowLayout.setVgap(0);
        flowLayout.setAlignment(FlowLayout.LEFT);
        p.setLayout(flowLayout);
        JSlider slider = new JSlider();
        slider.setFocusable(false);
        slider.setMaximum(((Dimension) Settings.getValue("maindim")).height / gridSize);
        slider.setMinimum(slider.getMaximum() / 2);
        slider.setPaintTicks(true);
        slider.setSnapToTicks(true);
        JLabel l = new JLabel(String.valueOf(slider.getValue()));
        l.setFocusable(false);
        l.setFont(Constants.smallFont);
        l.setPreferredSize(new Dimension(80, 40));
        slider.addChangeListener(changeEvent -> l.setText(String.valueOf(slider.getValue())));
        p.add(l);
        p.add(slider);
        try {
            Settings.addSetting("blocksize", slider::getValue, p);
        } catch (Exception e) {
            e.printStackTrace();
        }

        addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent mouseEvent) {

            }

            @Override
            public void mouseMoved(MouseEvent mouseEvent) {
                int[] tileIndex = tile(mouseEvent.getX(), mouseEvent.getY(), player1Grid);
                if (tileIndex != null && tileIndex != cPlayer1Tile) {
                    cPlayer1Tile = tileIndex;
                    repaint();
                } else {
                    tileIndex = tile(mouseEvent.getX(), mouseEvent.getY(), player2Grid);
                    if (tileIndex != null && tileIndex != cPlayer2Tile) {
                        cPlayer2Tile = tileIndex;
                        repaint();
                    }
                }
            }
        });

        addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {

            }

            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                if (cPlayer1Tile != null && cPlayer1Tile[0] > -1 && cPlayer1Tile[1] > -1) {
                    int[] copy = cPlayer1Tile.clone();
                    Ship s = Ship.create(copy[0], copy[1], rotate, Integer.parseInt(gameState.data));
                    for (Ship ss : player1Ships) {
                        if (Ship.collisions(ss, s).isEmpty()) {
                            player1Ships.add(s);
                        }
                    }
                    gameState = GameState.INIT;
                    repaint();
                }
            }

            @Override
            public void mouseReleased(MouseEvent mouseEvent) {

            }

            @Override
            public void mouseEntered(MouseEvent mouseEvent) {

            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {

            }
        });

        Texture texture = new Texture("/textures/ship.png");
        bigShip = texture.image.getSubimage(4, 4, 64, 64 * 4);

        InputMap inputMap = getInputMap(WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = getActionMap();
        HashMap<String, Integer> keys = new HashMap<>();
        keys.put("1", KeyEvent.VK_1);
        keys.put("2", KeyEvent.VK_2);
        keys.put("3", KeyEvent.VK_3);
        keys.put("4", KeyEvent.VK_4);
        for (String k : keys.keySet()) {
            KeyStroke ks = KeyStroke.getKeyStroke(keys.get(k), 0, false);
            inputMap.put(ks, k);
            actionMap.put(k, new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    gameState = GameState.PLACING;
                    gameState.data = k;
                    repaint();
                }
            });
        }
        KeyStroke ks = KeyStroke.getKeyStroke(KeyEvent.VK_E, 0, false);
        inputMap.put(ks, "E");
        actionMap.put("E", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (gameState == GameState.PLACING) {
                    rotate = !rotate;
                    repaint();
                }
            }
        });

        gameState = GameState.INIT;
    }

    private int[] tile(int x, int y, int[][][] grid) {
        for (int i = 0; i < grid.length; i++) {
            int[][] row = grid[i];
            if (row[0][0] > x) return null;
            if (row[0][1] > y) return null;
            if (row[0][0] + pixelPerBlock > x) {
                for (int j = 0; j < row.length; j++) {
                    int[] t = row[j];
                    if (t[1] + pixelPerBlock > y) {
                        return new int[] {i, j};
                    }
                }
            }
        }
        return null;
    }

    private void paintShips(Graphics2D g2d, ArrayList<Ship> ships, int offsetX) {
        for (Ship s : ships) {
            AffineTransform t = new AffineTransform();
            Image im = bigShip.getScaledInstance(pixelPerBlock + 1, s.h.length * (pixelPerBlock + 1), BufferedImage.TYPE_INT_RGB);
            t.translate(s.x * (pixelPerBlock + 1) + offsetX, s.y * (pixelPerBlock + 1));
            if (s.r) {
                t.translate(0, pixelPerBlock);
                t.rotate(Math.toRadians(270));
            }
            g2d.drawImage(im, t, this);
        }
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setFont(Constants.smallFont);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(Color.black);
        pixelPerBlock = (int) Settings.getValue("blocksize");
        int player2Offset = (gridSize + 1) * (pixelPerBlock + 1);
        paintShips(g2d, player1Ships, 0);
        paintShips(g2d, player2Ships, player2Offset);
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                player1Grid[i][j][0] = i * (pixelPerBlock + 1);
                player1Grid[i][j][1] = j * (pixelPerBlock + 1);
                player2Grid[i][j][0] = player1Grid[i][j][0] + player2Offset;
                player2Grid[i][j][1] = player1Grid[i][j][1];
                if (cPlayer1Tile[0] == i && cPlayer1Tile[1] == j) {
                    g2d.setColor(Color.red);
                    if (gameState == GameState.PLACING) {
                        g2d.fillRect(player1Grid[i][j][0], player1Grid[i][j][1], pixelPerBlock + 1, pixelPerBlock + 1);
                        g2d.setColor(Color.black);
                        g2d.drawString(gameState.data, player1Grid[i][j][0] + 1, player1Grid[i][j][1] + pixelPerBlock);
                    } else {
                        g2d.drawRect(player1Grid[i][j][0], player1Grid[i][j][1], pixelPerBlock, pixelPerBlock);
                    }
                    g2d.setColor(Color.black);
                } else {
                    g2d.drawRect(player1Grid[i][j][0], player1Grid[i][j][1], pixelPerBlock, pixelPerBlock);
                }
                if (cPlayer2Tile[0] == i && cPlayer2Tile[1] == j) {
                    g2d.setColor(Color.green);
                    g2d.drawRect(player2Grid[i][j][0], player2Grid[i][j][1], pixelPerBlock, pixelPerBlock);
                    g2d.setColor(Color.black);
                } else {
                    g2d.drawRect(player2Grid[i][j][0], player2Grid[i][j][1], pixelPerBlock, pixelPerBlock);
                }
            }
        }
        g2d.dispose();
    }
}
