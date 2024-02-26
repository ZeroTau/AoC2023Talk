from moduleclasses import *
from inputreader import get_input
from collections import deque
import argparse

parser = argparse.ArgumentParser()
parser.add_argument("-v", "--verbose", action="store_true")
verbose = parser.parse_args().verbose

def simulate(filename: str):
    modules, dests = get_input(filename)

    button = modules["button"]
    pulse_queue = deque()

    low_pulses = 0
    high_pulses = 0

    for press in range(1000):
        # Manual button press counts as one low pulse
        pulse = button.receive()
        low_pulses += 1

        # Enqueue all of the modules that button sends to
        for dest in dests["button"]:
            pulse_queue.append(("button", dest, pulse))

        while len(pulse_queue) != 0:
            # Next pulse in the queue
            source_name, dest_name, pulse = pulse_queue.popleft()
            if verbose: print(f"{source_name} -{"high" if pulse else "low"}-> {dest_name}")

            if pulse: high_pulses += 1
            else: low_pulses += 1

            dest_module = modules[dest_name] # Module object being sent to
            new_pulse = dest_module.receive(source_name, pulse) # Response from that module

            if new_pulse != None: # High pulse into FlipFlop returns None
                for new_dest in dests[dest_module.name]:
                    pulse_queue.append((dest_name, new_dest, new_pulse))

    print(low_pulses, high_pulses)

simulate("testinput")
simulate("testinput2")