/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.upc.epsevg.prop.hex;

import java.awt.Point;
import java.util.List;

/**
 *
 *
 * @author llucc
 */
public class PlayerID implements IPlayer, IAuto {

    /**
     * Instància de la classe Heuristica utilitzada per avaluar l'estat del joc.
     */
    Heuristica h = Heuristica.getInstance();

    /**
     * Nom del jugador.
     */
    String name;

    /**
     * Bandera per detectar si s'ha produït un timeout.
     */
    boolean timeoutFlag;

    /**
     * Nombre total de nodes explorats durant la cerca.
     */
    long playsExplored;

    /**
     * Profunditat actual de la cerca.
     */
    int depth;

    /**
     * Color actual del jugador.
     */
    int color;

    /**
     * Millor moviment seleccionat durant la cerca.
     */
    Point bestMove;

    /**
     * Constructor del jugador. Inicialitza el nom del jugador i les taules de
     * dispersió.
     */
    public PlayerID() {
        this.name = "HEXpertos";
    }

    /**
     * Selecciona el millor moviment utilitzant una cerca iterativa amb Minimax.
     *
     * @param hgs L'estat actual del joc (HexGameStatus).
     * @return Un objecte {@link PlayerMove} amb el millor moviment calculat.
     */
    @Override
    public PlayerMove move(HexGameStatus hgs) {
        if (h.taulaHash == null) {
            h.taulaHash = Heuristica.createHashingTable(hgs.getSize());
        }
        timeoutFlag = false;
        color = hgs.getCurrentPlayerColor();
        playsExplored = 0;
        MyStatus m = new MyStatus(hgs);
        bestMove = null;
        depth = 1;

        while (!timeoutFlag) {
            try {
                bestMove = iterativeMinimax(m, depth);
                depth++;
            } catch (TimeoutException e) {
                break;
            }
        }
        return new PlayerMove(bestMove, playsExplored, depth - 1, SearchType.MINIMAX_IDS);
    }

    /**
     * Marca la bandera de timeout com a activada.
     */
    @Override
    public void timeout() {
        timeoutFlag = true;
    }

    /**
     * Retorna el nom del jugador.
     *
     * @return Nom del jugador.
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Realitza la cerca iterativa utilitzant l'algorisme Minimax fins a la
     * profunditat donada.
     *
     * @param status L'estat actual del joc.
     * @param depth La profunditat màxima per a aquesta iteració.
     * @throws TimeoutException Si es produeix un timeout durant la cerca.
     */
    private Point iterativeMinimax(MyStatus status, int depth) throws TimeoutException {
        int bestScore = Integer.MIN_VALUE;
        List<MoveNode> movimientos = h.obtenerJugadas(status);
        Point best = movimientos.get(0).getPoint();

        for (MoveNode move : movimientos) {
            if (!h.pruned.contains(status.getNewHash(move.getPoint()))) {
                checkTimeout();
                MyStatus newState = new MyStatus(status);
                newState.placeStone(move.getPoint());
                int score = valorMin(newState, depth - 1, Integer.MIN_VALUE, Integer.MAX_VALUE);
                if (score > bestScore) {
                    bestScore = score;
                    best = move.getPoint();
                }
            }
        }
        return best;
    }

    /**
     * Calcula el valor màxim de Minimax per a un estat donat.
     *
     * @param status L'estat actual del joc.
     * @param depth La profunditat restant.
     * @param alpha El millor valor garantit per al jugador MAX.
     * @param beta El millor valor garantit per al jugador MIN.
     * @return El valor màxim calculat per a l'estat actual.
     * @throws TimeoutException Si es produeix un timeout durant la cerca.
     */
    private int valorMax(MyStatus status, int depth, int alpha, int beta) throws TimeoutException {
        checkTimeout();
        int maxScore = Integer.MIN_VALUE;
        if (status.isGameOver() && status.GetWinner() != null) {
            return maxScore;
        }
        if (depth == 0 || status.isGameOver()) {
            playsExplored++;
            return h.heuristica(status.graf1, status.graf2, status.ini, status.end);
        }

        List<MoveNode> movimientos = h.obtenerJugadas(status);

        for (MoveNode move : movimientos) {
            if (!h.pruned.contains(status.getNewHash(move.getPoint()))) {
                MyStatus newState = new MyStatus(status);
                newState.placeStone(move.getPoint());
                int score = valorMin(newState, depth - 1, alpha, beta);
                if (score > maxScore) {
                    maxScore = score;
                    h.millorJugada.put(status.hash, move.getPoint());
                }
                alpha = Math.max(alpha, maxScore);
                if (alpha >= beta) {
                    h.pruned.add(status.hash);
                    break;
                }
            }
        }

        return maxScore;
    }

    /**
     * Calcula el valor mínim de Minimax per a un estat donat.
     *
     * @param status L'estat actual del joc.
     * @param depth La profunditat restant.
     * @param alpha El millor valor garantit per al jugador MAX.
     * @param beta El millor valor garantit per al jugador MIN.
     * @return El valor mínim calculat per a l'estat actual.
     * @throws TimeoutException Si es produeix un timeout durant la cerca.
     */
    private int valorMin(MyStatus status, int depth, int alpha, int beta) throws TimeoutException {
        checkTimeout();
        int minScore = Integer.MAX_VALUE;
        if (status.isGameOver() && status.GetWinner() != null) {
            return minScore;
        }
        if (depth == 0 || status.isGameOver()) {
            playsExplored++;
            return h.heuristica(status.graf1, status.graf2, status.ini, status.end);
        }

        List<MoveNode> movimientos = h.obtenerJugadas(status);

        for (MoveNode move : movimientos) {
            if (!h.pruned.contains(status.getNewHash(move.getPoint()))) {
                MyStatus newState = new MyStatus(status);
                newState.placeStone(move.getPoint());
                int score = valorMax(newState, depth - 1, alpha, beta);
                if (score < minScore) {
                    minScore = score;
                    h.millorJugada.put(status.hash, move.getPoint());
                }
                beta = Math.min(beta, minScore);
                if (alpha >= beta) {
                    h.pruned.add(status.hash);
                    break;
                }
            }
        }

        return minScore;
    }

    /**
     * Comprova si s'ha produït un timeout.
     *
     * @throws TimeoutException Si el timeout s'ha activat.
     */
    private void checkTimeout() throws TimeoutException {
        if (timeoutFlag) {
            throw new TimeoutException("Timeout reached!");
        }
    }

    /**
     * Excepció personalitzada per gestionar el timeout.
     */
    private static class TimeoutException extends Exception {

        /**
         * Constructor de l'excepció TimeoutException.
         *
         * @param message El missatge de l'excepció.
         */
        public TimeoutException(String message) {
            super(message);
        }
    }
}
