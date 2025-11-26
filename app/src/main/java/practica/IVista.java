package practica;

public interface IVista {
    void mostrar();
    void cerrar();
    
    String obtenerNombreJugador();
    int elegirModoJuego();
    String obtenerIPServidor();
    
    void mostrarMensaje(String mensaje);
    void mostrarError(String error);
    
    void actualizarTableroPropio(char[][] tablero);
    void actualizarTableroEnemigo(char[][] tablero);
    
    void setControlador(BattleshipControlador controlador);
    
    void setPuedeDisparar(boolean puede);
}