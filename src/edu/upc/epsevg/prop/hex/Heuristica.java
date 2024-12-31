package edu.upc.epsevg.prop.hex;

import java.awt.Point;
import java.util.*;
import java.util.Map.Entry;

/**
 * @author llucc
 */
public class Heuristica {

    /**
     * Instància única de la classe (Singleton).
     */
    private static Heuristica instance;

    /**
     * Taula de hashing utilitzada per generar identificadors únics dels estats del joc.
     */
    public int[][][] taulaHash;

    /**
     * Map que guarda les millors jugades calculades per a cada hash.
     */
    Map<Integer, Point> millorJugada = new HashMap<>();

    /**
     * Conjunt d'estats descartats per la poda alfa-beta.
     */
    Set<Integer> pruned = new HashSet<>();

    /**
     * Funció heurística per avaluar l'estat del joc.
     * <p>
     * Aquesta funció calcula el valor heurístic en funció de les distàncies mínimes
     * dels dos jugadors entre els extrems del tauler (`ini` i `end`).
     * </p>
     * 
     * @param graf1 Graf que representa les connexions del jugador actual.
     * @param graf2 Graf que representa les connexions del jugador oponent.
     * @param ini Node inicial virtual del graf.
     * @param end Node final virtual del graf.
     * @return El valor heurístic calculat per a l'estat actual.
     */
    public int heuristica(Map<Point, Map<Point, Integer>> graf1, Map<Point, Map<Point, Integer>> graf2, Point ini, Point end) {
        int dij1 = dijkstra(graf1, ini, end);
        int dij2 = dijkstra(graf2, ini, end);
        return (1000 - dij1) - (1000 - dij2);
    }

    /**
     * Algorisme de Dijkstra per calcular la distància mínima entre dos nodes.
     * 
     * @param grafMap El graf que representa les connexions del tauler.
     * @param start El node inicial.
     * @param target El node objectiu.
     * @return La distància mínima entre els dos nodes, o -1 si no hi ha camí.
     */
    public int dijkstra(Map<Point, Map<Point, Integer>> grafMap, Point start, Point target) {
        Map<Point, Integer> distances = new HashMap<>();
        PriorityQueue<Point> PQ = new PriorityQueue<>(Comparator.comparingInt(distances::get));
        Set<Point> visited = new HashSet<>();
        
        distances.put(start, 0);
        PQ.add(start);

        while (!PQ.isEmpty()) {
            Point currentP = PQ.poll();
            if (currentP.equals(target)) {
                return distances.get(currentP);
            }

            if (!visited.contains(currentP)) {
                visited.add(currentP);
                Map<Point, Integer> ngbs = grafMap.get(currentP);
                if (ngbs != null) {
                    for (Entry<Point, Integer> ngb : ngbs.entrySet()) {
                        Point ngbP = ngb.getKey();
                        int dist = ngb.getValue();

                        if (!visited.contains(ngbP)) {
                            int newDist = distances.get(currentP) + dist;
                            if (newDist < distances.getOrDefault(ngbP, Integer.MAX_VALUE)) {
                                distances.put(ngbP, newDist);
                                PQ.add(ngbP);
                            }
                        }
                    }
                }
            }
        }

        return -1;
    }

    /**
     * Obté la llista de moviments possibles des de l'estat actual del joc.
     * <p>
     * Si l'estat actual no està marcat com a descartat (pruned) i hi ha
     * una jugada millor guardada, aquesta es prioritza en la llista.
     * </p>
     * 
     * @param h L'estat actual del joc.
     * @return Una llista ordenada de moviments possibles.
     */
    public List<MoveNode> obtenerJugadas(MyStatus h) {
        List<MoveNode> l = h.getMoves();
        if (!pruned.contains(h.hash) && millorJugada.containsKey(h.hash) && h.getPos(millorJugada.get(h.hash)) == 0) {
            l.remove(new MoveNode(new Point(millorJugada.get(h.hash))));
            l.addFirst(new MoveNode(new Point(millorJugada.get(h.hash))));
        }
        return l;
    }

    /**
     * Crea una taula de hashing Zobrist per generar identificadors únics dels estats.
     * 
     * @param size La mida del tauler.
     * @return Una matriu tridimensional amb valors aleatoris per a cada combinació de posició i estat.
     */
    public static int[][][] createHashingTable(int size) {
        int[][][] table = new int[size][size][3];
        Random ran = new Random();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                for (int z = 0; z < 3; z++) {
                    table[i][j][z] = ran.nextInt();
                }
            }
        }
        return table;
    }

    /**
     * Obté la instància única de la classe Heuristica (Singleton).
     * 
     * @return La instància de Heuristica.
     */
    public static Heuristica getInstance() {
        if (instance == null) {
            instance = new Heuristica();
        }
        return instance;
    }
}
