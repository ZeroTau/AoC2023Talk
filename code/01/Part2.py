from time import perf_counter


def reverse_string(string):
    return string[::-1]


def reverse_keys(my_map):
    return {reverse_string(name): value for name, value in my_map.items()}


def find_digit(line, my_map):
    for index in range(len(line)):
        # See if the current character is a digit
        if line[index].isdigit():
            return int(line[index])

        # Otherwise try the map
        for name, value in my_map.items():
            # If the digit name starts at the current index return its value
            if line.startswith(name, index):
                return value

    # If we reach the end of the line without finding a digit something is wrong
    raise ValueError(f"No digit found in {line}")


def main():
    digit_map = {"zero": 0, "one": 1, "two": 2, "three": 3, "four": 4,
                 "five": 5, "six": 6, "seven": 7, "eight": 8, "nine": 9}
    reverse_map = reverse_keys(digit_map)

    # Get the starting time
    start = perf_counter()

    with open("data.txt") as data_file:
        total = 0
        line_number = 1
        for line in data_file:
            # Remove whitespace and make lowercase (I don't think this is necessary for the given data)
            line = line.strip().lower()

            # Find the first digit in the line
            first_digit = find_digit(line, digit_map)

            # Find the last digit in the line
            last_digit = find_digit(reverse_string(line), reverse_map)

            # Combine the digits into a two-digit number
            number = first_digit * 10 + last_digit

            # Add the number to the total
            total += number
            line_number += 1

    # Print the result
    print(f"Total = {total}, found in {perf_counter() - start:.4f} seconds")


if __name__ == "__main__":
    main()
