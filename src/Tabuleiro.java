import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

public class Tabuleiro extends JFrame {

    private JPanel painel;
    private JPanel menu;
    private JButton iniciarButton;
    private JButton resetButton;
    private JButton pauseButton;
    private JTextField placarField;
    private int x, y;
    private String direcao = "direita";
    private long tempoAtualizacao = 100;
    private int incremento = 10;
    private Quadrado maca;
    private List<Quadrado> cobra;
    private int larguraTabuleiro, alturaTabuleiro;
    private int placar = 0;
    private boolean jogoEmAndamento = false;
    private boolean pausado = false;
    private boolean bordaAtiva = true; // True = colide com borda, False = ressurge do outro lado

    public Tabuleiro() {

        larguraTabuleiro = alturaTabuleiro = 400;

        cobra = new ArrayList<>();
        cobra.add(new Quadrado(10, 10, Color.BLACK));
        cobra.get(0).x = larguraTabuleiro / 2;
        cobra.get(0).y = alturaTabuleiro / 2;

        maca = new Quadrado(10, 10, Color.red);
        gerarNovaMaca();

        setTitle("Jogo da Cobrinha");
        setSize(alturaTabuleiro, larguraTabuleiro + 30);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        menu = new JPanel();
        menu.setLayout(new FlowLayout());

        iniciarButton = new JButton("Iniciar");
        resetButton = new JButton("Reiniciar");
        pauseButton = new JButton("Pausar");
        placarField = new JTextField("Placar: 0");
        placarField.setEditable(false);

        menu.add(iniciarButton);
        menu.add(resetButton);
        menu.add(pauseButton);
        menu.add(placarField);

        painel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Desenhar a cobra
                for (Quadrado parte : cobra) {
                    g.setColor(parte.cor);
                    g.fillRect(parte.x, parte.y, parte.altura, parte.largura);
                }

                // Desenhar a maçã
                g.setColor(maca.cor);
                g.fillRect(maca.x, maca.y, maca.largura, maca.altura);
            }
        };

        add(menu, BorderLayout.NORTH);
        add(painel, BorderLayout.CENTER);

        setVisible(true);

        iniciarButton.addActionListener(e -> {
            Iniciar();
            painel.requestFocusInWindow();
        });

        resetButton.addActionListener(e -> Reiniciar());

        pauseButton.addActionListener(e -> Pausar());

        painel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_LEFT:
                        if (!direcao.equals("direita")) {
                            direcao = "esquerda";
                        }
                        break;
                    case KeyEvent.VK_RIGHT:
                        if (!direcao.equals("esquerda")) {
                            direcao = "direita";
                        }
                        break;
                    case KeyEvent.VK_UP:
                        if (!direcao.equals("baixo")) {
                            direcao = "cima";
                        }
                        break;
                    case KeyEvent.VK_DOWN:
                        if (!direcao.equals("cima")) {
                            direcao = "baixo";
                        }
                        break;
                }
            }
        });

        painel.setFocusable(true);
        painel.requestFocusInWindow();
    }

    private void Iniciar() {
        jogoEmAndamento = true;
        pausado = false;

        new Thread(() -> {
            while (jogoEmAndamento) {
                if (!pausado) {
                    try {
                        Thread.sleep(tempoAtualizacao);

                        moverCobra();
                        checarColisao();
                        painel.repaint();
                        aumentarDificuldade();

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    private void Reiniciar() {
        jogoEmAndamento = false;
        pausado = false;
        cobra.clear();
        cobra.add(new Quadrado(10, 10, Color.BLACK));
        cobra.get(0).x = larguraTabuleiro / 2;
        cobra.get(0).y = alturaTabuleiro / 2;
        placar = 0;
        placarField.setText("Placar: 0");
        tempoAtualizacao = 100;
        gerarNovaMaca();
        painel.repaint();
        JOptionPane.showMessageDialog(this, "Jogo Reiniciado!", "Reset", JOptionPane.INFORMATION_MESSAGE);
    }

    private void Pausar() {
        pausado = !pausado;
        JOptionPane.showMessageDialog(this, pausado ? "Jogo Pausado!" : "Jogo Retomado!", "Pause", JOptionPane.INFORMATION_MESSAGE);
    }

    private void moverCobra() {
        // Movimenta o corpo da cobra
        for (int i = cobra.size() - 1; i > 0; i--) {
            cobra.get(i).x = cobra.get(i - 1).x;
            cobra.get(i).y = cobra.get(i - 1).y;
        }

        // Movimenta a cabeça
        Quadrado cabeca = cobra.get(0);
        switch (direcao) {
            case "esquerda":
                cabeca.x -= incremento;
                break;
            case "direita":
                cabeca.x += incremento;
                break;
            case "cima":
                cabeca.y -= incremento;
                break;
            case "baixo":
                cabeca.y += incremento;
                break;
        }

        // Verifica colisão com bordas
        if (bordaAtiva) {
            if (cabeca.x < 0 || cabeca.x >= larguraTabuleiro || cabeca.y < 0 || cabeca.y >= alturaTabuleiro) {
                jogoEmAndamento = false;
                JOptionPane.showMessageDialog(this, "Fim de Jogo! Colisão com a borda.", "Fim", JOptionPane.INFORMATION_MESSAGE);
            }
        } else {
            if (cabeca.x < 0) cabeca.x = larguraTabuleiro - incremento;
            if (cabeca.x >= larguraTabuleiro) cabeca.x = 0;
            if (cabeca.y < 0) cabeca.y = alturaTabuleiro - incremento;
            if (cabeca.y >= alturaTabuleiro) cabeca.y = 0;
        }
    }

    private void checarColisao() {
        // Verifica colisão com a maçã
        Quadrado cabeca = cobra.get(0);
        if (cabeca.x == maca.x && cabeca.y == maca.y) {
            cobra.add(new Quadrado(10, 10, Color.BLACK));
            placar++;
            placarField.setText("Placar: " + placar);
            gerarNovaMaca();
        }

        // Verifica colisão com o próprio corpo
        for (int i = 1; i < cobra.size(); i++) {
            if (cobra.get(i).x == cabeca.x && cobra.get(i).y == cabeca.y) {
                jogoEmAndamento = false;
                JOptionPane.showMessageDialog(this, "Fim de Jogo! Colisão com o próprio corpo.", "Fim", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    private void gerarNovaMaca() {
        maca.x = (int) (Math.random() * (larguraTabuleiro / incremento)) * incremento;
        maca.y = (int) (Math.random() * (alturaTabuleiro / incremento)) * incremento;
    }

    private void aumentarDificuldade() {
        if (tempoAtualizacao > 50 && placar % 5 == 0) {
            tempoAtualizacao -= 5;
        }
    }

    public static void main(String[] args) {
        new Tabuleiro();
    }
}
