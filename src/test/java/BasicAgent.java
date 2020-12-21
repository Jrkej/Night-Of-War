import java.util.*;
import java.io.*;
import java.math.*;

/**
 * Jrke's special
 **/
class sold {
	public int owner;
	public int x;
	public int y;
	public int id;
	public int lvl;
	public int dir;
	public int league = 1;
	public int s;
	public String msg = "Jrke's special";
	public String[] directions = {"UP", "LEFT", "DOWN", "RIGHT"};
	public sold(int own, int px, int py, int Id, int level, int direction) {
		s = 1;
		owner = own;
		x = px;
		y = py;
		id = Id;
		lvl = level;
		dir = direction;
	}
	public boolean is_valid(int m, ArrayList<sold> check) {
		if (m == (dir+2)%4) return false;
		if (m == 0 && y - 1 < 0) return false;
		if (m == 1 && x - 1 < 0) return false;
		if (m == 2 && y + 1 >= s) return false;
		if (m == 3 && x + 1 >= s) return false;
		for (sold n: check) {
			if (m == 0 && n.x == x && n.y == y-1) return false;
			if (m == 1 && n.x == x-1 && n.y == y) return false;
			if (m == 2 && n.x == x && n.y == y+1) return false;
			if (m == 3 && n.x == x+1 && n.y == y) return false;
		}
		return true;
	}
	public boolean is_valid_attack(int ox, int oy) {
		if (Math.abs(x - ox) + Math.abs(y - oy) > league) return false;
		return true;
	}
	public String move(int ox, int oy, ArrayList<sold> check) {
		int m = 0;
		int val = 10000;
		for (int i = 0; i < 4; i++) {
			if (this.is_valid(i, check)) {
				if (i == 0 && Math.abs(ox - x) + Math.abs(oy - y - 1) < val) {
					val = Math.abs(ox - x) + Math.abs(oy - y - 1);
					m = i;
				}
				if (i == 2 && Math.abs(ox - x) + Math.abs(oy - y + 1) < val) {
					val = Math.abs(ox - x) + Math.abs(oy - y + 1);
					m = i;
				}
				if (i == 1 && Math.abs(ox - x - 1) + Math.abs(oy - y) < val) {
					val = Math.abs(ox - x - 1) + Math.abs(oy - y);
					m = i;
				}
				if (i == 3 && Math.abs(ox - x + 1) + Math.abs(oy - y) < val) {
					val = Math.abs(ox - x + 1) + Math.abs(oy - y);
					m = i;
				}
			}
		}
		return move_dir(m);
	}
	public String move_dir(int m) {return "MOVE " + id + " " + directions[m] + " " + msg;}
	public String attack(int d) {return "ATTACK " + id + " " + d + " " + msg;}
}
class BasicAgent {

    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        int myId = in.nextInt(); // Your unique player Id
        int mapSize = in.nextInt(); // the size of map MapSize*MapSize

        // game loop
        while (true) {
            int myBucks = in.nextInt(); // Your Money
            int oppBucks = in.nextInt(); // Opponent Money
            for (int i = 0; i < mapSize; i++) {
                for (int j = 0; j < mapSize; j++) {
                    int blockOwner = in.nextInt(); // The playerId of this box owned player
                    int x = in.nextInt(); // position x
                    int y = in.nextInt(); // position y
                }
            }
            int activeSoldierCount = in.nextInt(); // Total no. of active soldier in the game
            ArrayList<sold> Soldiers = new ArrayList<sold> ();
            Soldiers.clear();
            for (int i = 0; i < activeSoldierCount; i++) {
                int ownerId = in.nextInt(); // owner of the soldier
                int x = in.nextInt();
                int y = in.nextInt();
                int soldierId = in.nextInt(); // The unique identifier of soldier
                int level = in.nextInt(); // Level of the soldier ignore for wood 2
                int direction = in.nextInt(); // The side where the soldier is facing 0 = UP, 1 = LEFT , 2 = DOWN, 3 = RIGHT
                Soldiers.add(new sold(ownerId, x, y, soldierId, level, direction));
            }
            ArrayList<sold> S = Soldiers;
            String Action = "WAIT Jrke's special";
            int min_dist = 1000;
            int ox = 0;
            int oy = 0;
            int oid = 0;
            sold my = new sold(0,0,0,0,0,0);
            for (sold a: Soldiers) {
            	if (a.owner == myId) {
            		for (sold b: S) {
            			if (b.owner == 1-myId && Math.abs(b.x - a.x) + Math.abs(b.y - a.y) < min_dist) {
            				min_dist = Math.abs(b.x - a.x) + Math.abs(b.y - a.y);
            				ox = b.x;
            				oy = b.y;
            				oid = b.id;
            				my = a;
            			}
            		}
            	}
            }
            if (my.is_valid_attack(ox, oy)) {
            	Action = my.attack(oid);
            }
            else {
            	my.s = mapSize;
            	Action = my.move(ox, oy, Soldiers);
            }
            System.err.println(ox + " " + oy);
            System.out.println(Action);
        }
    }
}