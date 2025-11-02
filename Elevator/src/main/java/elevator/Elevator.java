package elevator;

import java.util.*;

/**
 * Represents an elevator in the building.
 * Handles movement, doors, and passenger requests.
 */
public class Elevator {
    private final int id;
    private final int maxFloor;
    private final int minFloor;
    private int currentFloor;
    private Direction direction;
    private boolean doorsOpen;
    private final Set<Integer> destinationFloors;
    private final List<Request> pendingRequests;
    
    public Elevator(int id, int minFloor, int maxFloor) {
        if (minFloor < 0 || maxFloor <= minFloor) {
            throw new IllegalArgumentException("Invalid floor range");
        }
        this.id = id;
        this.minFloor = minFloor;
        this.maxFloor = maxFloor;
        this.currentFloor = minFloor;
        this.direction = Direction.IDLE;
        this.doorsOpen = false;
        this.destinationFloors = new TreeSet<>();
        this.pendingRequests = new ArrayList<>();
    }
    
    /**
     * Adds a destination floor request (from inside the elevator)
     */
    public void addDestination(int floor) {
        if (floor < minFloor || floor > maxFloor) {
            throw new IllegalArgumentException(
                String.format("Floor %d is out of range [%d, %d]", floor, minFloor, maxFloor));
        }
        if (floor != currentFloor) {
            destinationFloors.add(floor);
        }
    }
    
    /**
     * Adds a pickup request (from outside the elevator)
     */
    public void addRequest(Request request) {
        if (request.getFloor() < minFloor || request.getFloor() > maxFloor) {
            throw new IllegalArgumentException(
                String.format("Request floor %d is out of range [%d, %d]", 
                    request.getFloor(), minFloor, maxFloor));
        }
        if (!pendingRequests.contains(request)) {
            pendingRequests.add(request);
            if (request.getFloor() == currentFloor && 
                (direction == Direction.IDLE || direction == request.getDirection())) {
                destinationFloors.add(request.getFloor());
            }
        }
    }
    
    /**
     * Processes one step of elevator movement
     * Returns true if elevator moved or doors opened/closed
     */
    public boolean step() {
        boolean actionTaken = false;
        
        if (shouldStopAtCurrentFloor()) {
            if (!doorsOpen) {
                openDoors();
                actionTaken = true;
            }
            destinationFloors.remove(currentFloor);
            pendingRequests.removeIf(req -> 
                req.getFloor() == currentFloor && 
                (direction == Direction.IDLE || req.getDirection() == direction));
            
            if (doorsOpen && destinationFloors.isEmpty() && !hasPendingRequestsInDirection()) {
                closeDoors();
            }
            return actionTaken;
        }
        
        if (doorsOpen && hasDestinationsOrRequests()) {
            closeDoors();
            actionTaken = true;
        }
        
        if (hasDestinationsOrRequests()) {
            Direction nextDirection = determineNextDirection();
            
            if (nextDirection != Direction.IDLE && nextDirection != direction) {
                direction = nextDirection;
                actionTaken = true;
            }
            
            if (direction == Direction.UP && currentFloor < maxFloor) {
                currentFloor++;
                actionTaken = true;
            } else if (direction == Direction.DOWN && currentFloor > minFloor) {
                currentFloor--;
                actionTaken = true;
            }
            
            if (currentFloor == maxFloor && direction == Direction.UP) {
                direction = hasDestinationsOrRequests() ? Direction.DOWN : Direction.IDLE;
            } else if (currentFloor == minFloor && direction == Direction.DOWN) {
                direction = hasDestinationsOrRequests() ? Direction.UP : Direction.IDLE;
            }
        } else {
            if (direction != Direction.IDLE) {
                direction = Direction.IDLE;
                if (doorsOpen) {
                    closeDoors();
                }
                actionTaken = true;
            }
        }
        
        checkForPickupRequests();
        
        return actionTaken;
    }
    
    private boolean shouldStopAtCurrentFloor() {
        if (destinationFloors.contains(currentFloor)) {
            return true;
        }
        
        for (Request req : pendingRequests) {
            if (req.getFloor() == currentFloor) {
                if (direction == Direction.IDLE || 
                    (direction == Direction.UP && req.getDirection() == Direction.UP) ||
                    (direction == Direction.DOWN && req.getDirection() == Direction.DOWN)) {
                    return true;
                }
            }
        }
        
        return false;
    }
    
    private boolean hasDestinationsOrRequests() {
        return !destinationFloors.isEmpty() || !pendingRequests.isEmpty();
    }
    
    private boolean hasPendingRequestsInDirection() {
        if (direction == Direction.IDLE) {
            return !pendingRequests.isEmpty();
        }
        
        for (Request req : pendingRequests) {
            if ((direction == Direction.UP && req.getFloor() > currentFloor && req.getDirection() == Direction.UP) ||
                (direction == Direction.DOWN && req.getFloor() < currentFloor && req.getDirection() == Direction.DOWN)) {
                return true;
            }
        }
        return false;
    }
    
    private Direction determineNextDirection() {
        if (destinationFloors.isEmpty() && pendingRequests.isEmpty()) {
            return Direction.IDLE;
        }
        
        if (!destinationFloors.isEmpty()) {
            if (direction == Direction.UP) {
                Integer nextUp = destinationFloors.stream()
                    .filter(f -> f > currentFloor)
                    .min(Integer::compare)
                    .orElse(null);
                if (nextUp != null) return Direction.UP;
                
                Integer nextDown = destinationFloors.stream()
                    .filter(f -> f < currentFloor)
                    .max(Integer::compare)
                    .orElse(null);
                if (nextDown != null) return Direction.DOWN;
            } else if (direction == Direction.DOWN) {
                Integer nextDown = destinationFloors.stream()
                    .filter(f -> f < currentFloor)
                    .max(Integer::compare)
                    .orElse(null);
                if (nextDown != null) return Direction.DOWN;
                
                Integer nextUp = destinationFloors.stream()
                    .filter(f -> f > currentFloor)
                    .min(Integer::compare)
                    .orElse(null);
                if (nextUp != null) return Direction.UP;
            } else {
                Integer nearestUp = destinationFloors.stream()
                    .filter(f -> f > currentFloor)
                    .min(Integer::compare)
                    .orElse(null);
                Integer nearestDown = destinationFloors.stream()
                    .filter(f -> f < currentFloor)
                    .max(Integer::compare)
                    .orElse(null);
                
                if (nearestUp != null && nearestDown != null) {
                    return (nearestUp - currentFloor) <= (currentFloor - nearestDown) ? 
                           Direction.UP : Direction.DOWN;
                } else if (nearestUp != null) {
                    return Direction.UP;
                } else if (nearestDown != null) {
                    return Direction.DOWN;
                }
            }
        }
        
        if (!pendingRequests.isEmpty()) {
            boolean hasUpRequest = pendingRequests.stream()
                .anyMatch(req -> req.getFloor() > currentFloor && 
                               (direction == Direction.IDLE || req.getDirection() == Direction.UP));
            boolean hasDownRequest = pendingRequests.stream()
                .anyMatch(req -> req.getFloor() < currentFloor && 
                               (direction == Direction.IDLE || req.getDirection() == Direction.DOWN));
            
            if (direction == Direction.UP && hasUpRequest) return Direction.UP;
            if (direction == Direction.DOWN && hasDownRequest) return Direction.DOWN;
            if (direction == Direction.IDLE) {
                if (hasUpRequest) return Direction.UP;
                if (hasDownRequest) return Direction.DOWN;
            }
        }
        
        return Direction.IDLE;
    }
    
    private void checkForPickupRequests() {
        for (Request req : new ArrayList<>(pendingRequests)) {
            if (req.getFloor() == currentFloor && 
                req.getType() == Request.RequestType.PICKUP &&
                (direction == Direction.IDLE || direction == req.getDirection())) {
                destinationFloors.add(req.getFloor());
            }
        }
    }
    
    private void openDoors() {
        doorsOpen = true;
    }
    
    private void closeDoors() {
        doorsOpen = false;
    }
    
    public int getId() {
        return id;
    }
    
    public int getCurrentFloor() {
        return currentFloor;
    }
    
    public Direction getDirection() {
        return direction;
    }
    
    public boolean areDoorsOpen() {
        return doorsOpen;
    }
    
    public Set<Integer> getDestinationFloors() {
        return new HashSet<>(destinationFloors);
    }
    
    public List<Request> getPendingRequests() {
        return new ArrayList<>(pendingRequests);
    }
    
    public int getMaxFloor() {
        return maxFloor;
    }
    
    public int getMinFloor() {
        return minFloor;
    }
    
    /**
     * Gets a status string describing the current state of the elevator
     */
    public String getStatus() {
        return String.format("Elevator %d: Floor %d, Direction: %s, Doors: %s, " +
                           "Destinations: %s, Pending Requests: %d",
                id, currentFloor, direction, 
                doorsOpen ? "OPEN" : "CLOSED",
                destinationFloors, pendingRequests.size());
    }
    
    @Override
    public String toString() {
        return getStatus();
    }
}

