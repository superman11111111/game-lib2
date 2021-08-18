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
    Color normalColor;
    Color hoverColor;
    Color letterColor;

    BufferedImage[] shipsImg;
    BufferedImage markedOverlay;
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
        boolean marked;

        protected Ship(int x, int y, boolean r, boolean[] h) {
            this.x = x;
            this.y = y;
            this.r = r;
            this.h = h;
        }

        public static Ship create(int x, int y, boolean rotation, int length) {
            return new Ship(x, y, rotation, new boolean[length]);
        }

        public int length() {
            return this.h.length;
        }

        public int[][] tiles() {
            int[][] tiles;
            int a;
            int b;
            int ia;
            int ib;
            if (!r) {
                a = y;
                b = x;
                ia = 1;
                ib = 0;
            } else {
                a = x;
                b = y;
                ia = 0;
                ib = 1;
            }
            tiles = new int[length()][2];
            for (int i = 0; i < length(); i++) {
                tiles[i][ia] = a + i;
                tiles[i][ib] = b;
            }
            return tiles;
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
        normalColor = Color.black;
        hoverColor = Color.red;
        letterColor = Color.pink;
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

        JSlider slider = new JSlider();
        slider.setFocusable(false);
        slider.setMaximum(((Dimension) Settings.getValue("maindim")).height / gridSize);
        slider.setMinimum(slider.getMaximum() / 2);
        slider.setPaintTicks(true);
        slider.setSnapToTicks(true);
        slider.setMajorTickSpacing(slider.getMaximum() / 10);
        slider.setPaintLabels(true);
        FlowLayout fl = new FlowLayout(FlowLayout.LEFT);
        fl.setVgap(slider.getMaximum() / 2 - 10);
        JPanel p = new JPanel(fl) {
            @Override
            public void paint(Graphics g) {
                super.paint(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setColor(Color.black);
                g2d.drawRect(slider.getWidth(), 0, slider.getValue(), slider.getValue());
                g2d.dispose();
            }
        };
        p.setFocusable(false);
        slider.addChangeListener(changeEvent -> p.repaint());
        p.setPreferredSize(new Dimension(400, slider.getMaximum()));
        p.add(slider);
        Settings.addSetting("blocksize", slider::getValue, p, new Dimension(400, p.getPreferredSize().height), true);

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
                assert (cPlayer1Tile != null);
                if (cPlayer1Tile[0] > -1 && cPlayer1Tile[1] > -1) {
                    int[] copy = cPlayer1Tile.clone();
                    if (gameState.data != null) {
                        Ship s = Ship.create(copy[0], copy[1], rotate, Integer.parseInt(gameState.data));
                        int[][] testTiles = s.tiles();
                        for (Ship ss : player1Ships) {
                            for (int[] tt : testTiles) {
                                if (tt[0] >= gridSize || tt[1] >= gridSize) return;
                                for (int[] t : ss.tiles()) {
                                    if (t[0] == tt[0] && t[1] == tt[1]) {
                                        return;
                                    }
                                }
                            }
                        }
                        player1Ships.add(s);
                        gameState = GameState.INIT;
                        repaint();
                    } else {
                        for (Ship s : player1Ships) {
                            for (int[] t : s.tiles()) {
                                if (t[0] == cPlayer1Tile[0] && t[1] == cPlayer1Tile[1]) {
                                    s.marked = !s.marked;
                                    repaint();
                                    return;
                                }
                            }
                        }
                    }
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

        Texture shipTexture = new Texture("/textures/ships.png");
        shipsImg = new BufferedImage[] {
                shipTexture.image.getSubimage(65, 155, 43, 43),
                shipTexture.image.getSubimage(10, 0, 30, 103),
                shipTexture.image.getSubimage(68, 2, 35, 140),
                shipTexture.image.getSubimage(109, 2, 52, 209)
        };
        markedOverlay = shipTexture.image.getSubimage(8, 108, 32, 32);

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
            Image im = shipsImg[s.length() - 1].getScaledInstance(pixelPerBlock, s.length() * (pixelPerBlock + 1), BufferedImage.TYPE_INT_ARGB);
            t.translate(s.x * (pixelPerBlock + 1) + offsetX, s.y * (pixelPerBlock + 1));
            if (s.r) {
                t.translate(0, pixelPerBlock);
                t.rotate(Math.toRadians(270));
            }
            g2d.drawImage(im, t, this);
            if (s.marked) {
                g2d.drawImage(markedOverlay.getScaledInstance(pixelPerBlock, s.length() * (pixelPerBlock + 1), BufferedImage.TYPE_INT_ARGB), t, this);
            }
        }
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setFont(Constants.smallFont);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(normalColor);
        pixelPerBlock = (int) Settings.getValue("blocksize");
        int player2Offset = (gridSize + 1) * (pixelPerBlock + 1);
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                player1Grid[i][j][0] = i * (pixelPerBlock + 1);
                player1Grid[i][j][1] = j * (pixelPerBlock + 1);
                player2Grid[i][j][0] = player1Grid[i][j][0] + player2Offset;
                player2Grid[i][j][1] = player1Grid[i][j][1];
                if (cPlayer1Tile[0] == i && cPlayer1Tile[1] == j) {
                    g2d.setColor(hoverColor);
                    if (gameState == GameState.PLACING) {
                        g2d.fillRect(player1Grid[i][j][0], player1Grid[i][j][1], pixelPerBlock + 1, pixelPerBlock + 1);
                        g2d.setColor(letterColor);
                        g2d.drawString(gameState.data, player1Grid[i][j][0] + 1, player1Grid[i][j][1] + pixelPerBlock);
                    } else {
                        g2d.drawRect(player1Grid[i][j][0], player1Grid[i][j][1], pixelPerBlock, pixelPerBlock);
                    }
                    g2d.setColor(normalColor);
                } else {
                    g2d.drawRect(player1Grid[i][j][0], player1Grid[i][j][1], pixelPerBlock, pixelPerBlock);
                }
                if (cPlayer2Tile[0] == i && cPlayer2Tile[1] == j) {
                    g2d.setColor(hoverColor);
                    g2d.drawRect(player2Grid[i][j][0], player2Grid[i][j][1], pixelPerBlock, pixelPerBlock);
                    g2d.setColor(normalColor);
                } else {
                    g2d.drawRect(player2Grid[i][j][0], player2Grid[i][j][1], pixelPerBlock, pixelPerBlock);
                }
            }
        }
        paintShips(g2d, player1Ships, 0);
        paintShips(g2d, player2Ships, player2Offset);
        g2d.dispose();
    }
}
