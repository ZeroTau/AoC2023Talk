import argparse
parser = argparse.ArgumentParser()
parser.add_argument("-f", "--file", default="testinput")
filename = parser.parse_args().file

class Point():
    def __init__(self, x: int, y: int):
        self.x = x
        self.y = y

    def move(self, direction: str, distance: int):
        if direction == 'R':
            return Point(self.x + distance, self.y)
        elif direction == 'L':
            return Point(self.x - distance, self.y)
        elif direction == 'U':
            return Point(self.x, self.y + distance)
        else:
            return Point(self.x, self.y - distance)
        
class Polygon():
    def __init__(self,
                 position: Point = Point(0,0),
                 area: int = 0,
                 perimeter: int = 0):
        self.position = position
        self.interior_area = area
        self.perimeter = perimeter

    def add_point(self, direction: str, distance: int):
        # Shoelace formula / Gauss's area formula
        # A = Summation( (y_i + y_(i+1)) * (x_i - x_(i+1)) * 1/2 )
        new_position = self.position.move(direction, distance)
        self.interior_area += ((self.position.y + new_position.y) * (self.position.x - new_position.x)) / 2
        self.perimeter += distance
        self.position = new_position

    def get_area(self) -> int:
        # Pick's Theorem: A = i + b / 2 + 1
        # Area = interior_area + boundary_points / 2 + 1
        return int(abs(self.interior_area) + (self.perimeter // 2) + 1)


def Part1() -> int:
    with open(filename, "r") as puzzle_input:
        polygon = Polygon()
        for line in puzzle_input:
            direction, distance, color = line.split() # R 6 (#70c710)
            distance = int(distance)
            polygon.add_point(direction, distance)
        return polygon.get_area()
print(f"{Part1():,}")

directions = {"0": "R", "1": "D", "2": "L", "3": "U"}
def Part2() -> int:
    with open(filename, "r") as puzzle_input:
        polygon = Polygon()
        for line in puzzle_input:
            hex_string = line.split()[-1].removeprefix("(#").removesuffix(")") # 70c710
            direction = directions[hex_string[-1]] # 0 -> R
            distance = int(hex_string[:-1], base=16) # "70c71" -> 471,937
            polygon.add_point(direction, distance)
        return polygon.get_area()         
print(f"{Part2():,}")