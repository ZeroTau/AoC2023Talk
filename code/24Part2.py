from sympy import Symbol, solve_poly_system

class Vec3D():
    def __init__(self, x: int, y: int, z: int):
        self.x = x
        self.y = y
        self.z = z
    
    def __add__(self, o):
        if isinstance(o, Vec3D):
            return Vec3D(self.x + o.x, self.y + o.y, self.z + o.z)
        
    def __mul__(self, o):
        if isinstance(o, int):
            return Vec3D(self.x * o, self.y * o, self.z * o)
        
    def __str__(self):
        return f"<{self.x}, {self.y}, {self.z}>"

# Position and velocity of given hailstones
position_vectors: list[Vec3D] = []
velocity_vectors: list[Vec3D] = []

# We are looking for a hailstone with position P0 = <PX0, PY0, PZ0> and velocity V0 = <VX0, VY0, VZ0>
# that intercepts each hailstone with position PI = <PXI, PYI, PZI> and velocity VI = <VXI, VYI, VZI>
# at a given time TI.

# P0 + TI * V0 == PI + TI * VI can be further split into:
# PX0 + TI * VX0 == PXI + TI * VXI, or 0 = PXI + TI * VXI - PX0 - TI * VX0
# PY0 + TI * VY0 == PYI + TI * VYI, or 0 = PYI + TI * VYI - PY0 - TI * VY0
# PZ0 + TI * VZ0 == PZI + TI * VZI, or 0 = PZI + TI * VZI - PZ0 - TI * VZ0

# So for these three equations, we have 7 unknowns:
# PX0, PY0, PZ0, VX0, VY0, VZ0, and TI
# (PI and VI are given to us as input)

# By adding additional equations for our input's PJ and VJ,
# we only introduce one new unknown - TJ. P0 and V0 remain the same,
# and PJ and VJ are known. This bring us to 6 equations and 8 unknowns.

# Adding further additional equations for our input's PK and VK,
# we introduce one new unknown (TK) and three new equations, for
# a total of 9 equations and 9 unknowns, which we can plug into a linear solver.

# Any 3 hailstones will suffice, but for simplicity I used the first three
# First hailstone
position_vectors.append(Vec3D(327367788702047, 294752664325632, 162080199489440))
velocity_vectors.append(Vec3D(20, 51, 36))
# Second hailstone
position_vectors.append(Vec3D(349323332347395, 429135322811787, 397812423558610))
velocity_vectors.append(Vec3D(-96, -480, -782))
# Third hailstone
position_vectors.append(Vec3D(342928768632768, 275572250031810, 310926883862869))
velocity_vectors.append(Vec3D(-69, 104, -510))

# Add intial unknown variables
PX0 = Symbol('PX0')
PY0 = Symbol('PY0')
PZ0 = Symbol('PZ0')
VX0 = Symbol('VX0')
VY0 = Symbol('VY0')
VZ0 = Symbol('VZ0')
symbols = [PX0, PY0, PZ0, VX0, VY0, VZ0]

equations = []
for i, (p, v) in enumerate(zip(position_vectors, velocity_vectors)):
    # Create impact time unknown variable
    t = Symbol(f'T{i}')
    symbols.append(t)

    # Create equations for the impact of P0, V0 at time TI with PI and VI
    equations.append((p.x + t * (v.x - VX0) - PX0)) # Add X equation
    equations.append((p.y + t * (v.y - VY0) - PY0)) # Add Y equation
    equations.append((p.z + t * (v.z - VZ0) - PZ0)) # Add Z equation

# Solve
result = solve_poly_system(equations, *symbols)

for r, s in zip(result[0], symbols):
    print(f"{s}: {r}")
