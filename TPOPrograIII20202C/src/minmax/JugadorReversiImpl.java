package minmax;

import java.util.ArrayList;
import java.util.List;

import reversi.Celda;
import reversi.JugadorReversi;

class Logger {
    public static void log(String msg) {
        System.out.println(msg);
    }

    public static void matrix(int[][] matrix) {
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                System.out.print(matrix[i][j] + " ");
            }
            System.out.println();
        }
    }
}

public class JugadorReversiImpl implements JugadorReversi {

    final int JUGADOR = 1;
    final int OPONENTE = -1;

    public int nodosExplorados = 0;

    @Override
    public Celda devolverJugadaOptima(int[][] tablero) {
        return CalcularPuntaje(tablero, JUGADOR, 10);
    }

    public Celda CalcularPuntaje(int[][] tablero, int jugador, int control) {
        nodosExplorados = 0;

        Celda mejor_mov = new Celda(-1, -1);
        int maxPuntaje = Integer.MIN_VALUE;

        boolean semBusqueda = true;
        List<Celda> posiblesJugadas = BuscarPosiblesJugadas(tablero, JUGADOR);
        int i = 0;

        while (semBusqueda && i < posiblesJugadas.size() - 1) {
            int[][] tableroHijo = MarcarTablero(tablero, posiblesJugadas.get(i), JUGADOR, 0);
            Logger.matrix(tableroHijo);
            maxPuntaje = valorMinMax(tableroHijo, jugador, control - 1);
            if (maxPuntaje > 0) {
                semBusqueda = false;
                mejor_mov = posiblesJugadas.get(i);
            }
            i++;
        }
        Logger.log("NODOS EXPLORADOS: " + nodosExplorados);
        return mejor_mov;
    }

    private int valorMinMax(int[][] tablero, int jugador, int control) {
        nodosExplorados++;
        int valor = 0;
        int proxJugador = 0;
        if (TableroCompleto(tablero) || !HayMovimientos(tablero) || control < 0) {
            return EvaluarTablero(tablero, jugador);
        } else {
            List<Celda> hijos = BuscarPosiblesJugadas(tablero, jugador);
            Logger.log("posibles jugadas " + hijos.size());
            if (hijos.size() == 0) {
                System.out.println("NO TENGO MOVS: " + jugador + " DEBE JUGAR: " + -1 * jugador);
                return valorMinMax(tablero, jugador * -1, control);
                //TODO: FUNCION PARA PASAR DE TURNO QUE EJECUTA???

            } else {
                if (jugador == JUGADOR) {
                    proxJugador = OPONENTE;
                    valor = OPONENTE;
                } else {
                    proxJugador = JUGADOR;
                    valor = JUGADOR;
                }
            }

            int i = 0;
            boolean podar = false;
            while (!podar && i < hijos.size() - 1 && control > 0) {
                System.out.println("JUEGA JUGADOR: " + jugador);
                int[][] tableroHijo = MarcarTablero(tablero, hijos.get(i), jugador, 0);
                Logger.matrix(tableroHijo);
                int resu = valorMinMax(tableroHijo, proxJugador, control - 1);
                if (jugador == 1) {
                    valor = Math.max(valor, resu);
                } else {
                    valor = Math.min(valor, resu);
                }

                if ((jugador == 1 && valor == 1) || (jugador == -1 && valor == -1)) {
                    Logger.log("podo4");
                    podar = true;
                }
                i++;
            }
            return valor;
        }
    }


    private boolean TableroCompleto(int[][] tablero) {
        boolean completo = true;
        for (int fila = 0; fila <= 7; fila++) {
            for (int columna = 0; columna <= 7; columna++) {
                if (tablero[fila][columna] == 0) {
                    completo = false;
                }
            }
        }
        return completo;
    }

    private boolean HayMovimientos(int[][] tablero) {
        boolean hayMovimientos = true;

        List<Celda> movimientosJugador = BuscarPosiblesJugadas(tablero, 1);
        List<Celda> movimientosOponente = BuscarPosiblesJugadas(tablero, -1);
        Logger.log("movimientos jugador " + movimientosJugador.size());
        Logger.log("movimientos openente " + movimientosOponente.size());
        if (movimientosJugador.size() == 0 && movimientosOponente.size() == 0)
            hayMovimientos = false;

        return hayMovimientos;
    }

    private int EvaluarTablero(int[][] tablero, int jugador) {
        int fichasJugador = 0;
        int fichasOponente = 0;

        for (int fila = 0; fila <= 7; fila++) {
            for (int columna = 0; columna <= 7; columna++) {
                if (tablero[fila][columna] == 1) {
                    fichasJugador++;
                } else if (tablero[fila][columna] == -1) {
                    fichasOponente++;
                }
            }
        }
        if (fichasJugador - fichasOponente > 0)
            return 1;
        else
            return -1;
    }

    private int[][] MarcarTablero(int[][] tableroAnterior, Celda proxPos, int jugador, int desmarcar) {
        int fichas = 0;
        int fila = proxPos.getFila();
        int columna = proxPos.getColumna();

        // COPIO EL TABLERO
        int[][] tablero = new int[8][8];
        for (int i = 0; i <= 7; i++) {
            for (int j = 0; j <= 7; j++) {
                tablero[i][j] = tableroAnterior[i][j];
            }
        }

        // MARCO O DESMARCO
        if (desmarcar == 1)
            tablero[fila][columna] = 0;
        else
            tablero[fila][columna] = jugador;

        // MARCO EN TODAS LAS DIRECCIONES POSIBLES
        fichas = BuscarArriba(tablero, jugador, fila, columna);
        if (fichas > 0) {
            for (int i = 1; i <= fichas; i++) {
                tablero[fila - i][columna] = jugador;
            }
        }

        fichas = BuscarDiagSupDer(tablero, jugador, fila, columna);
        if (fichas > 0) {
            for (int i = 1; i <= fichas; i++) {
                tablero[fila - i][columna + i] = jugador;
            }
        }

        fichas = BuscarDerecha(tablero, jugador, fila, columna);
        if (fichas > 0) {
            for (int i = 1; i <= fichas; i++) {
                tablero[fila][columna + i] = jugador;
            }
        }

        fichas = BuscarDiagInfDer(tablero, jugador, fila, columna);
        if (fichas > 0) {
            for (int i = 1; i <= fichas; i++) {
                tablero[fila + i][columna + i] = jugador;
            }
        }

        fichas = BuscarAbajo(tablero, jugador, fila, columna);
        if (fichas > 0) {
            for (int i = 1; i <= fichas; i++) {
                tablero[fila + i][columna] = jugador;
            }
        }

        fichas = BuscarDiagInfIzq(tablero, jugador, fila, columna);
        if (fichas > 0) {
            for (int i = 1; i <= fichas; i++) {
                tablero[fila + i][columna - i] = jugador;
            }
        }

        fichas = BuscarIzquierda(tablero, jugador, fila, columna);
        if (fichas > 0) {
            for (int i = 1; i <= fichas; i++) {
                tablero[fila][columna - i] = jugador;
            }
        }

        fichas = BuscarDiagSupIzq(tablero, jugador, fila, columna);
        if (fichas > 0) {
            for (int i = 1; i <= fichas; i++) {
                tablero[fila - i][columna - i] = jugador;
            }
        }

        //PrintTablero(tablero);

        return tablero;
    }

    private void PrintTablero(int[][] tablero) {
        System.out.println("-----------------------------------");
        System.out.println("  | 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 |");
        System.out.println("-----------------------------------");
        for (int x = 0; x < tablero.length; x++) {
            System.out.print(x + " | ");
            for (int y = 0; y < tablero[x].length; y++) {
                String jugadorPrint;
                if (tablero[x][y] == 1) {
                    jugadorPrint = "O";
                } else if (tablero[x][y] == -1) {
                    jugadorPrint = "X";
                } else {
                    jugadorPrint = " ";
                }
                System.out.print(jugadorPrint);
                if (y != tablero[x].length - 1) {
                    System.out.print(" | ");
                }
            }
            System.out.println(" |");
        }
        System.out.println("-----------------------------------");
        System.out.println();
    }

    private List<Celda> BuscarPosiblesJugadas(int[][] tablero, int jugador) {
        List<Celda> posiblesJugadas = new ArrayList<Celda>();
        for (int fila = 0; fila <= 7; fila++) {
            for (int columna = 0; columna <= 7; columna++) {
                if (tablero[fila][columna] == 0) {
                    int cantMovimientos = 0;
                    cantMovimientos = cantMovimientos + BuscarArriba(tablero, jugador, fila, columna);
                    cantMovimientos = cantMovimientos + BuscarDiagSupDer(tablero, jugador, fila, columna);
                    cantMovimientos = cantMovimientos + BuscarDerecha(tablero, jugador, fila, columna);
                    cantMovimientos = cantMovimientos + BuscarDiagInfDer(tablero, jugador, fila, columna);
                    cantMovimientos = cantMovimientos + BuscarAbajo(tablero, jugador, fila, columna);
                    cantMovimientos = cantMovimientos + BuscarDiagInfIzq(tablero, jugador, fila, columna);
                    cantMovimientos = cantMovimientos + BuscarIzquierda(tablero, jugador, fila, columna);
                    cantMovimientos = cantMovimientos + BuscarDiagSupIzq(tablero, jugador, fila, columna);

                    if (cantMovimientos > 0) {
                        posiblesJugadas.add(new Celda(fila, columna));
                    }
                }
            }
        }
        return posiblesJugadas;
    }

    /**
     * @param tablero
     * @param jugador
     * @param fila
     * @param columna
     * @return
     */
    private int BuscarArriba(int[][] tablero, int jugador, int fila, int columna) {
        // System.out.println("BUSCAR ARRIBA: " + fila + columna);
        int posicionFinalFila = 0;
        int fichasContrarias = 0;
        boolean semBusqueda = true;
        int filaSig = fila - 1;
        while (semBusqueda && filaSig >= 0) {
            if (tablero[filaSig][columna] == -1 * jugador) {
                fichasContrarias = fichasContrarias + 1;
            } else {
                semBusqueda = false;
            }
            filaSig = filaSig - 1;
        }
        posicionFinalFila = fila - fichasContrarias - 1;
        if (fichasContrarias > 0 && posicionFinalFila >= 0 && tablero[posicionFinalFila][columna] == jugador) {
            return fichasContrarias;
        } else {
            return 0;
        }
    }

    /**
     * @param tablero
     * @param jugador
     * @param fila
     * @param columna
     * @return
     */
    private int BuscarDiagSupDer(int[][] tablero, int jugador, int fila, int columna) {
        // System.out.println("BUSCAR DIAG SUP DER: " + fila + columna);
        int posicionFinalFila = 0;
        int posicionFinalColumna = 0;
        int fichasContrarias = 0;
        boolean semBusqueda = true;
        int filaSig = fila - 1;
        int columnaSig = columna + 1;
        while (semBusqueda && filaSig >= 0 && columnaSig <= 7) {
            if (tablero[filaSig][columnaSig] == -1 * jugador) {
                fichasContrarias = fichasContrarias + 1;
            } else {
                semBusqueda = false;
            }
            filaSig = filaSig - 1;
            columnaSig = columnaSig + 1;
        }
        posicionFinalFila = fila - fichasContrarias - 1;
        posicionFinalColumna = columna + fichasContrarias + 1;
        if (fichasContrarias > 0 && posicionFinalFila >= 0 && posicionFinalColumna <= 7
                && tablero[posicionFinalFila][posicionFinalColumna] == jugador) {
            return fichasContrarias;
        } else {
            return 0;
        }
    }

    /**
     * @param tablero
     * @param jugador
     * @param fila
     * @param columna
     * @return
     */
    private int BuscarDerecha(int[][] tablero, int jugador, int fila, int columna) {
        // System.out.println("BUSCAR DERECHA: " + fila + columna);
        int posicionFinalColumna = 0;
        int fichasContrarias = 0;
        boolean semBusqueda = true;
        int columnaSig = columna + 1;
        while (semBusqueda && columnaSig <= 7) {
            if (tablero[fila][columnaSig] == -1 * jugador) {
                fichasContrarias = fichasContrarias + 1;
            } else {
                semBusqueda = false;
            }
            columnaSig = columnaSig + 1;
        }
        posicionFinalColumna = columna + fichasContrarias + 1;
        if (fichasContrarias > 0 && posicionFinalColumna <= 7 && tablero[fila][posicionFinalColumna] == jugador) {
            return fichasContrarias;
        } else {
            return 0;
        }
    }

    /**
     * @param tablero
     * @param jugador
     * @param fila
     * @param columna
     * @return
     */
    private int BuscarDiagInfDer(int[][] tablero, int jugador, int fila, int columna) {
        // System.out.println("BUSCAR DIAG INF DER: " + fila + columna);
        int posicionFinalFila = 0;
        int posicionFinalColumna = 0;
        int fichasContrarias = 0;
        boolean semBusqueda = true;
        int filaSig = fila + 1;
        int columnaSig = columna + 1;
        while (semBusqueda && filaSig <= 7 && columnaSig <= 7) {
            if (tablero[filaSig][columnaSig] == -1 * jugador) {
                fichasContrarias = fichasContrarias + 1;
            } else {
                semBusqueda = false;
            }
            filaSig = filaSig + 1;
            columnaSig = columnaSig + 1;
        }
        posicionFinalFila = fila + fichasContrarias + 1;
        posicionFinalColumna = columna + fichasContrarias + 1;
        if (fichasContrarias > 0 && posicionFinalFila <= 7 && posicionFinalColumna <= 7
                && tablero[posicionFinalFila][posicionFinalColumna] == jugador) {
            return fichasContrarias;
        } else {
            return 0;
        }
    }

    /**
     * @param tablero
     * @param jugador
     * @param fila
     * @param columna
     * @return
     */
    private int BuscarAbajo(int[][] tablero, int jugador, int fila, int columna) {
        // System.out.println("BUSCAR ABAJO: " + fila + columna);
        int posicionFinalFila = 0;
        int fichasContrarias = 0;
        boolean semBusqueda = true;
        int filaSig = fila + 1;
        while (semBusqueda && filaSig <= 7) {
            if (tablero[filaSig][columna] == -1 * jugador) {
                fichasContrarias = fichasContrarias + 1;
            } else {
                semBusqueda = false;
            }
            filaSig = filaSig + 1;
        }
        posicionFinalFila = fila + fichasContrarias + 1;
        if (fichasContrarias > 0 && posicionFinalFila <= 7 && tablero[posicionFinalFila][columna] == jugador) {
            return fichasContrarias;
        } else {
            return 0;
        }
    }

    /**
     * @param tablero
     * @param jugador
     * @param fila
     * @param columna
     * @return
     */
    private int BuscarDiagInfIzq(int[][] tablero, int jugador, int fila, int columna) {
        // System.out.println("BUSCAR DIAG INF IZQ: " + fila + columna);
        int posicionFinalFila = 0;
        int posicionFinalColumna = 0;
        int fichasContrarias = 0;
        boolean semBusqueda = true;
        int filaSig = fila + 1;
        int columnaSig = columna - 1;
        while (semBusqueda && filaSig <= 7 && columnaSig >= 0) {
            if (tablero[filaSig][columnaSig] == -1 * jugador) {
                fichasContrarias = fichasContrarias + 1;
            } else {
                semBusqueda = false;
            }
            filaSig = filaSig + 1;
            columnaSig = columnaSig - 1;
        }
        posicionFinalFila = fila + fichasContrarias + 1;
        posicionFinalColumna = columna - fichasContrarias - 1;
        if (fichasContrarias > 0 && posicionFinalFila <= 7 && posicionFinalColumna >= 0
                && tablero[posicionFinalFila][posicionFinalColumna] == jugador) {
            return fichasContrarias;
        } else {
            return 0;
        }
    }

    /**
     * @param tablero
     * @param jugador
     * @param fila
     * @param columna
     * @return
     */
    private int BuscarIzquierda(int[][] tablero, int jugador, int fila, int columna) {
        // System.out.println("BUSCAR IZQUIERDA: " + fila + columna);
        int posicionFinalColumna = 0;
        int fichasContrarias = 0;
        boolean semBusqueda = true;
        int columnaSig = columna - 1;
        while (semBusqueda && columnaSig >= 0) {
            if (tablero[fila][columnaSig] == -1 * jugador) {
                fichasContrarias = fichasContrarias + 1;
            } else {
                semBusqueda = false;
            }
            columnaSig = columnaSig - 1;
        }
        posicionFinalColumna = columna - fichasContrarias - 1;
        if (fichasContrarias > 0 && posicionFinalColumna >= 0 && tablero[fila][posicionFinalColumna] == jugador) {
            return fichasContrarias;
        } else {
            return 0;
        }
    }

    /**
     * @param tablero
     * @param jugador
     * @param fila
     * @param columna
     * @return
     */
    private int BuscarDiagSupIzq(int[][] tablero, int jugador, int fila, int columna) {
        // System.out.println("BUSCAR DIAG SUP IZQ: " + fila + columna);
        int posicionFinalFila = 0;
        int posicionFinalColumna = 0;
        int fichasContrarias = 0;
        boolean semBusqueda = true;
        int filaSig = fila - 1;
        int columnaSig = columna - 1;
        while (semBusqueda && filaSig >= 0 && columnaSig >= 0) {
            if (tablero[filaSig][columnaSig] == -1 * jugador) {
                fichasContrarias = fichasContrarias + 1;
            } else {
                semBusqueda = false;
            }
            filaSig = filaSig - 1;
            columnaSig = columnaSig - 1;
        }
        posicionFinalFila = fila - fichasContrarias - 1;
        posicionFinalColumna = columna - fichasContrarias - 1;
        if (fichasContrarias > 0 && posicionFinalFila >= 0 && posicionFinalColumna >= 0
                && tablero[posicionFinalFila][posicionFinalColumna] == jugador) {
            return fichasContrarias;
        } else {
            return 0;
        }
    }

}
