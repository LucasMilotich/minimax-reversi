package minmax;

import java.util.ArrayList;
import java.util.List;

import reversi.Celda;
import reversi.JugadorReversi;



public class JugadorReversiImpl implements JugadorReversi {

    final int BOT = 1;
    final int HUMANO = -1;

    public int nodosExplorados = 0;

    @Override
    public Celda devolverJugadaOptima(int[][] tablero) {
        return CalcularPuntaje(tablero, BOT, 8);
    }

    public Celda CalcularPuntaje(int[][] tablero, int jugador, int control) {
        nodosExplorados = 0;

        Celda jugada = new Celda(-1, -1);
        int maxPuntaje = Integer.MIN_VALUE;
        int alpha = Integer.MIN_VALUE;
        int beta = Integer.MAX_VALUE;
        int puntaje = 0;

        boolean semBusqueda = true;
        List<Celda> posiblesJugadas = BuscarPosiblesJugadas(tablero, BOT);
        int i = 0;
        
        if(posiblesJugadas.size()>0) {
        	jugada = posiblesJugadas.get(0);
        }
        while (semBusqueda && i < posiblesJugadas.size()) {
            int[][] tableroHijo = MarcarTablero(tablero, posiblesJugadas.get(i), BOT);
            
            if (!HayMovimientos(tableroHijo)) {
            	if(DiferenciaFichasBot(tableroHijo)>0) { //Si es jugada ganadora, corta y no evalua el resto de posibles jugadas
            		semBusqueda = false;
                	jugada = posiblesJugadas.get(i);
            	}
            	else {
            		i++;
            	}
            }else{
	            puntaje = valorMinMax(tableroHijo, HUMANO, control, alpha, beta);
	            if (puntaje > maxPuntaje) { 
	                maxPuntaje = puntaje;
	                jugada = posiblesJugadas.get(i);
	            }
	            i++;
            }
        }
        System.out.println("Nodos explorados: " + nodosExplorados);
        System.out.println("Diferencia fichas O: " + DiferenciaFichasBot(MarcarTablero(tablero, jugada, BOT)));
        return jugada;
    }

    private int valorMinMax(int[][] tablero, int jugador, int control, int alpha, int beta) {
    	nodosExplorados++;
        int valor = 0;
   
        if(beta<=alpha){
        	//System.out.println("Corte en nivel "+ (8 - control));
        	if(jugador == BOT) return Integer.MAX_VALUE; else return Integer.MIN_VALUE;
        }
        if (TableroCompleto(tablero) || !HayMovimientos(tablero) || control <= 0 || nodosExplorados>13000000) { 
            return EvaluarTablero(tablero);
        } else {
            List<Celda> hijos = BuscarPosiblesJugadas(tablero, jugador); 
            
            if (jugador == BOT) {
                valor = Integer.MIN_VALUE;
            } else {
                valor = Integer.MAX_VALUE;
            }
            
            if (hijos.size() == 0) {
                return valorMinMax(tablero, jugador*-1, control, alpha, beta); 
            }
           
            int i = 0;
            boolean podar = false;
            while ( i < hijos.size() && !podar) {
            	int[][] tableroHijo = MarcarTablero(tablero, hijos.get(i), jugador);
            	int v = 0;
                if (jugador == BOT) {
                	v = valorMinMax(tableroHijo, HUMANO, control-1, alpha, beta);
                    valor = Math.max(valor, v);
                    alpha = Math.max(alpha, v); 
                } else {
                	v = valorMinMax(tableroHijo, BOT, control-1, alpha, beta);
                    valor = Math.min(valor, v);                   
                    beta = Math.min(beta, v);
                }
                if(v == Integer.MIN_VALUE || v == Integer.MAX_VALUE ) { 
                	podar = true;
                }
                i++;
            }
            return valor;      	
        }
    }


    private boolean TableroCompleto(int[][] tablero) { //Chequea si el tablero se completo
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

    private boolean HayMovimientos(int[][] tablero) { //Chequea que ambos jugadores tengan movimientos
        boolean hayMovimientos = true;

        List<Celda> movimientosJugador = BuscarPosiblesJugadas(tablero, 1);
        List<Celda> movimientosOponente = BuscarPosiblesJugadas(tablero, -1);
       
        if (movimientosJugador.size() == 0 && movimientosOponente.size() == 0)
            hayMovimientos = false;

        return hayMovimientos;
    }

    private int DiferenciaFichasBot(int[][] tablero) {
        int resultado = 0;

        for (int fila = 0; fila <= 7; fila++) {
            for (int columna = 0; columna <= 7; columna++) {
                resultado += tablero[fila][columna];
            }
        }
        return resultado;
    }
    
    private int EvaluarTablero(int[][] tablero) {
        int[] heuristica = new int[]{100, -10, 10,  7,  7, 10, -10, 100,
                                     -10, -25, -5, -3, -3, -5, -25, -10,  
                                      10,  -5,  7,  4,  4,  7,  -5,  10, 
                                       7,  -3,  4,  1,  1,  4,  -3,  7 ,  
                                       7,  -3,  4,  1,  1,  4,  -3,  7 , 
                                      10,  -5,  7,  4,  4,  7,  -5,  10, 
                                     -10, -25, -5, -3, -3, -5, -25, -10, 
                                     100, -10, 10,  7,  7, 10, -10, 100 };
       
        int i = 0;
        int resultado = 0;
        for (int fila = 0; fila <= 7; fila++) {
            for (int columna = 0; columna <= 7; columna++) {
                resultado += tablero[fila][columna] * heuristica[i];
                i++;
            }
        }
        return resultado;
    }

    private int[][] MarcarTablero(int[][] tableroAnterior, Celda proxPos, int jugador) {
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

        return tablero;
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
        int fichasContrarias = 0;
        boolean semBusqueda = true;
        int filaSig = fila - 1;
        while (semBusqueda && filaSig >= 0) {
            if (tablero[filaSig][columna] == -1 * jugador) {
                fichasContrarias = fichasContrarias + 1;
                filaSig = filaSig - 1;
            } else {
                semBusqueda = false;
            }            
        }
  
        if (fichasContrarias > 0 && filaSig >= 0 && tablero[filaSig][columna] == jugador) {
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
        int fichasContrarias = 0;
        boolean semBusqueda = true;
        int filaSig = fila - 1;
        int columnaSig = columna + 1;
        while (semBusqueda && filaSig >= 0 && columnaSig <= 7) {
            if (tablero[filaSig][columnaSig] == -1 * jugador) {
                fichasContrarias = fichasContrarias + 1;
                filaSig = filaSig - 1;
                columnaSig = columnaSig + 1;
            } else {
                semBusqueda = false;
            }
        }
    
        if (fichasContrarias > 0 && filaSig >= 0 && columnaSig <= 7
                && tablero[filaSig][columnaSig] == jugador) {
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
        int fichasContrarias = 0;
        boolean semBusqueda = true;
        int columnaSig = columna + 1;
        while (semBusqueda && columnaSig <= 7) {
            if (tablero[fila][columnaSig] == -1 * jugador) {
                fichasContrarias = fichasContrarias + 1;
                columnaSig = columnaSig + 1;
            } else {
                semBusqueda = false;
            }
        }
     
        if (fichasContrarias > 0 && columnaSig <= 7 && tablero[fila][columnaSig] == jugador) {
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
        int fichasContrarias = 0;
        boolean semBusqueda = true;
        int filaSig = fila + 1;
        int columnaSig = columna + 1;
        while (semBusqueda && filaSig <= 7 && columnaSig <= 7) {
            if (tablero[filaSig][columnaSig] == -1 * jugador) {
                fichasContrarias = fichasContrarias + 1;
                filaSig = filaSig + 1;
                columnaSig = columnaSig + 1;
            } else {
                semBusqueda = false;
            }
        }
        
        if (fichasContrarias > 0 && filaSig <= 7 && columnaSig <= 7
                && tablero[filaSig][columnaSig] == jugador) {
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
        int fichasContrarias = 0;
        boolean semBusqueda = true;
        int filaSig = fila + 1;
        while (semBusqueda && filaSig <= 7) {
            if (tablero[filaSig][columna] == -1 * jugador) {
                fichasContrarias = fichasContrarias + 1;
                filaSig = filaSig + 1;
            } else {
                semBusqueda = false;
            }
        }
 
        if (fichasContrarias > 0 && filaSig <= 7 && tablero[filaSig][columna] == jugador) {
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
        int fichasContrarias = 0;
        boolean semBusqueda = true;
        int filaSig = fila + 1;
        int columnaSig = columna - 1;
        while (semBusqueda && filaSig <= 7 && columnaSig >= 0) {
            if (tablero[filaSig][columnaSig] == -1 * jugador) {
                fichasContrarias = fichasContrarias + 1;
                filaSig = filaSig + 1;
                columnaSig = columnaSig - 1;
            } else {
                semBusqueda = false;
            }          
        }
        
        if (fichasContrarias > 0 && filaSig <= 7 && columnaSig >= 0
                && tablero[filaSig][columnaSig] == jugador) {
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
        int fichasContrarias = 0;
        boolean semBusqueda = true;
        int columnaSig = columna - 1;
        while (semBusqueda && columnaSig >= 0) {
            if (tablero[fila][columnaSig] == -1 * jugador) {
                fichasContrarias = fichasContrarias + 1;
                columnaSig = columnaSig - 1;
            } else {
                semBusqueda = false;
            }           
        }

        if (fichasContrarias > 0 && columnaSig >= 0 && tablero[fila][columnaSig] == jugador) {
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
        int fichasContrarias = 0;
        boolean semBusqueda = true;
        int filaSig = fila - 1;
        int columnaSig = columna - 1;
        while (semBusqueda && filaSig >= 0 && columnaSig >= 0) {
            if (tablero[filaSig][columnaSig] == -1 * jugador) {
                fichasContrarias = fichasContrarias + 1;
                filaSig = filaSig - 1;
                columnaSig = columnaSig - 1;
            } else {
                semBusqueda = false;
            }
           
        }
        
        if (fichasContrarias > 0 && filaSig >= 0 && columnaSig >= 0
                && tablero[filaSig][columnaSig] == jugador) {
            return fichasContrarias;
        } else {
            return 0;
        }
    }

}
