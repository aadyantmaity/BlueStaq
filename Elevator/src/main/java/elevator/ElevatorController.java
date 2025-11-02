package elevator;

import java.util.*;

/**
 * Controller for managing one or more elevators.
 * Handles request routing and elevator selection.
 */
public class ElevatorController {
    private final List<Elevator> elevators;
    private final int minFloor;
    private final int maxFloor;
    
    public ElevatorController(int numElevators, int minFloor, int maxFloor) {
        if (numElevators < 1) {
            throw new IllegalArgumentException("Must have at least one elevator");
        }
        if (minFloor < 0 || maxFloor <= minFloor) {
            throw new IllegalArgumentException("Invalid floor range");
        }
        
        this.minFloor = minFloor;
        this.maxFloor = maxFloor;
        this.elevators = new ArrayList<>();
        
        for (int i = 0; i < numElevators; i++) {
            elevators.add(new Elevator(i + 1, minFloor, maxFloor));
        }
    }
    
    /**
     * Requests an elevator pickup from a floor
     * @param floor The floor where the pickup is requested
     * @param direction The direction the passenger wants to go
     */
    public void requestPickup(int floor, Direction direction) {
        if (floor < minFloor || floor > maxFloor) {
            throw new IllegalArgumentException(
                String.format("Floor %d is out of range [%d, %d]", floor, minFloor, maxFloor));
        }
        
        Request request = new Request(floor, direction, Request.RequestType.PICKUP);
        Elevator selectedElevator = selectBestElevator(request);
        selectedElevator.addRequest(request);
    }
    
    /**
     * Selects the best elevator for a pickup request
     */
    private Elevator selectBestElevator(Request request) {
        int requestFloor = request.getFloor();
        Direction requestDirection = request.getDirection();
        
        Elevator bestElevator = null;
        int bestScore = Integer.MAX_VALUE;
        
        for (Elevator elevator : elevators) {
            int score = calculateScore(elevator, requestFloor, requestDirection);
            if (score < bestScore) {
                bestScore = score;
                bestElevator = elevator;
            }
        }
        
        return bestElevator;
    }
    
    private int calculateScore(Elevator elevator, int requestFloor, Direction requestDirection) {
        int currentFloor = elevator.getCurrentFloor();
        Direction elevatorDirection = elevator.getDirection();
        int distance = Math.abs(currentFloor - requestFloor);
        
        if (elevatorDirection == Direction.IDLE && currentFloor == requestFloor) {
            return 0;
        }
        
        if (elevatorDirection == Direction.UP && requestDirection == Direction.UP && 
            currentFloor < requestFloor) {
            return distance * 2;
        }
        
        if (elevatorDirection == Direction.DOWN && requestDirection == Direction.DOWN && 
            currentFloor > requestFloor) {
            return distance * 2;
        }
        
        if (elevatorDirection == Direction.IDLE) {
            return distance * 5;
        }
        
        return distance * 10;
    }
    
    public Elevator getElevator(int elevatorId) {
        if (elevatorId < 1 || elevatorId > elevators.size()) {
            throw new IllegalArgumentException(
                String.format("Elevator ID %d is out of range [1, %d]", elevatorId, elevators.size()));
        }
        return elevators.get(elevatorId - 1);
    }
    
    public List<Elevator> getAllElevators() {
        return new ArrayList<>(elevators);
    }
    
    public void stepAll() {
        for (Elevator elevator : elevators) {
            elevator.step();
        }
    }
    
    public String getStatus() {
        StringBuilder sb = new StringBuilder();
        for (Elevator elevator : elevators) {
            sb.append(elevator.getStatus()).append("\n");
        }
        return sb.toString();
    }
}

