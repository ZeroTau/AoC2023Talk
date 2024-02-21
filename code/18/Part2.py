from shapely.geometry import Polygon
import matplotlib.pyplot as plt


def main():
    with (open("data.txt") as datafile):
        # The starting points for the two paths
        x_part1, y_part1 = 0, 0
        points_part1 = [(x_part1, y_part1)]
        x_part2, y_part2 = 0, 0
        points_part2 = [(x_part2, y_part2)]

        # Get the first line
        line = datafile.readline()

        # An accumulator for the area of the border
        area_border_part1 = 0
        area_border_part2 = 0

        # Loop through the lines
        while line:
            # Split the line into the direction, length, and hex_code
            direction_part1, length_part1, hex_code = line.split()
            length_part1 = int(length_part1)

            # Only keep the hexadecimal digits
            hex_code = hex_code[2:-1]

            # The first five hexadecimal digits are the length
            length_part2 = int(hex_code[:5], 16)

            # The last hexadecimal digit is the direction
            direction_part2 = int(hex_code[-1], 16)

            # Convert the direction to a letter for printing
            if direction_part2 == 0:
                direction_part2 = "R"
            elif direction_part2 == 1:
                direction_part2 = "D"
            elif direction_part2 == 2:
                direction_part2 = "L"
            else:
                direction_part2 = "U"

            # Update the current positions
            if direction_part1 == "R":
                x_part1 += length_part1
            elif direction_part1 == "D":
                y_part1 -= length_part1
            elif direction_part1 == "L":
                x_part1 -= length_part1
            else:
                y_part1 += length_part1
            if direction_part2 == "R":
                x_part2 += length_part2
            elif direction_part2 == "D":
                y_part2 -= length_part2
            elif direction_part2 == "L":
                x_part2 -= length_part2
            else:
                y_part2 += length_part2

            # Add the new positions to their lists
            points_part1.append((x_part1, y_part1))
            points_part2.append((x_part2, y_part2))

            # Add the lengths to the area of the borders
            area_border_part1 += length_part1
            area_border_part2 += length_part2

            # Get the next line
            line = datafile.readline()

        # Cut the last points
        points_part1 = points_part1[:-1]
        points_part2 = points_part2[:-1]

        # Get the area of the polygons
        polygon_part1 = Polygon(points_part1)
        polygon_part2 = Polygon(points_part2)
        area_part1 = polygon_part1.area
        area_part2 = polygon_part2.area

        # Add the area of the border (half of the border is inside the polygon already
        area_part1 += area_border_part1/2
        area_part2 += area_border_part2/2

        # Add 1 to the areas. There is one block left as 4 of the corners have 3/4 of the area outside the
        # polygon and 1/4 inside the polygon. The other corners are in pairs where one has 1/4 inside and
        # one has 3/4 inside. See an example where the dig area is a square, a plus sign, or a U shape.
        area_part1 = int(area_part1 + 1)
        area_part2 = int(area_part2 + 1)

        print(f"Part 1 area: {area_part1}")
        print(f"Part 2 area: {area_part2}")

        # Plot the points
        fig, ax = plt.subplots(2, 1)
        ax[0].fill(*polygon_part1.exterior.xy)
        ax[0].title.set_text(f"Part 1 Area {area_part1}")

        ax[1].fill(*polygon_part2.exterior.xy)
        ax[1].title.set_text(f"Part 2 Area {area_part2}")

        fig.set_size_inches(10, 10)
        plt.show()


if __name__ == "__main__":
    main()
