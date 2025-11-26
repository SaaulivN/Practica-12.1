package practica;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ProtocoloBattleshipTest {

    @Test
    void construirYParsearDisparo() {
        String msg = ProtocoloBattleship.construirMensajeDisparo(2, 4);
        assertEquals("DISPARAR|2,4", msg);

        ProtocoloBattleship.Mensaje m = ProtocoloBattleship.parsearMensaje(msg);
        assertEquals(ProtocoloBattleship.DISPARAR, m.comando);
        assertEquals(2, m.x);
        assertEquals(4, m.y);
    }

    @Test
    void construirYParsearResultadoConTipo() {
        String msg = ProtocoloBattleship.construirMensajeResultado(ProtocoloBattleship.HUNDIDO, 1, 1, "DESTRUCTOR");
        assertEquals("HUNDIDO|1,1|DESTRUCTOR", msg);

        ProtocoloBattleship.Mensaje m = ProtocoloBattleship.parsearMensaje(msg);
        assertEquals(ProtocoloBattleship.HUNDIDO, m.comando);
        assertEquals(1, m.x);
        assertEquals(1, m.y);
        assertEquals("DESTRUCTOR", m.tipoBarco);
    }

    @Test
    void parsearComandoSimple() {
        ProtocoloBattleship.Mensaje m = ProtocoloBattleship.parsearMensaje("LISTO");
        assertEquals("LISTO", m.comando);
        assertEquals(-1, m.x);
        assertEquals(-1, m.y);
    }

    @Test
    void parsearErrores() {
        assertThrows(IllegalArgumentException.class, () -> ProtocoloBattleship.parsearMensaje(null));
        assertThrows(IllegalArgumentException.class, () -> ProtocoloBattleship.parsearMensaje(""));
        assertThrows(IllegalArgumentException.class, () -> ProtocoloBattleship.parsearMensaje("DISPARAR|a,b"));
    }
}
