import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
public class Test_SOM extends JFrame {

    private Morphing morphingComponent;
    private ImagePanel leftImagePanel, rightImagePanel;
    private SOM som;
    private Timer morphingTimer;
    private boolean isLeftImageActive = true;
    private int iterationCount = 0;
    private final int MAX_ITERATIONS = 1600;
    private final int SOM_WIDTH = 10;
    private final int SOM_HEIGHT = 10;
    private JLabel morphingStatusLabel = new JLabel("Stan morphingu: Nieaktywny");

    class Morphing extends JComponent {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            som.draw(g, getWidth() / 4, getHeight() / 4, getWidth() / 2, getHeight() / 2);
        }
    }

    
    class ImagePanel extends JPanel {
        private BufferedImage image;

        public ImagePanel(String buttonText) {
            setLayout(new BorderLayout());

            JButton uploadButton = new JButton(buttonText);
            uploadButton.addActionListener(e -> chooseImage());

            this.add(uploadButton, BorderLayout.NORTH);

            JComponent imageComponent = new JComponent() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    if (image != null) {
                        g.drawImage(image, 0, 0, getWidth(), getHeight(), this);
                    }
                }
            };

            this.add(imageComponent, BorderLayout.CENTER);
        }

        public BufferedImage getImage() {
            return image;
        }

        private void chooseImage() {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileFilter(new FileNameExtensionFilter("Obrazy", "jpg", "png", "gif", "bmp"));
            File projectDirectory = new File(System.getProperty("user.dir"));
            fileChooser.setCurrentDirectory(projectDirectory);

            int result = fileChooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                try {
                    File selectedFile = fileChooser.getSelectedFile();
                    image = ImageIO.read(selectedFile);
                    repaint();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    public Test_SOM(String title) {
        super(title);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        morphingComponent = new Morphing();
        leftImagePanel = new ImagePanel("Wczytaj lewy obraz");
        rightImagePanel = new ImagePanel("Wczytaj prawy obraz");

        setLayout(new GridLayout(1, 3));
        add(leftImagePanel);
        add(morphingComponent);
        add(rightImagePanel);

        som = new SOM(SOM_WIDTH, SOM_HEIGHT, 0.1, 0.999, 0.999);

        morphingTimer = new Timer(10, e -> {
            morphingUpdate();
            morphingComponent.repaint();

            iterationCount++;
            if (iterationCount >= MAX_ITERATIONS) {
                resetMorphing();
            }
        });

        JButton startButton = new JButton("Start/Stop");
        startButton.addActionListener(e -> toggleMorphing());

        morphingStatusLabel.setHorizontalAlignment(JLabel.CENTER);

        add(startButton);
        add(morphingStatusLabel);
        
        getContentPane().setBackground(Color.WHITE);
        setSize(1600, 800);
        setLocationRelativeTo(null);
        setVisible(true);
    }
    

    private void morphingUpdate() {
        ImagePanel activePanel = isLeftImageActive ? leftImagePanel : rightImagePanel;
        trainSOMWithImage(activePanel.getImage());
        morphingStatusLabel.setText("Stan morphingu: " + (isLeftImageActive ? "Lewy" : "Prawy"));

        if (!morphingTimer.isRunning()) {
            resetMorphing();
        }
    }

    private void resetMorphing() {
        som.eta = 0.1;
        som.S = Math.sqrt(SOM_WIDTH * SOM_HEIGHT);
        isLeftImageActive = !isLeftImageActive;
        iterationCount = 0;
    }

    private void toggleMorphing() {
        if (morphingTimer.isRunning()) {
            stopMorphing();
        } else {
            startMorphing();
        }
    }

    private void startMorphing() {
        if (leftImagePanel.getImage() == null || rightImagePanel.getImage() == null ||
                bufferedImagesEqual(leftImagePanel.getImage(), rightImagePanel.getImage())) {
            showErrorMessage("B��d", "Niepoprawne obrazy do morphingu.");
            return;
        }

        morphingTimer.start();
        morphingStatusLabel.setText("Stan morphingu: Aktywny");
    }

    private void stopMorphing() {
        morphingTimer.stop();
        som = new SOM(SOM_WIDTH, SOM_HEIGHT, 0.1, 0.999, 0.999);
        morphingStatusLabel.setText("Stan morphingu: Zatrzymany");
        isLeftImageActive = true;
    }

    private void showErrorMessage(String title, String message) {
        JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
    }

    boolean bufferedImagesEqual(BufferedImage img1, BufferedImage img2) {
        if (img1.getWidth() != img2.getWidth() || img1.getHeight() != img2.getHeight()) {
            return false;
        }

        for (int x = 0; x < img1.getWidth(); x++) {
            for (int y = 0; y < img1.getHeight(); y++) {
                if (img1.getRGB(x, y) != img2.getRGB(x, y)) {
                    return false;
                }
            }
        }

        return true;
    }

    public void trainSOMWithImage(BufferedImage image) {
    	  Vec2D input = getRandomBlackPixel(image);
          if (input != null) {
              som.ucz(input);
          
        }
    }

    private Vec2D getRandomBlackPixel(BufferedImage image) {
        Random rand = new Random();
        int x, y;
        do {
            x = rand.nextInt(image.getWidth());
            y = rand.nextInt(image.getHeight());
        } while (image.getRGB(x, y) != Color.BLACK.getRGB());

        double normalizedX = 2.0 * x / image.getWidth() - 1.0;
        double normalizedY = 2.0 * y / image.getHeight() - 1.0;

        return new Vec2D(normalizedX, normalizedY);
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> new Test_SOM("Image Morphing with SOM"));
    }
}
