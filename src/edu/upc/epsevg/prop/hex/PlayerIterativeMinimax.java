package edu.upc.epsevg.prop.hex;

import java.awt.Point;
import java.util.List;

/**
 * PlayerIterativeMinimax: Implementa un jugador basado en Minimax Iterativo
 * con poda alfa-beta y una heurística avanzada.
 */
public class PlayerIterativeMinimax implements IPlayer, IAuto {
    private Heuristica h = new Heuristica(); // Instancia de Heuristica
    private String name; // Nombre del jugador
    private int maxDepth; // Profundidad máxima para el algoritmo Minimax
    private long playsExplored; // Nodos explorados durante la búsqueda
    private int color; // Color del jugador
    private boolean timeoutFlag; // Bandera para timeout
    private long startTime; // Tiempo inicial
    private long timeoutLimit; // Tiempo máximo permitido en milisegundos

    /**
     * Constructor del jugador.
     *
     * @param depth        Profundidad máxima permitida para Minimax.
     * @param timeoutLimit Tiempo máximo permitido en milisegundos.
     */
    public PlayerIterativeMinimax(int depth, long timeoutLimit) {
        this.name = "IterativeMinimaxPlayer";
        this.maxDepth = depth;
        this.timeoutLimit = timeoutLimit;
    }

    @Override
    public PlayerMove move(HexGameStatus hgs) {
        color = hgs.getCurrentPlayerColor();
        playsExplored = 0;
        timeoutFlag = false;
        MyStatus m = new MyStatus(hgs);

        Point bestMove = null;
        int maxDepthReached = 0; // Variable para rastrear la profundidad máxima alcanzada

        // Iterative deepening: profundiza hasta timeout o hasta alcanzar maxDepth
        for (int depth = 1; depth <= maxDepth; depth++) {
            startTime = System.currentTimeMillis();

            try {
                Point moveAtDepth = iterativeMinimax(m, depth);

                if (!timeoutFlag) {
                    bestMove = moveAtDepth; // Actualizamos si completamos la profundidad
                    maxDepthReached = depth; // Actualizamos la profundidad alcanzada
                } else {
                    break; // Salimos del bucle si se alcanza el timeout
                }

                // Imprime la profundidad alcanzada al final de cada iteración
                System.out.println("Profundidad alcanzada: " + depth);

            } catch (TimeoutException e) {
                break; // Salimos del bucle al alcanzar el timeout
            }
        }

        // Imprime la profundidad final antes de salir
        System.out.println("Profundidad final alcanzada antes del timeout: " + maxDepthReached);
        System.out.println("Nodos explorados: " + playsExplored);

        return new PlayerMove(bestMove, playsExplored, maxDepthReached, SearchType.MINIMAX_IDS);
    }


    @Override
    public void timeout() {
        timeoutFlag = true; // Señal de timeout
    }

    @Override
    public String getName() {
        return name;
    }

    /**
     * Minimax iterativo hasta la profundidad especificada.
     *
     * @param status Estado actual del tablero (MyStatus).
     * @param depth  Profundidad máxima para esta iteración.
     * @return Mejor movimiento calculado.
     * @throws TimeoutException Si se excede el tiempo límite.
     */
    private Point iterativeMinimax(MyStatus status, int depth) throws TimeoutException {
        int bestScore = Integer.MIN_VALUE;
        Point bestMove = null;

        List<MoveNode> movimientos = h.obtenerJugadas(status);

        for (MoveNode move : movimientos) {
            checkTimeout();

            MyStatus newState = new MyStatus(status);
            newState.placeStone(move.getPoint());

            int score = valorMin(newState, depth - 1, Integer.MIN_VALUE, Integer.MAX_VALUE);

            if (score > bestScore) {
                bestScore = score;
                bestMove = move.getPoint();
            }
        }

        return bestMove;
    }

    /**
     * Minimax para el jugador MAX.
     */
    public int valorMax(MyStatus status, int depth, int alpha, int beta) throws TimeoutException {
        checkTimeout();

        if (depth == 0 || status.isGameOver()) {
            playsExplored++;
            return h.heuristica(status.graf1, status.graf2, status.ini, status.end); // Evaluación heurística
        }

        int maxScore = Integer.MIN_VALUE;
        List<MoveNode> movimientos = h.obtenerJugadas(status);

        for (MoveNode move : movimientos) {
            MyStatus newState = new MyStatus(status);
            newState.placeStone(move.getPoint());

            int score = valorMin(newState, depth - 1, alpha, beta);
            maxScore = Math.max(maxScore, score);

            alpha = Math.max(alpha, maxScore);
            if (alpha >= beta) {
                break; // Poda beta
            }
        }

        return maxScore;
    }

    /**
     * Minimax para el jugador MIN.
     */
    public int valorMin(MyStatus status, int depth, int alpha, int beta) throws TimeoutException {
        checkTimeout();

        if (depth == 0 || status.isGameOver()) {
            playsExplored++;
            return h.heuristica(status.graf1, status.graf2, status.ini, status.end); // Evaluación heurística
        }

        int minScore = Integer.MAX_VALUE;
        List<MoveNode> movimientos = h.obtenerJugadas(status);

        for (MoveNode move : movimientos) {
            MyStatus newState = new MyStatus(status);
            newState.placeStone(move.getPoint());

            int score = valorMax(newState, depth - 1, alpha, beta);
            minScore = Math.min(minScore, score);

            beta = Math.min(beta, minScore);
            if (alpha >= beta) {
                break; // Poda alfa
            }
        }

        return minScore;
    }

    /**
     * Verifica si el tiempo límite ha sido alcanzado.
     *
     * @throws TimeoutException Si se supera el tiempo límite.
     */
    private void checkTimeout() throws TimeoutException {
        if (System.currentTimeMillis() - startTime > timeoutLimit) {
            timeoutFlag = true;
            throw new TimeoutException("Timeout reached!");
        }
    }

    /**
     * Excepción personalizada para manejar el timeout.
     */
    private static class TimeoutException extends Exception {
        public TimeoutException(String message) {
            super(message);
        }
    }
}
