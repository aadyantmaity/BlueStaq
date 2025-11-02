# Elevator Simulation

A Java-based elevator simulation system developed as a back-end code challenge.

## Overview

This project simulates one or more elevators in a building, handling passenger pickup requests and destination selections. The system includes an elevator controller that manages multiple elevators and routes requests efficiently.

## Architecture

The system consists of the following main components:

### Core Classes

1. **`Direction`** (Enum)
   - Represents elevator movement direction: `UP`, `DOWN`, or `IDLE`

2. **`Request`**
   - Represents a request to use the elevator
   - Types: `PICKUP` (from floor button) or `DESTINATION` (from inside elevator)

3. **`Elevator`**
   - Manages individual elevator state (current floor, direction, doors, destinations)
   - Handles movement logic and request processing
   - Implements a simple scheduling algorithm to serve requests efficiently

4. **`ElevatorController`**
   - Manages multiple elevators
   - Routes pickup requests to the most appropriate elevator
   - Uses a scoring algorithm to select the best elevator for each request

5. **`ElevatorSimulation`**
   - Main application with interactive command-line interface
   - Supports manual control and automated demo scenarios

## Features Implemented

- ✅ Single and multiple elevator support
- ✅ Floor-to-floor movement
- ✅ Pickup requests from floors (with direction)
- ✅ Destination selection from inside elevator
- ✅ Door open/close simulation
- ✅ Direction-based request handling (UP/DOWN buttons)
- ✅ Basic elevator selection algorithm (closest idle, same direction preference)
- ✅ Simple scheduling algorithm (serve destinations in current direction first)
- ✅ Interactive command-line interface
- ✅ Automatic simulation mode
- ✅ Demo scenario

## How to Build and Run

### Prerequisites
- Java 11 or higher
- Maven 3.6+ (optional, for building with Maven)

### Building

Using Maven:
```bash
mvn clean compile
```

Or compile directly with javac:
```bash
cd src/main/java
javac elevator/*.java
```

### Running

Using Maven:
```bash
mvn exec:java
```

Or run directly:
```bash
cd src/main/java
java elevator.ElevatorSimulation [num_elevators] [min_floor] [max_floor]
```

Default: 1 elevator, floors 0-10

Example:
```bash
java elevator.ElevatorSimulation 2 0 15
```

This creates 2 elevators operating between floors 0 and 15.

## Usage Examples

### Interactive Commands

- `pickup <floor> <UP|DOWN>` - Request elevator pickup from a floor
- `destination <elevator_id> <floor>` - Set destination inside elevator
- `step` - Advance simulation one step
- `auto <steps>` - Run automatic simulation for N steps
- `status` - Show all elevator statuses
- `demo` - Run pre-configured demo scenario
- `quit` - Exit the program

### Example Session

```
> pickup 2 UP
Pickup requested: Floor 2, Direction: UP

> step
Elevator 1: Floor 1, Direction: UP, Doors: CLOSED, Destinations: [2], Pending Requests: 1

> step
Elevator 1: Floor 2, Direction: UP, Doors: OPEN, Destinations: [], Pending Requests: 0

> destination 1 5
Destination set: Elevator 1 -> Floor 5

> auto 5
...
```

## Assumptions

1. **Floor Range**: Floors are numbered from `minFloor` (typically 0) to `maxFloor`. The system supports any valid range but assumes non-negative floor numbers.

2. **Ground Floor**: The elevator starts at `minFloor` (typically floor 0, the ground floor).

3. **Request Processing**: 
   - Pickup requests specify the floor and desired direction (UP/DOWN)
   - The system assumes passengers know which direction they want to go
   - Multiple requests can be queued

4. **Elevator Selection**: 
   - Uses a simple scoring algorithm prioritizing:
     - Idle elevators on the same floor
     - Elevators moving in the same direction toward the request
     - Closest idle elevators
   - Does not implement advanced load balancing or predictive algorithms

5. **Scheduling Algorithm**:
   - Uses a simple "scan" algorithm: serve all requests in current direction before reversing
   - Prioritizes destinations already set inside the elevator
   - Does not re-optimize queue order based on new requests

6. **Movement Speed**: 
   - One floor per step (no time-based movement)
   - Doors open/close instantly within a step

7. **Capacity**: 
   - No passenger capacity limits
   - No weight restrictions

8. **Threading**: 
   - Single-threaded simulation
   - No concurrent request handling

## Features Not Implemented

The following features were not implemented in this version:

1. **Advanced Scheduling Algorithms**
   - No SCAN, C-SCAN, or LOOK algorithms
   - No dynamic request reordering for optimization
   - No predictive algorithms based on traffic patterns

2. **Emergency Features**
   - No emergency stop button
   - No fire alarm mode
   - No earthquake mode
   - No maintenance mode

3. **Physical Constraints**
   - No weight/capacity sensors
   - No maximum passenger count
   - No speed control (instant floor changes)
   - No door sensors (obstruction detection)
   - No door hold button

4. **Priority Features**
   - No priority floors (e.g., VIP floors)
   - No express mode (skip certain floors)
   - No time-of-day optimizations

5. **Multi-Elevator Coordination**
   - Basic coordination only (simple selection algorithm)
   - No advanced load balancing
   - No zone-based assignment
   - No predictive coordination

6. **Real-Time Features**
   - No actual time delays
   - No realistic movement speed (1 floor per step)
   - No door open/close timing

7. **Monitoring & Analytics**
   - No wait time tracking
   - No usage statistics
   - No performance metrics

8. **User Interface**
   - CLI only (no GUI)
   - No REST API
   - No web interface

9. **Advanced Request Handling**
   - No request cancellation
   - No request priority levels
   - No batch request optimization

10. **Safety Features**
    - No door safety sensors
    - No inter-floor communication
    - No backup power system simulation

## Design Decisions

1. **State-Based Simulation**: The elevator operates in discrete steps rather than continuous time, making it easier to test and reason about.

2. **Separation of Concerns**: 
   - `Elevator` handles individual elevator logic
   - `ElevatorController` handles request routing
   - `ElevatorSimulation` handles user interaction

3. **Immutability Where Possible**: Request objects are immutable; elevator state changes only through controlled methods.

4. **Simple First**: Implemented a working, understandable solution rather than over-engineering with advanced algorithms that might not be necessary.

## Testing Considerations

The current implementation is designed for demonstration purposes. For production use, consider adding:

- Unit tests for each class
- Integration tests for the controller
- Edge case testing (boundary conditions, concurrent requests)
- Performance testing with many elevators/floors

## Future Enhancements

If this were to be extended, potential improvements include:

1. Implement SCAN or LOOK scheduling algorithms
2. Add capacity and weight constraints
3. Implement emergency modes
4. Add time-based simulation with realistic speeds
5. Create a REST API for remote control
6. Add web-based visualization
7. Implement advanced multi-elevator coordination
8. Add comprehensive test suite
9. Add logging and monitoring
10. Support for building-specific configurations

## License

This code is provided as part of a technical assessment.

## Author

Developed as part of BlueStaq technical assessment.

