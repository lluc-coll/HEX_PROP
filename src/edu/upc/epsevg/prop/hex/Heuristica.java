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

    public int heuristica(MyStatus m, int color) {
        Map<Point, List<Map.Entry<Point, Integer[]>>> grafMap;
        Point startU = new Point(-1, -1);
        Point targetU = new Point(m.getSize(), m.getSize());
        Point startE = new Point(-2, -2);
        Point targetE = new Point(m.getSize() + 1, m.getSize() + 1);
        if (color == 1) {
            grafMap = getTableGraph(m, startU, startE, targetU, targetE);
        } else {
            grafMap = getTableGraph(m, startE, startU, targetE, targetU);
        }
        // crear grafMap per nosaltres i per enemic
        int dijU = dijkstra(m, grafMap, startU, targetU, -color);
        int dijE = dijkstra(m, grafMap, startE, targetE, color);
        System.out.println(dijU + ":" + dijE);
        // resta de dijU-dijE per fer la valoracio del tauler
        return dijU - dijE;
    }

    public int dijkstra(MyStatus m, Map<Point, List<Map.Entry<Point, Integer[]>>> grafMap, Point start, Point target, int colEne) {
        PriorityQueue<Map.Entry<Point, Integer>> PQ = new PriorityQueue<>(Map.Entry.comparingByValue());
        Set<Point> visited = new HashSet<>();

        Map<Point, Integer> distances = new HashMap<>();
        /*for (Point node : grafMap.keySet()) {
            distances.put(node, Integer.MAX_VALUE);
        }*/
        distances.put(start, 0);
        PQ.add(new AbstractMap.SimpleEntry<>(start, -1));

        while (!PQ.isEmpty()) {
            Map.Entry<Point, Integer> current = PQ.poll();
            Point currentP = current.getKey();
            if (currentP.equals(target)) {
                return distances.get(currentP);
            }
            
            if (!visited.contains(currentP)) {
                visited.add(currentP);
                List<Map.Entry<Point, Integer[]>> ngbs = grafMap.get(currentP);
                if (ngbs != null) {
                    for (Map.Entry<Point, Integer[]> ngb : ngbs) {
                        Point ngbP = ngb.getKey();
                        System.out.println((colEne+1)/2);
                        int dist = ngb.getValue()[(-colEne+1)/2];
                        if (ngbP.x<m.getSize() && ngbP.y<m.getSize() &&m.getPos(ngbP) == -colEne){
                            //dist = 0;
                            //System.out.println(currentP+":"+ngbP+"."+-colEne+":"+m.getPos(ngbP)+"."+dist);
                            
                        }
                        
                        if (!visited.contains(ngbP) && dist != -1) {
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

    public Map<Point, List<Map.Entry<Point, Integer[]>>> getTableGraph(MyStatus m, Point startU, Point startE, Point targetU, Point targetE) {
        Map<Point, List<Map.Entry<Point, Integer[]>>> map = new HashMap<>();
        for (int i = 0; i < m.getSize(); i++) {
            for (int j = 0; j < m.getSize(); j++) {
                Point p = new Point(i, j);
                map.put(p, getNeighbors(m, p));
                if (i == 0) {
                    map.putIfAbsent(startU, new ArrayList<>());
                    map.get(startU).add(new AbstractMap.SimpleEntry<>(p, new Integer[]{1, 1}));
                }
                if (i == m.getSize() - 1) {
                    map.get(p).add(new AbstractMap.SimpleEntry<>(targetU, new Integer[]{0, 0}));
                }
                if (j == 0) {
                    map.putIfAbsent(startE, new ArrayList<>());
                    map.get(startE).add(new AbstractMap.SimpleEntry<>(p, new Integer[]{1, 1}));
                }
                if (j == m.getSize() - 1) {
                    map.get(p).add(new AbstractMap.SimpleEntry<>(targetE, new Integer[]{0, 0}));
                }
            }
        }
        System.out.println(map);
        return map;
    }
    
    public List<Map.Entry<Point, Integer[]>> getNeighbors(MyStatus m, Point p) {
        List<Map.Entry<Point, Integer[]>> ngbs = new ArrayList<>();
        int[][] directions = {
            {1, 0}, // Derecha
            {1, -1}, // Abajo-derecha
            {0, -1}, // Abajo-izquierda
            {-1, 0}, // Izquierda
            {-1, 1}, // Arriba-izquierda
            {0, 1} // Arriba-derecha
        };

        for (int[] dir : directions) {
            int x = p.x + dir[0];
            int y = p.y + dir[1];
            if (x >= 0 && y >= 0 && x < m.getSize() && y < m.getSize()) {         
                Integer[] val = {1, 1};
                if (m.getPos(x, y) != 0){
                    val[(m.getCurrentPlayerColor()+1)/2] = 0;
                    val[(-m.getCurrentPlayerColor()+1)/2] = -1;
                }
                ngbs.add(new AbstractMap.SimpleEntry<>(new Point(x, y), val));
            }
        }

        return ngbs;
    }
}
