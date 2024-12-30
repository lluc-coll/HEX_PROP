package edu.upc.epsevg.prop.hex;

import java.awt.Point;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * PlayerIterativeMinimax: Implementa un jugador basado en Minimax Iterativo
 * con poda alfa-beta, una heurística avanzada, y una tabla de aperturas.
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
    public static int[][][] taulaHash; // Tabla de hash
    private Map<Integer, Point> openingTable; // Tabla de aperturas

    public PlayerIterativeMinimax(int depth, long timeoutLimit) {
        this.name = "IterativeMinimaxPlayer";
        this.maxDepth = depth;
        this.timeoutLimit = timeoutLimit;
        taulaHash = Heuristica.createHashingTable(11);
        initializeOpeningTable();
    }

    private void initializeOpeningTable() {
        openingTable = new HashMap<>();
        int hashEmptyBoard = calculateEmptyBoardHash();
        openingTable.put(hashEmptyBoard, new Point(5, 5)); // Movimiento inicial central
    }

    private int calculateEmptyBoardHash() {
        int hash = 0;
        for (int i = 0; i < 11; i++) {
            for (int j = 0; j < 11; j++) {
                hash ^= taulaHash[i][j][1]; // Valor para posición vacía
            }
        }
        return hash;
    }

    private int hashState(HexGameStatus hgs) {
        int hash = 0;
        int size = hgs.getSize();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                int cellValue = hgs.getPos(i, j);
                hash ^= taulaHash[i][j][cellValue + 1]; // +1 para indexar correctamente (-1, 0, 1)
            }
        }
        return hash;
    }

    @Override
    public PlayerMove move(HexGameStatus hgs) {
        color = hgs.getCurrentPlayerColor();
        playsExplored = 0;
        timeoutFlag = false;
        MyStatus m = new MyStatus(hgs);

        // Determinar si estamos en una apertura
        int filledCells = countFilledCells(hgs);
        if (filledCells <= 1) {
            Point openingMove = handleOpening(filledCells, hgs);
            System.out.println("Movimiento de apertura: " + openingMove);
            return new PlayerMove(openingMove, playsExplored, 1, SearchType.MINIMAX_IDS);
        }

        Point bestMove = null;
        int maxDepthReached = 0;

        for (int depth = 1; depth <= maxDepth; depth++) {
            startTime = System.currentTimeMillis();
            try {
                Point moveAtDepth = iterativeMinimax(m, depth);
                if (!timeoutFlag) {
                    bestMove = moveAtDepth;
                    maxDepthReached = depth;
                } else {
                    break;
                }
                System.out.println("Profundidad alcanzada: " + depth);
            } catch (TimeoutException e) {
                break;
            }
        }

        System.out.println("Profundidad final alcanzada antes del timeout: " + maxDepthReached);
        System.out.println("Nodos explorados: " + playsExplored);

        return new PlayerMove(bestMove, playsExplored, maxDepthReached, SearchType.MINIMAX_IDS);
    }

    private Point handleOpening(int filledCells, HexGameStatus hgs) {
        if (filledCells == 0) {
            // Jugador comienza primero, usar tabla de aperturas
            int boardHash = hashState(hgs);
            return openingTable.getOrDefault(boardHash, new Point(5, 5));
        } else if (filledCells == 1) {
            // Jugador comienza segundo, responder cerca de la ficha existente
            Point opponentMove = findFirstMove(hgs);
            return findCentralAdjacentMove(opponentMove, hgs);
        }
        return null; // No debería llegar aquí
    }

    private int countFilledCells(HexGameStatus hgs) {
        int count = 0;
        int size = hgs.getSize();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (hgs.getPos(i, j) != 0) {
                    count++;
                }
            }
        }
        return count;
    }

    private Point findFirstMove(HexGameStatus hgs) {
        int size = hgs.getSize();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (hgs.getPos(i, j) != 0) {
                    return new Point(i, j);
                }
            }
        }
        return null; // Nunca debería llegar aquí si `countFilledCells` es correcto
    }

    private Point findCentralAdjacentMove(Point opponentMove, HexGameStatus hgs) {
        int[][] directions = {
            {-1, 0}, {1, 0}, {0, -1}, {0, 1}, // Adyacentes
            {-1, -1}, {-1, 1}, {1, -1}, {1, 1} // Diagonales
        };

        for (int[] dir : directions) {
            int newX = opponentMove.x + dir[0];
            int newY = opponentMove.y + dir[1];
            if (newX >= 0 && newY >= 0 && newX < hgs.getSize() && newY < hgs.getSize()) {
                if (hgs.getPos(newX, newY) == 0) {
                    return new Point(newX, newY); // Retorna la primera posición adyacente libre
                }
            }
        }

        return new Point(5, 5); // Movimiento central como respaldo
    }

    @Override
    public void timeout() {
        timeoutFlag = true;
    }

    @Override
    public String getName() {
        return name;
    }

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

    private int valorMax(MyStatus status, int depth, int alpha, int beta) throws TimeoutException {
        checkTimeout();
        if (depth == 0 || status.isGameOver()) {
            playsExplored++;
            return h.heuristica(status.graf1, status.graf2, status.ini, status.end);
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
                break;
            }
        }

        return maxScore;
    }

    private int valorMin(MyStatus status, int depth, int alpha, int beta) throws TimeoutException {
        checkTimeout();
        if (depth == 0 || status.isGameOver()) {
            playsExplored++;
            return h.heuristica(status.graf1, status.graf2, status.ini, status.end);
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
                break;
            }
        }

        return minScore;
    }

    private void checkTimeout() throws TimeoutException {
        if (System.currentTimeMillis() - startTime > timeoutLimit) {
            timeoutFlag = true;
            throw new TimeoutException("Timeout reached!");
        }
    }

    private static class TimeoutException extends Exception {
        public TimeoutException(String message) {
            super(message);
        }
    }
}
