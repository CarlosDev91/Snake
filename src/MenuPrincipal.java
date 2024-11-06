import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MenuPrincipal extends JFrame {

    public MenuPrincipal() {
        setTitle("Menu Principal - Jogo da Cobrinha");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JButton modoInfinitoButton = new JButton("Modo Infinito (Bordas Conectadas)");
        JButton modoFinitasButton = new JButton("Modo Finito (Game Over nas Bordas)");

        modoInfinitoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Tabuleiro(true);  // Passa true para o modo infinito
                dispose();  // Fecha o menu principal
            }
        });

        modoFinitasButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Tabuleiro(false); // Passa false para o modo finito
                dispose();  // Fecha o menu principal
            }
        });

        JPanel panel = new JPanel();
        panel.add(modoInfinitoButton);
        panel.add(modoFinitasButton);
        add(panel);

        setVisible(true);
    }

    public static void main(String[] args) {
        new MenuPrincipal();
    }
}
