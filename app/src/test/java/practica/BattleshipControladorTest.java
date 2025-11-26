package practica;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BattleshipControladorTest {

    static class FakeVista implements IVista {
        private BattleshipControlador controladorSet;

        @Override public void mostrar() {}
        @Override public int elegirModoJuego() { return 1; }
        @Override public void actualizarTableroPropio(char[][] tablero) {}
        @Override public void actualizarTableroEnemigo(char[][] tablero) {}
        @Override public void setControlador(BattleshipControlador controlador) { this.controladorSet = controlador; }
        @Override public void setPuedeDisparar(boolean puede) {}
        @Override public String obtenerNombreJugador() { return "Test"; }
        @Override public String obtenerIPServidor() { return "127.0.0.1"; }
        @Override public void mostrarMensaje(String mensaje) {}
        @Override public void mostrarError(String error) {}
        @Override public void cerrar() {}
    }

    @Test
    public void constructor_setsControladorOnVista() {
        FakeVista vista = new FakeVista();
        BattleshipControlador controller = new BattleshipControlador(vista);

        assertNotNull(vista.controladorSet, "Controller should be set on the view");
        assertSame(controller, vista.controladorSet);
    }
}
 
