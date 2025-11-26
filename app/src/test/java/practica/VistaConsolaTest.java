package practica;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

public class VistaConsolaTest {

    private final PrintStream originalOut = System.out;
    private final InputStream originalIn = System.in;
    private final PrintStream originalErr = System.err;

    @AfterEach
    public void restoreStreams() {
        System.setOut(originalOut);
        System.setIn(originalIn);
        System.setErr(originalErr);
    }

    @Test
    public void mostrarBienvenida_printsHeader() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        VistaConsola v = new VistaConsola();
        v.mostrarBienvenida();

        String s = out.toString();
        assertTrue(s.contains("BATTLESHIP"));
    }

    @Test
    public void obtenerDisparo_parsesInput() {
        String input = "3,4\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        VistaConsola v = new VistaConsola();
        int[] res = v.obtenerDisparo((x, y) -> false);
        assertEquals(3, res[0]);
        assertEquals(4, res[1]);
    }

    @Test
    public void setPuedeDisparar_invokesControllerProcesarDisparo() {
        String input = "2,3\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        VistaConsola v = new VistaConsola();
        class FakeController extends BattleshipControlador {
            public int calledX = -1, calledY = -1;
            public FakeController() { super(new VistaConsola() { // dummy IVista for super
                @Override public void mostrar() {}
                @Override public int elegirModoJuego() { return 1; }
                @Override public void actualizarTableroPropio(char[][] tablero) {}
                @Override public void actualizarTableroEnemigo(char[][] tablero) {}
                @Override public void setControlador(BattleshipControlador controlador) {}
                @Override public void setPuedeDisparar(boolean puede) {}
                @Override public String obtenerNombreJugador() { return "fake"; }
                @Override public String obtenerIPServidor() { return "127.0.0.1"; }
                @Override public void mostrarMensaje(String mensaje) {}
                @Override public void mostrarError(String error) {}
                @Override public void cerrar() {}
            }); }
            @Override
            public void procesarDisparo(int fila, int columna) {
                this.calledX = fila; this.calledY = columna;
            }
        }

        FakeController controller = new FakeController();
        v.setControlador(controller);

        v.setPuedeDisparar(true);

        assertEquals(2, controller.calledX);
        assertEquals(3, controller.calledY);
    }
}
 
