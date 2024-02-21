import java.awt.*;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;
import java.util.*;

public class Part2
{
    public static void main(String[] args) throws FileNotFoundException
    {
        Scanner scanner = new Scanner(new BufferedReader(new FileReader("data.txt")));
        LightMaze maze = new LightMaze(scanner);

        int best = 0;

        // Make a list of all the starting lights
        List<Light> startingLights = new LinkedList<>();
        for (int index = 1; index < maze.cells.length - 1; index++)
        {
            startingLights.add(new Light(index, 1, Direction.DOWN, maze));
            startingLights.add(new Light(index, maze.cells.length - 2, Direction.UP, maze));
            startingLights.add(new Light(1, index, Direction.RIGHT, maze));
            startingLights.add(new Light(maze.cells.length - 2, index, Direction.LEFT, maze));
        }

        // test each starting light
        String message = "";
        for (Light startingLight : startingLights)
        {
            int energized = testMaze(maze, startingLight);
            if (energized > best)
            {
                best = energized;

                // Delete old message
                for (int i = 0; i < message.length(); i++)
                {
                    System.out.print("\b");
                }

                message = String.format("Energized cells: %d", best);
                System.out.print(message);
            }
        }
        System.out.println();
    }

    private static int testMaze(LightMaze maze, Light startingLight)
    {
        maze.reset(startingLight);

        while (!maze.lights.isEmpty())
        {
            maze.setLightDirections();
            maze.propagate();
        }

        return maze.visited.size();
    }

    enum Direction
    {
        UP, DOWN, LEFT, RIGHT
    }

    private static class Light
    {
        private final LightMaze maze;
        private int row;
        private int col;
        private Direction direction;
        
        public Light(int col, int row, Direction direction, LightMaze maze)
        {
            this.row = row;
            this.col = col;
            this.direction = direction;
            this.maze = maze;
        }

        @Override
        public boolean equals(Object o)
        {
            if (this == o) return true;
            if (!(o instanceof Light light)) return false;
            return row == light.row &&
                   col == light.col &&
                   direction == light.direction;
        }

        private Light setDirection()
        {
            // Based on the current cell determine what to do next
            switch (maze.cells[row][col])
            {
                case LightMaze.WALL:
                case LightMaze.EMPTY:
                    return null;
                case LightMaze.FORWARD_MIRROR:
                    switch (direction)
                    {
                        case UP:
                            direction = Direction.RIGHT;
                            return null;
                        case DOWN:
                            direction = Direction.LEFT;
                            return null;
                        case LEFT:
                            direction = Direction.DOWN;
                            return null;
                        case RIGHT:
                            direction = Direction.UP;
                            return null;
                    }
                case LightMaze.BACKWARD_MIRROR:
                    switch (direction)
                    {
                        case UP:
                            direction = Direction.LEFT;
                            return null;
                        case DOWN:
                            direction = Direction.RIGHT;
                            return null;
                        case LEFT:
                            direction = Direction.UP;
                            return null;
                        case RIGHT:
                            direction = Direction.DOWN;
                            return null;
                    }
                case LightMaze.VERTICAL_SPLITTER:
                    switch (direction)
                    {
                        case UP:
                        case DOWN:
                            return null;
                        case LEFT:
                        case RIGHT:
                            direction = Direction.DOWN;
                            return new Light(col, row, Direction.UP, maze);
                    }
                    break;
                case LightMaze.HORIZONTAL_SPLITTER:
                    switch (direction)
                    {
                        case UP:
                        case DOWN:
                            direction = Direction.RIGHT;
                            return new Light(col, row, Direction.LEFT, maze);
                        case LEFT:
                        case RIGHT:
                            return null;
                    }
            }
            return null;
        }

        private boolean propagate()
        {
            // If we are at the edge of the maze, the light is dead
            if (maze.cells[row][col] == LightMaze.WALL)
            {
                return false;
            }

            // Mark the current cell as visited and add the current state to the list of states
            maze.visited.add(new Point(col, row));

            // Move the light in the current direction
            switch (direction)
            {
                case UP:
                    row--;
                    break;
                case DOWN:
                    row++;
                    break;
                case LEFT:
                    col--;
                    break;
                case RIGHT:
                    col++;
                    break;
            }

            return true;
        }
    }

    private static class LightMaze
    {
        private static final char EMPTY = '.';
        private static final char FORWARD_MIRROR = '/';
        private static final char BACKWARD_MIRROR = '\\';
        private static final char WALL = '*';
        private static final char VERTICAL_SPLITTER = '|';
        private static final char HORIZONTAL_SPLITTER = '-';
        private final List<Light> lights = new LinkedList<>();
        private final Set<Point> visited = new HashSet<>();
        private final char[][] originalCells;
        private char[][] cells;

        public LightMaze(Scanner scanner)
        {
            String line = scanner.nextLine();
            int size = line.length() + 2;
            cells = new char[size][size];
            originalCells = new char[size][size];

            // fill the first and last rows with walls
            Arrays.fill(cells[0], WALL);
            Arrays.fill(cells[size - 1], WALL);

            // Use the first line to fill the next row
            int row = 1;
            cells[row][0] = WALL;
            for (int i = 0; i < line.length(); i++)
            {
                cells[row][i + 1] = line.charAt(i);
            }
            cells[row++][size - 1] = WALL;

            // Read the rest of the lines
            while (scanner.hasNextLine())
            {
                line = scanner.nextLine();
                cells[row][0] = WALL;
                for (int i = 0; i < line.length(); i++)
                {
                    cells[row][i + 1] = line.charAt(i);
                }
                cells[row++][size - 1] = WALL;
            }

            // Make a copy of the original cells
            for (int i = 0; i < cells.length; i++)
            {
                System.arraycopy(cells[i], 0, originalCells[i], 0, cells.length);
            }
        }

        private void reset(Light startingLight)
        {
            lights.clear();
            visited.clear();
            cells = new char[originalCells.length][originalCells.length];
            for (int i = 0; i < originalCells.length; i++)
            {
                System.arraycopy(originalCells[i], 0, cells[i], 0, originalCells.length);
            }

            lights.add(startingLight);
        }

        private void setLightDirections()
        {
            List<Light> newLights = new LinkedList<>();

            for (Light light : lights)
            {
                Light newLight = light.setDirection();
                if (newLight != null)
                {
                    newLights.add(newLight);
                }
            }

            lights.addAll(newLights);
        }

        public void propagate()
        {
            // Mark dead splitters
            List<Point> deadSplitters = new LinkedList<>();
            for (Light light : lights)
            {
                if (cells[light.row][light.col] == VERTICAL_SPLITTER ||
                    cells[light.row][light.col] == HORIZONTAL_SPLITTER)
                {
                    deadSplitters.add(new Point(light.col, light.row));
                }
            }

            List<Light> deadLights = new LinkedList<>();

            for (Light light : lights)
            {
                if (!light.propagate())
                {
                    deadLights.add(light);
                }
            }

            lights.removeAll(deadLights);
            for (Point deadSplitter : deadSplitters)
            {
                cells[deadSplitter.y][deadSplitter.x] = WALL;
            }
        }

        @Override
        public String toString()
        {
            StringBuilder sb = new StringBuilder();
            for (char[] chars : cells)
            {
                for (char c : chars)
                {
                    sb.append(c);
                }
                sb.append('\n');
            }
            return sb.toString();
        }
    }
}