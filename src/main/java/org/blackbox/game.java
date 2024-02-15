package org.blackbox;
import java.util.Random;
import java.util.HashSet;
public class game {

    public void atomSelection() {
        Random rand = new Random();
        HashSet<String> usedHexes = new HashSet<>(); // Using HashSet to prevent duplicate atom positions

        for(int i=0;i<6;i++){
            int x, y, z;
            do {
                x = rand.nextInt(9) - 4;
                y = rand.nextInt(5 - Math.abs(x)) - (4 - Math.abs(x)); // Derived from the pattern of the hexagonal grid
                z = -(x+y);
            } while (usedHexes.contains(x + "," + y + "," + z));

            usedHexes.add(x + "," + y + "," + z);
            System.out.println("x: " + x + " y: " + y + " z: " + z);
        }
    }


}
