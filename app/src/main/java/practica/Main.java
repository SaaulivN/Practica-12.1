package practica;

import java.util.Scanner;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("=== BATTLESHIP P2P ===");
        System.out.println("Selecciona el modo de visualización:");
        System.out.println("[1] Modo Consola");
        System.out.println("[2] Modo Gráfico");
        System.out.print("Opción: ");
        
        String opcion = scanner.nextLine();

        if ("2".equals(opcion)) {
            System.out.println("Iniciando interfaz gráfica...");
            SwingUtilities.invokeLater(() -> {
                IVista vistaSwing = new VistaSwing();
                BattleshipControlador controlador = new BattleshipControlador(vistaSwing);
                controlador.iniciar();
            });
        } else {
            System.out.println("Iniciando modo consola...\n");
            IVista vistaConsola = new VistaConsola();
            BattleshipControlador controlador = new BattleshipControlador(vistaConsola);
            controlador.iniciar();
        }
    }
}