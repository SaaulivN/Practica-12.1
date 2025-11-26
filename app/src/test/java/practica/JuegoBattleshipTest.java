package practica;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class JuegoBattleshipTest {

    private JuegoBattleship juego;

    @BeforeEach
    void setUp() {
        juego = new JuegoBattleship();
    }

    @Test
    void colocarBarcosCuentaCorrecto() {
        juego.colocarBarcosAutomaticamente();
        int ocupadas = 0;
        char[][] t = juego.getTableroPropio();
        for (int i = 0; i < JuegoBattleship.TAMANIO_TABLERO; i++) {
            for (int j = 0; j < JuegoBattleship.TAMANIO_TABLERO; j++) {
                if (t[i][j] != '~') ocupadas++;
            }
        }
        assertEquals(17, ocupadas, "Total de casillas ocupadas por barcos debe ser 17");
    }

    @Test
    void recibirDisparoActualizaTableroYContadores() {
        // Colocar manualmente un barco de 2 en (0,0)-(0,1) con 'D'
        char[][] t = juego.getTableroPropio();
        t[0][0] = 'D';
        t[0][1] = 'D';

        // Disparar a (0,0) y (0,1)
        boolean r1 = juego.recibirDisparo(0, 0);
        assertTrue(r1);
        assertEquals('X', juego.getTableroPropio()[0][0]);

        boolean r2 = juego.recibirDisparo(0, 1);
        assertTrue(r2);
        assertEquals('X', juego.getTableroPropio()[0][1]);

        // Ahora el DESTRUCTOR debería estar hundido (tamaño 2)
        assertTrue(juego.estaBarcoHundido("DESTRUCTOR"));
    }

    @Test
    void registrarImpactoYFalloYyaDisparado() {
        juego.registrarImpacto(3, 3);
        assertEquals('X', juego.getTableroEnemigo()[3][3]);
        assertTrue(juego.yaDisparado(3,3));

        juego.registrarFallo(4, 4);
        assertEquals('O', juego.getTableroEnemigo()[4][4]);
        assertTrue(juego.yaDisparado(4,4));
    }

    @Test
    void todosBarcosHundidosDetectaVictoria() {
        for (String barco : juego.getBarcos().keySet()) {
            juego.getImpactosPorBarco().put(barco, juego.getBarcos().get(barco));
        }
        assertTrue(juego.todosBarcosHundidos());
    }
}
