/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.upc.epsevg.prop.hex;

import java.util.List;

/**
 *
 * @author llucc
 */
public class Heuristica {
    public int iniDijkstra(MyStatus h, int color){
        int val = 0, max = 0;
        for (int i = 0; i < h.getSize(); i++){
            if (color == 1){
                val = dijkstra(h, color, i, 0);
            }
            else{
                val = dijkstra(h, color, 0, i);
            }
            
            if ()
        }
        return val;
    }
    
    public int dijkstra(MyStatus h, int color, int i, int j){
        int val = 0, max = 0;
        if (h.getPos(i, j) == color){
            max++;
        }
        
    }
    
    public List<MoveNode> obtenerJugadas(HexGameStatus h) {
        return h.getMoves();
    }
}
