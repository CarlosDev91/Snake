import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

public class Tabuleiro extends JFrame {

    private JPanel painel;
    private String direcao = "direita";
    private long tempoAtualizacao = 350;
    private int incremento = 10;
    private Quadrado maca;
    private List<Quadrado> cobra;
    private int larguraTabuleiro, alturaTabuleiro;
    private int placar = 0;
    private boolean jogoEmAndamento = false;
    private boolean pausado = false;
    private boolean modoInfinito;  // Novo atributo para modo de jogo

    public Tabuleiro(boolean modoInfinito) {
        this.modoInfinito = modoInfinito;

        larguraTabuleiro = alturaTabuleiro = 400;
        cobra = new ArrayList<>();
        cobra.add(new Quadrado(10, 10, Color.ORANGE));
        cobra.get(0).x = larguraTabuleiro / 2;
        cobra.get(0).y = alturaTabuleiro / 2;

        maca = new Quadrado(10, 10, Color.RED);
        gerarNovaMaca();

        setTitle("Jogo da Cobrinha");
        setSize(alturaTabuleiro, larguraTabuleiro + 30);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        painel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                setBackground(Color.BLACK);
                for (Quadrado parte : cobra) {
                    g.setColor(parte.cor);
                    g.fillRect(parte.x, parte.y, parte.altura, parte.largura);
                }
                g.setColor(maca.cor);
                g.fillRect(maca.x, maca.y, maca.largura, maca.altura);
            }
        };

        add(painel, BorderLayout.CENTER);
        painel.setFocusable(true);
        painel.requestFocusInWindow();
        painel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_A -> direcao = !direcao.equals("direita") ? "esquerda" : direcao;
                    case KeyEvent.VK_D -> direcao = !direcao.equals("esquerda") ? "direita" : direcao;
                    case KeyEvent.VK_W -> direcao = !direcao.equals("baixo") ? "cima" : direcao;
                    case KeyEvent.VK_S -> direcao = !direcao.equals("cima") ? "baixo" : direcao;
                }
            }
        });

        setVisible(true);
        iniciarJogo();
    }

    private void iniciarJogo() {
        jogoEmAndamento = true;
        new Thread(() -> {
            while (jogoEmAndamento) {
                try {
                    Thread.sleep(tempoAtualizacao);
                    moverCobra();
                    checarColisao();
                    painel.repaint();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void moverCobra() {
        for (int i = cobra.size() - 1; i > 0; i--) {
            cobra.get(i).x = cobra.get(i - 1).x;
            cobra.get(i).y = cobra.get(i - 1).y;
        }

        Quadrado cabeca = cobra.get(0);
        switch (direcao) {
            case "esquerda" -> cabeca.x -= incremento;
            case "direita" -> cabeca.x += incremento;
            case "cima" -> cabeca.y -= incremento;
            case "baixo" -> cabeca.y += incremento;
        }

        if (modoInfinito) {
            if (cabeca.x < 0) cabeca.x = larguraTabuleiro - incremento;
            if (cabeca.x >= larguraTabuleiro) cabeca.x = 0;
            if (cabeca.y < 0) cabeca.y = alturaTabuleiro - incremento;
            if (cabeca.y >= alturaTabuleiro) cabeca.y = 0;
        }
    }

    private void checarColisao() {
        Quadrado cabeca = cobra.get(0);

        if (Math.abs(cabeca.x - maca.x) < incremento && Math.abs(cabeca.y - maca.y) < incremento) {
            cobra.add(new Quadrado(10, 10, Color.ORANGE));
            placar++;
            gerarNovaMaca();
        }

        for (int i = 1; i < cobra.size(); i++) {
            if (cobra.get(i).x == cabeca.x && cobra.get(i).y == cabeca.y) {
                fimDeJogo("Colisão com o corpo!");
            }
        }

        if (!modoInfinito) {
            if (cabeca.x < 0 || cabeca.x >= larguraTabuleiro || cabeca.y < 0 || cabeca.y >= alturaTabuleiro) {
                fimDeJogo("Colisão com a borda!");
            }
        }
    }

    private void fimDeJogo(String mensagem) {
        jogoEmAndamento = false;
        JOptionPane.showMessageDialog(this, "Fim de Jogo! " + mensagem, "Fim", JOptionPane.INFORMATION_MESSAGE);
        System.exit(0);
    }

    private void gerarNovaMaca() {
        maca.x = (int) (Math.random() * (larguraTabuleiro / incremento)) * incremento;
        maca.y = (int) (Math.random() * (alturaTabuleiro / incremento)) * incremento;
    }
}
