import moduleclasses

def get_input(input_file = "input"):
    lines = []
    with open(input_file, "r") as f:
        lines = f.readlines()

    modules: dict[str, moduleclasses.Module] = {}
    dests: dict[str, list[str]] = {}
    for line in lines:
        source, dests_str = line.split(" -> ")
        dests_list = dests_str.strip().split(", ")
        source_module = create_module(source, dests_list)
        modules[source_module.name] = source_module
        dests[source_module.name] = dests_list

    for source, dest_list in dests.items():
        for dest in dest_list:
            if dest not in modules:
                modules[dest] = moduleclasses.Module(dest)
            if type(modules[dest]) == moduleclasses.Conjunction:
                modules[dest].register_parent(source)
    return (modules, dests)


def create_module(source: str, dests: list[str]) -> moduleclasses.Module:
    if source[0] == "%":
        return moduleclasses.FlipFlop(source[1:])
    elif source[0] == "&":
        return moduleclasses.Conjunction(source[1:])
    else:
        return moduleclasses.Button("button")