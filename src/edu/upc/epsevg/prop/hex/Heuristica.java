/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.upc.epsevg.prop.hex;

import java.awt.Point;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

/**
 *
 * @author llucc
 */
public class Heuristica {

    public int heuristica(Map<Point, List<Map.Entry<Point, Integer>>> graf1, Map<Point, List<Map.Entry<Point, Integer>>> graf2, Point ini, Point end) {
        int dij1 = dijkstra(graf1, ini, end);
        int dij2 = dijkstra(graf2, ini, end);

        System.out.println(dij1 + ":" + dij2);
        return 0;
    }

    public int dijkstra(Map<Point, List<Map.Entry<Point, Integer>>> grafMap, Point start, Point target) {
        PriorityQueue<Map.Entry<Point, Integer>> PQ = new PriorityQueue<>(Map.Entry.comparingByValue());
        Set<Point> visited = new HashSet<>();

        Map<Point, Integer> distances = new HashMap<>();
        /*for (Point node : grafMap.keySet()) {
            distances.put(node, Integer.MAX_VALUE);
        }*/
        distances.put(start, 0);
        PQ.add(new AbstractMap.SimpleEntry<>(start, 0));

        while (!PQ.isEmpty()) {
            Map.Entry<Point, Integer> current = PQ.poll();
            Point currentP = current.getKey();
            if (currentP.equals(target)) {
                /*for (int i = 0; i < 11; i++) {
                    for(int s=0; s<i;s++){System.out.print(" ");}
                    for (int j = 0; j < 11; j++) {
                        System.out.print(distances.get(new Point(j, i)) + " ");
                    }
                    System.out.println();
                }*/
                System.out.println("a");
                return distances.get(currentP);
            }

            if (!visited.contains(currentP)) {
                visited.add(currentP);
                List<Map.Entry<Point, Integer>> ngbs = grafMap.get(currentP);
                if (ngbs != null) {
                    for (Map.Entry<Point, Integer> ngb : ngbs) {
                        Point ngbP = ngb.getKey();
                        int dist = ngb.getValue();

                        if (!visited.contains(ngbP)) {
                            int newDist = distances.get(currentP) + dist;
                            if (newDist < distances.getOrDefault(ngbP, Integer.MAX_VALUE)) {
                                distances.put(ngbP, newDist);
                                PQ.add(new AbstractMap.SimpleEntry<>(ngbP, newDist));
                            }
                        }
                    }
                }
            }
        }

        return -1;
    }

    public List<MoveNode> obtenerJugadas(HexGameStatus h) {
        return h.getMoves();
    }
}
