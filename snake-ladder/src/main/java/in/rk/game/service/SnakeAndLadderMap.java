package in.rk.game.service;

import java.util.HashMap;
import java.util.Map;

public class SnakeAndLadderMap {

    public static final Map<Integer,Integer> MAP=new HashMap<>();

    static {
        //Snake MAP : 97-78, 95-56, 88-24, 63-18, 48-26, 36-6, 32-10
        MAP.put(97,78);
        MAP.put(95,56);
        MAP.put(88,24);
        MAP.put(63,18);
        MAP.put(48,26);
        MAP.put(36,6);
        MAP.put(32,10);
        //Ladder MAP: 1-38, 4-14, 8-30, 21-42, 28-76, 50-67, 71-92, 80-99
        MAP.put(1,38);
        MAP.put(4,14);
        MAP.put(8,30);
        MAP.put(21,42);
        MAP.put(28,76);
        MAP.put(50,67);
        MAP.put(71,92);
        MAP.put(80,99);
    }
    public static int getPosition(int position)
    {
        return MAP.getOrDefault(position,position);
    }
}
