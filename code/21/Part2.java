import java.io.*;
import java.util.*;

public class Part2
{
    private static final String MAZE_FILENAME = "data.txt";
    private static final String OUTPUT_FILENAME = "output2.txt";
    private static final int MAX_DISTANCE = 10000;

    public static void main(String[] args) throws FileNotFoundException
    {
        Maze maze = new Maze(MAZE_FILENAME);
        Map<Integer, Integer> distancesMap = new HashMap<>();
        for (int d = 0; d <= MAX_DISTANCE; d++)
        {
            distancesMap.put(d, 0);
        }

        findDistances(maze, distancesMap);

        System.out.print("\nWriting to file...");

        // Write the output to a file
        try (PrintWriter writer = new PrintWriter(OUTPUT_FILENAME))
        {
            int oddTotal = 0;
            int evenTotal = 0;

            for (int goal = 0; goal <= MAX_DISTANCE; goal++)
            {
                int value = distancesMap.get(goal);

                if (goal % 2 == 0)
                {
                    evenTotal += value;
                    writer.printf("%d, %d\n", goal, evenTotal);
                }
                else
                {
                    oddTotal += value;
                    writer.printf("%d, %d\n", goal, oddTotal);
                }
            }
        }
        System.out.println(" Done");
    }

    private static void findDistances(Maze maze, Map<Integer, Integer> distanceMap)
    {
        Queue<State> queue = new ArrayDeque<>();
        queue.add(new State(maze.startX, maze.startY,0,0, 0));
        Set<State> visited = new HashSet<>();
        String message = "";
        int distance = -1;
        long startTime = System.currentTimeMillis();
        double lastTime = 0.0;
        while (!queue.isEmpty())
        {
            State state = queue.remove();

            double elapsedTime = (System.currentTimeMillis() - startTime) / 1000.0;

            if (state.distance > distance && elapsedTime > lastTime + 1.0)
            {
                lastTime = elapsedTime;

                distance = state.distance;

                // Calculate the time and nodes per second
                double nodesPerSecond = visited.size() / elapsedTime;

                // Remove the last message
                System.out.print("\b".repeat(message.length()));

                // Create the new message
                message = String.format("Distance: %d, Nodes: %d, Time: %.2f, Nodes/Sec: %.2f",
                                        distance, visited.size(), elapsedTime, nodesPerSecond);

                System.out.print(message);
            }

            if (visited.contains(state) || state.distance > MAX_DISTANCE)
            {
                continue;
            }
            visited.add(state);

            int value = distanceMap.get(state.distance);
            distanceMap.put(state.distance, value + 1);

            queue.addAll(maze.getNeighbors(state));
        }
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

    private record State(int x, int y, int mapX, int mapY, int distance)
    {
        @Override
        public String toString()
        {
            return "x=" + x + ", y=" + y + ", mapX=" + mapX + ", mapY=" + mapY + ", distance=" + distance;
        }

        @Override
        public int hashCode()
        {
            return Objects.hash(x, y, mapX, mapY);
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
            return x == other.x && y == other.y && mapX == other.mapX && mapY == other.mapY;
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
                int mapX = state.mapX;
                int mapY = state.mapY;
                switch (direction)
                {
                    case NORTH -> y--;
                    case SOUTH -> y++;
                    case EAST -> x++;
                    case WEST -> x--;
                }
                if (y < 0)
                {
                    mapY--;
                    y += height;
                }
                if (y >= height)
                {
                    mapY++;
                    y -= height;
                }
                if (x < 0)
                {
                    mapX--;
                    x += width;
                }
                if (x >= width)
                {
                    mapX++;
                    x -= width;
                }
                if (isWalkable(x, y))
                {
                    neighbors.add(new State(x, y, mapX, mapY, state.distance + 1));
                }
            }
            return neighbors;
        }
    }
}