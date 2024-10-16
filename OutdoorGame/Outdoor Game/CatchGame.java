import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Random;

public class CatchGame extends JPanel implements ActionListener {
    private static final int WIDTH = 400;
    private static final int HEIGHT = 600;
    private static final int CATCHER_WIDTH = 60;
    private static final int CATCHER_HEIGHT = 20;
    private static final int OBJECT_SIZE = 30;

    private Timer timer;
    private int catcherX;
    private ArrayList<Point> fallingObjects;
    private int score;
    private String playerName;

    public CatchGame(String playerName) {
        this.playerName = playerName;
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);
        catcherX = WIDTH / 2 - CATCHER_WIDTH / 2;
        fallingObjects = new ArrayList<>();
        score = 0;
        timer = new Timer(20, this);
        timer.start();

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_LEFT && catcherX > 0) {
                    catcherX -= 20;
                }
                if (e.getKeyCode() == KeyEvent.VK_RIGHT && catcherX < WIDTH - CATCHER_WIDTH) {
                    catcherX += 20;
                }
            }
        });

        new FallingObjectGenerator().start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.RED);
        g.fillRect(catcherX, HEIGHT - CATCHER_HEIGHT, CATCHER_WIDTH, CATCHER_HEIGHT);

        g.setColor(Color.YELLOW);
        for (Point object : fallingObjects) {
            g.fillRect(object.x, object.y, OBJECT_SIZE, OBJECT_SIZE);
        }

        // Display score and player name
        g.setColor(Color.WHITE);
        g.drawString("Player: " + playerName, 10, 20);
        g.drawString("Score: " + score, WIDTH - 100, 20);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        for (int i = 0; i < fallingObjects.size(); i++) {
            Point object = fallingObjects.get(i);
            object.y += 5; // fall speed

            // Check if caught
            if (object.y + OBJECT_SIZE >= HEIGHT - CATCHER_HEIGHT && 
                object.x >= catcherX && object.x <= catcherX + CATCHER_WIDTH) {
                fallingObjects.remove(i);
                score++; // Increase score for catching
                i--; // Adjust index after removal
                continue; // Catch the object
            }

            // Deduct points if object is missed
            if (object.y > HEIGHT) {
                fallingObjects.remove(i);
                score = Math.max(0, score - 1); // Deduct points but not below zero
                i--; // Adjust index after removal
            }
        }
        repaint();
    }

    private class FallingObjectGenerator extends Thread {
        @Override
        public void run() {
            Random random = new Random();
            while (true) {
                int x = random.nextInt(WIDTH - OBJECT_SIZE);
                fallingObjects.add(new Point(x, 0));
                try {
                    Thread.sleep(1000); // new object every second
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
        // Input player name
        String playerName = JOptionPane.showInputDialog("Enter your name:");
        if (playerName == null || playerName.trim().isEmpty()) {
            playerName = "Player"; // Default name
        }

        JFrame frame = new JFrame("Catch the Falling Objects");
        CatchGame game = new CatchGame(playerName);
        frame.add(game);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
