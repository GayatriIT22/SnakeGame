package gayatriproject;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.Random;

public class SnakeGame extends JPanel implements KeyListener, ActionListener {

    private int[] snakeX = new int[750];
    private int[] snakeY = new int[750];
    private int snakeLength = 3;

    private boolean left = false;
    private boolean right = true;
    private boolean up = false;
    private boolean down = false;

    private int moves = 0;
    private int score = 0;
    private boolean gameOver = false;

    private Timer timer;
    private int delay = 100;

    private int foodX;
    private int foodY;
    private Random random = new Random();

    // Boundaries of the black box
    private final int BOX_LEFT = 25;
    private final int BOX_RIGHT = 875; // 25 + 850
    private final int BOX_TOP = 75;
    private final int BOX_BOTTOM = 625; // 50 + 575

    public SnakeGame() {
        addKeyListener(this);
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);
        setBackground(Color.black);
        spawnFood();
        timer = new Timer(delay, this);
        timer.start();
    }

    public void paint(Graphics g) {
        super.paint(g);

        // Title
        g.setColor(Color.white);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("Snake Game", 380, 30);

        // Score
        g.setFont(new Font("Arial", Font.PLAIN, 15));
        g.drawString("Score: " + score, 780, 30);

        // Border
        g.drawRect(BOX_LEFT, 50, 850, 575);

        // Gameplay area
        g.setColor(Color.black);
        g.fillRect(BOX_LEFT + 1, 51, 848, 573);

        // Snake head
        g.setColor(Color.green);
        g.fillRect(snakeX[0], snakeY[0], 25, 25);

        // Eyes 👀
        g.setColor(Color.white);
        if (right) {
            g.fillOval(snakeX[0] + 15, snakeY[0] + 5, 5, 5);
            g.fillOval(snakeX[0] + 15, snakeY[0] + 15, 5, 5);
        } else if (left) {
            g.fillOval(snakeX[0] + 5, snakeY[0] + 5, 5, 5);
            g.fillOval(snakeX[0] + 5, snakeY[0] + 15, 5, 5);
        } else if (up) {
            g.fillOval(snakeX[0] + 5, snakeY[0] + 5, 5, 5);
            g.fillOval(snakeX[0] + 15, snakeY[0] + 5, 5, 5);
        } else if (down) {
            g.fillOval(snakeX[0] + 5, snakeY[0] + 15, 5, 5);
            g.fillOval(snakeX[0] + 15, snakeY[0] + 15, 5, 5);
        }

        // Snake body
        for (int i = 1; i < snakeLength; i++) {
            g.setColor(Color.yellow);
            g.fillRect(snakeX[i], snakeY[i], 25, 25);
        }

        // Food (restricted inside box)
        g.setColor(Color.red);
        g.fillOval(foodX, foodY, 25, 25);

        // Game Over message
        if (gameOver) {
            g.setColor(Color.red);
            g.setFont(new Font("Arial", Font.BOLD, 50));
            g.drawString("Game Over", 300, 300);

            g.setFont(new Font("Arial", Font.PLAIN, 20));
            g.drawString("Press SPACE to Restart", 340, 350);
        }

        g.dispose();
    }

    private void spawnFood() {
        // Generate food only inside the playable box (in multiples of 25)
        foodX = BOX_LEFT + 25 * random.nextInt((BOX_RIGHT - BOX_LEFT - 25) / 25);
        foodY = BOX_TOP + 25 * random.nextInt((BOX_BOTTOM - BOX_TOP - 25) / 25);
    }

    private void move() {
        for (int i = snakeLength - 1; i > 0; i--) {
            snakeX[i] = snakeX[i - 1];
            snakeY[i] = snakeY[i - 1];
        }

        if (left) snakeX[0] -= 25;
        if (right) snakeX[0] += 25;
        if (up) snakeY[0] -= 25;
        if (down) snakeY[0] += 25;

        // Wrap-around movement
        if (snakeX[0] > BOX_RIGHT - 25) snakeX[0] = BOX_LEFT;
        if (snakeX[0] < BOX_LEFT) snakeX[0] = BOX_RIGHT - 25;
        if (snakeY[0] > BOX_BOTTOM - 25) snakeY[0] = BOX_TOP;
        if (snakeY[0] < BOX_TOP) snakeY[0] = BOX_BOTTOM - 25;
    }

    private void checkCollision() {
        for (int i = 1; i < snakeLength; i++) {
            if (snakeX[i] == snakeX[0] && snakeY[i] == snakeY[0]) {
                gameOver = true;
                timer.stop();
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!gameOver) {
            if (moves == 0) {
                snakeX[0] = 100;
                snakeY[0] = 100;
                snakeX[1] = 75;
                snakeY[1] = 100;
                snakeX[2] = 50;
                snakeY[2] = 100;
            }

            move();
            checkCollision();

            // Eating food
            if (snakeX[0] == foodX && snakeY[0] == foodY) {
                score += 1; // +1 per food
                snakeLength++;
                spawnFood();
            }
        }

        repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE && gameOver) {
            restart();
        }

        if (e.getKeyCode() == KeyEvent.VK_RIGHT && !left) {
            right = true;
            up = down = left = false;
        }
        if (e.getKeyCode() == KeyEvent.VK_LEFT && !right) {
            left = true;
            up = down = right = false;
        }
        if (e.getKeyCode() == KeyEvent.VK_UP && !down) {
            up = true;
            left = right = down = false;
        }
        if (e.getKeyCode() == KeyEvent.VK_DOWN && !up) {
            down = true;
            left = right = up = false;
        }

        moves++;
    }

    private void restart() {
        gameOver = false;
        score = 0;
        snakeLength = 3;
        moves = 0;
        left = false;
        right = true;
        up = false;
        down = false;
        spawnFood();
        timer.start();
        repaint();
    }

    @Override public void keyReleased(KeyEvent e) {}
    @Override public void keyTyped(KeyEvent e) {}
    
    public static void main(String[] args) {
        JFrame frame = new JFrame("Snake Game");
        SnakeGame game = new SnakeGame();

        frame.setBounds(10, 10, 905, 700);
        frame.setBackground(Color.black);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.add(game);

        frame.setVisible(true);
    }

}
