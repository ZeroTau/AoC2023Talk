import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;
import java.util.Queue;
import java.util.*;

public class Part1
{
    private static final String MAZE_FILENAME = "data.txt";
    private static final int GOAL_DISTANCE = 64;

    public static void main(String[] args) throws FileNotFoundException
    {
        Maze maze = new Maze(MAZE_FILENAME);

        Map<Integer, Integer> distancesMap = findDistances(maze);

        int total = 0;
        for (int d = GOAL_DISTANCE; d >= 0; d -= 2)
        {
            total += distancesMap.get(d);
        }

        System.out.println("Goals found: " + total);
    }

    private static Map<Integer, Integer> findDistances(Maze maze)
    {
        Map<Integer, Integer> distancesMap = new HashMap<>();
        for(int d = 0; d <= GOAL_DISTANCE; d++)
        {
            distancesMap.put(d, 0);
        }

        Queue<State> queue = new ArrayDeque<>();
        queue.add(new State(maze.startX, maze.startY, 0));
        Set<State> visited = new HashSet<>();
        while (!queue.isEmpty())
        {
            State state = queue.remove();
            if (visited.contains(state) || state.distance > GOAL_DISTANCE)
            {
                continue;
            }
            visited.add(state);

            int value = distancesMap.get(state.distance);
            distancesMap.put(state.distance, value + 1);

            queue.addAll(maze.getNeighbors(state));
        }

        return distancesMap;
    }

    enum Direction
    {
        NORTH,
        SOUTH,
        EAST,
        WEST
    }

    enum Tile
    {
        GARDEN,
        ROCKS,
        START;

        public static Tile fromChar(char c)
        {
            return switch (c)
            {
                case '.' -> GARDEN;
                case '#' -> ROCKS;
                case 'S' -> START;
                default -> throw new IllegalArgumentException("Invalid tile character: " + c);
            };
        }

        private static char toChar(Tile tile)
        {
            return switch (tile)
            {
                case GARDEN -> '.';
                case ROCKS -> '#';
                case START -> 'S';
            };
        }

        private static boolean isWalkable(Tile tile)
        {
            return switch (tile)
            {
                case GARDEN, START -> true;
                case ROCKS -> false;
            };
        }
    }

    private record State(int x, int y, int distance)
    {
        @Override
        public String toString()
        {
            return "x=" + x + ", y=" + y + ", distance=" + distance;
        }

        @Override
        public int hashCode()
        {
            return Objects.hash(x, y);
        }

        @Override
        public boolean equals(Object obj)
        {
            if (obj == this)
            {
                return true;
            }
            if (!(obj instanceof State other))
            {
                return false;
            }
            return x == other.x && y == other.y;
        }
    }

    private static class Maze
    {
        private final Tile[][] tiles;
        private final int width;
        private final int height;
        private final int startX;
        private final int startY;

        public Maze(String filename) throws FileNotFoundException
        {
            Scanner scanner = new Scanner(new BufferedReader(new FileReader(filename)));

            width = scanner.nextLine().length();
            int height = 1;
            while (scanner.hasNextLine())
            {
                scanner.nextLine();
                height++;
            }
            this.height = height;

            tiles = new Tile[width][height];

            scanner = new Scanner(new BufferedReader(new FileReader(filename)));
            int startX = -1;
            int startY = -1;
            for (int y = 0; y < height; y++)
            {
                String line = scanner.nextLine();
                for (int x = 0; x < width; x++)
                {
                    tiles[x][y] = Tile.fromChar(line.charAt(x));
                    if (tiles[x][y] == Tile.START)
                    {
                        startX = x;
                        startY = y;
                    }
                }
            }
            this.startX = startX;
            this.startY = startY;
        }

        @Override
        public String toString()
        {
            StringBuilder sb = new StringBuilder();
            for (int y = 0; y < height; y++)
            {
                for (int x = 0; x < width; x++)
                {
                    sb.append(Tile.toChar(tiles[x][y]));
                }
                sb.append('\n');
            }
            return sb.toString();
        }

        public boolean isWalkable(int x, int y)
        {
            return x >= 0 && x < width && y >= 0 && y < height && Tile.isWalkable(tiles[x][y]);
        }

        public List<State> getNeighbors(State state)
        {
            List<State> neighbors = new ArrayList<>(4);
            for (Direction direction : Direction.values())
            {
                int x = state.x;
                int y = state.y;
                switch (direction)
                {
                    case NORTH -> y--;
                    case SOUTH -> y++;
                    case EAST -> x++;
                    case WEST -> x--;
                }
                if (isWalkable(x, y))
                {
                    neighbors.add(new State(x, y, state.distance + 1));
                }
            }
            return neighbors;
        }
    }
}