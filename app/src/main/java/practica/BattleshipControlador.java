package practica;

import javax.swing.SwingUtilities;
import java.io.IOException;

public class BattleshipControlador {
    private static final int PUERTO = 12345;

    private JuegoBattleship juego;
    private IVista vista;
    private ConexionP2P conexion;

    private String nombreJugador;
    private boolean esServidor;
    private boolean juegoTerminado = false;

    public BattleshipControlador(IVista vista) {
        this.vista = vista;
        this.juego = new JuegoBattleship();
        this.conexion = new ConexionP2P();
        
        this.vista.setControlador(this);
    }

    public void iniciar() {
        vista.mostrar();
        
        new Thread(() -> {
            nombreJugador = vista.obtenerNombreJugador();
            if (nombreJugador == null || nombreJugador.isEmpty()) nombreJugador = "Jugador";

            int modo = vista.elegirModoJuego();
            esServidor = (modo == 1);

            boolean conectado = establecerConexion();

            if (conectado) {
                try {
                    intercambiarNombres();
                    configurarJuego();
                    jugar();
                } catch (IOException e) {
                    vista.mostrarError("Error durante el juego: " + e.getMessage());
                }
            } else {
                vista.mostrarError("No se pudo establecer la conexión.");
                vista.cerrar();
            }
        }).start();
    }

    private boolean establecerConexion() {
        try {
            if (esServidor) {
                vista.mostrarMensaje("Iniciando servidor en puerto " + PUERTO + "...");
                vista.mostrarMensaje("Esperando rival...");
                conexion.esperarConexion(PUERTO);
            } else {
                String ip = vista.obtenerIPServidor();
                vista.mostrarMensaje("Conectando a " + ip + "...");
                conexion.conectar(ip, PUERTO);
            }
            vista.mostrarMensaje("¡Conexión establecida!");
            return true;
        } catch (IOException e) {
            vista.mostrarError("Error de conexión: " + e.getMessage());
            return false;
        }
    }

    private void intercambiarNombres() throws IOException {
        if (esServidor) {
            String nombreOponente = conexion.leerMensaje();
            conexion.enviarMensaje(nombreJugador);
            vista.mostrarMensaje("Rival: " + nombreOponente);
        } else {
            conexion.enviarMensaje(nombreJugador);
            String nombreOponente = conexion.leerMensaje();
            vista.mostrarMensaje("Rival: " + nombreOponente);
        }
    }

    private void configurarJuego() {
        juego.colocarBarcosAutomaticamente();
        
        SwingUtilities.invokeLater(() -> {
            vista.actualizarTableroPropio(juego.getTableroPropio());
            vista.mostrarMensaje("Barcos colocados. ¡Listo para la batalla!");
        });
    }

    private void jugar() throws IOException {
        conexion.enviarMensaje(ProtocoloBattleship.LISTO);
        String respuesta = conexion.leerMensaje();
        
        if (!ProtocoloBattleship.LISTO.equals(respuesta)) {
            vista.mostrarError("Error de sincronización con el rival.");
            return;
        }

        vista.mostrarMensaje("¡La partida ha comenzado!");
        
        if (esServidor) {
            iniciarMiTurno();
        } else {
            iniciarTurnoOponente();
        }
    }

    private void iniciarMiTurno() {
        if (juegoTerminado) return;
        
        SwingUtilities.invokeLater(() -> {
            vista.setPuedeDisparar(true);
        });
    }

    public void procesarDisparo(int fila, int columna) {
        new Thread(() -> {
            try {
                conexion.enviarMensaje(ProtocoloBattleship.construirMensajeDisparo(fila, columna));
                
                String respuesta = conexion.leerMensaje();
                ProtocoloBattleship.Mensaje msg = ProtocoloBattleship.parsearMensaje(respuesta);

                SwingUtilities.invokeLater(() -> {
                    if (msg.comando.equals(ProtocoloBattleship.IMPACTO) || msg.comando.equals(ProtocoloBattleship.HUNDIDO)) {
                        vista.mostrarMensaje("¡IMPACTO en (" + fila + "," + columna + ")!");
                        juego.registrarImpacto(fila, columna);
                        if (msg.comando.equals(ProtocoloBattleship.HUNDIDO)) {
                            vista.mostrarMensaje("¡Has HUNDIDO un barco enemigo!");
                        }
                    } else if (msg.comando.equals(ProtocoloBattleship.FALLO)) {
                        vista.mostrarMensaje("Agua en (" + fila + "," + columna + ").");
                        juego.registrarFallo(fila, columna);
                    } else if (msg.comando.equals(ProtocoloBattleship.JUEGO_TERMINADO)) {
                        vista.mostrarMensaje("¡HAS GANADO LA PARTIDA!");
                        juegoTerminado = true;
                        juego.registrarImpacto(fila, columna); 
                        vista.actualizarTableroEnemigo(juego.getTableroEnemigo());
                        return;
                    }

                    vista.actualizarTableroEnemigo(juego.getTableroEnemigo());
                    vista.setPuedeDisparar(false);
                });

                if (!juegoTerminado) {
                    iniciarTurnoOponente();
                }

            } catch (Exception e) {
                e.printStackTrace();
                vista.mostrarError("Error procesando disparo: " + e.getMessage());
            }
        }).start();
    }

    private void iniciarTurnoOponente() {
        if (juegoTerminado) return;

        SwingUtilities.invokeLater(() -> vista.setPuedeDisparar(false));

        new Thread(() -> {
            try {
                String mensajeEntrante = conexion.leerMensaje();
                if (mensajeEntrante == null) return;

                ProtocoloBattleship.Mensaje msg = ProtocoloBattleship.parsearMensaje(mensajeEntrante);

                if (ProtocoloBattleship.DISPARAR.equals(msg.comando)) {
                    boolean impacto = juego.recibirDisparo(msg.x, msg.y);
                    String respuesta;

                    if (impacto) {
                        String tipoBarco = juego.obtenerTipoBarcoEn(msg.x, msg.y);
                        
                        if (juego.todosBarcosHundidos()) {
                            respuesta = ProtocoloBattleship.JUEGO_TERMINADO;
                            juegoTerminado = true;
                            SwingUtilities.invokeLater(() -> {
                                vista.mostrarError("¡TE HAN DERROTADO! Tu flota ha sido destruida.");
                            });
                        } else if (juego.estaBarcoHundido(tipoBarco)) {
                             respuesta = ProtocoloBattleship.construirMensajeResultado(
                                    ProtocoloBattleship.HUNDIDO, msg.x, msg.y, tipoBarco);
                             SwingUtilities.invokeLater(() -> vista.mostrarMensaje("¡El enemigo hundió tu " + tipoBarco + "!"));
                        } else {
                            respuesta = ProtocoloBattleship.construirMensajeResultado(
                                    ProtocoloBattleship.IMPACTO, msg.x, msg.y, null);
                            SwingUtilities.invokeLater(() -> vista.mostrarMensaje("¡Te han dado en (" + msg.x + "," + msg.y + ")!"));
                        }
                    } else {
                        respuesta = ProtocoloBattleship.construirMensajeResultado(
                                ProtocoloBattleship.FALLO, msg.x, msg.y, null);
                        SwingUtilities.invokeLater(() -> vista.mostrarMensaje("El enemigo falló el disparo."));
                    }

                    conexion.enviarMensaje(respuesta);

                    SwingUtilities.invokeLater(() -> vista.actualizarTableroPropio(juego.getTableroPropio()));

                    if (!juegoTerminado) {
                        iniciarMiTurno();
                    }
                }
            } catch (IOException e) {
                vista.mostrarError("Error en turno oponente: " + e.getMessage());
            }
        }).start();
    }
}