import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Scanner;

public class Initialiser {
    static JTextArea text = new JTextArea();
    static JTextArea panel2Text = new JTextArea();

    private int playerCount = 0;

    /**
     * Determines the amount of players in the game
     */
    Initialiser() {
        Scanner sc;
        System.out.println("Type below how many players are playing (Choose between 3 or 4): ");
        while(true) {
            sc = new Scanner(System.in);
            if (sc.hasNextInt()) {
                playerCount = sc.nextInt();
                if (playerCount == 3 || playerCount == 4) {
                    break;
                }
            }
            System.out.println("Please input a valid player count :");
        }
    }

    /**
     * Getter method
     * @return The playerCount
     */
    public int getPlayerCount() { return playerCount; }

    /**
     * Redirects System.out to JTextArea
     */
    public static class CustomOutputStream extends OutputStream {
        private JTextArea textControl;

        public CustomOutputStream(JTextArea control) {
            this.textControl = control;
        }

        @Override
        public void write(int b) throws IOException {
            // redirects data to the text control
            textControl.append(String.valueOf((char)b));
            // scrolls the text area to the end of data
            textControl.setCaretPosition(textControl.getDocument().getLength());
        }
    }

    /**
     * Setup GUI for game start
     */
    public static void setup() {
        JFrame frame = new JFrame("Cluedo");
        JPanel container = new JPanel();

        JPanel panel1 = new JPanel();
        JPanel panel2 = new JPanel();

        panel1.setBackground(new Color(32,33,36));
        panel2.setBackground(Color.WHITE);
        panel2.setSize(1000,200);

        container.add(panel1);
        container.add(panel2);
        container.setLayout(new BoxLayout(container, BoxLayout.X_AXIS));

        frame.add(container);
        frame.setSize(1300, 750);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        frame.setAlwaysOnTop(true);

        text = new JTextArea();
        text.setEditable(false); // prevent users from accidentily messing up grid formatting
        panel1.add(text);

        // redirected System.out
        panel2.setLayout(new BorderLayout());
        panel2Text = new JTextArea();
        panel2Text.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // text padding
        panel2Text.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(panel2Text);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        panel2.add(scrollPane, BorderLayout.CENTER);

        PrintStream panel2PrintStream = new PrintStream(new CustomOutputStream(panel2Text));
        System.setOut(panel2PrintStream);
        System.setErr(panel2PrintStream);

        text.setFont(new Font("Courier New", Font.PLAIN, 12));
        text.setBackground(new Color(32,33,36));
        text.setForeground(new Color(220, 220, 220));

        text.setText(Main.board.toString());
    }
}