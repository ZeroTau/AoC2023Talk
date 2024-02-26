class Module():
    """
    Base class
    """
    def __init__(self, name: str):
        self.name = name
    def receive(self, source: str = None, pulse: bool = None) -> bool:
        """Default behavior: module is a sink"""
        pass

class FlipFlop(Module):
    """
    On high pulse, sends no pulse.\n
    On low pulse, flips and sends internal state.
    """
    def __init__(self, name: str):
        self.state: bool = False
        super().__init__(name)
    
    def receive(self, source: str = None, pulse: bool = None) -> bool:
        if pulse:
            return None
        else:
            self.state = not self.state
            return self.state
    
class Conjunction(Module):
    """
    Returns low pulse if it remembers high pulses for all inputs.\n
    Else, returns high pulse.
    """
    def __init__(self, name: str):
        self.memory: dict = {}
        super().__init__(name)

    def register_parent(self, source: str):
        self.memory[source] = False

    def receive(self, source: str = None, pulse: bool = None) -> bool:
        self.memory[source] = pulse
        if all(self.memory.values()):
            return False
        else:
            return True
        
class Button(Module):
    """
    Sends low pulse
    """
    def receive(self, source: str = None, pulse: bool = None) -> bool:
        return False