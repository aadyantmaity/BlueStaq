package elevator;

import java.util.Scanner;

/**
 * Main application demonstrating the elevator simulation.
 * Provides an interactive command-line interface.
 */
public class ElevatorSimulation {
    private final ElevatorController controller;
    private final Scanner scanner;
    
    public ElevatorSimulation(int numElevators, int minFloor, int maxFloor) {
        this.controller = new ElevatorController(numElevators, minFloor, maxFloor);
        this.scanner = new Scanner(System.in);
    }
    
    public void run() {
        System.out.println("=== Elevator Simulation ===");
        System.out.println("Commands:");
        System.out.println("  pickup <floor> <UP|DOWN>  - Request elevator pickup");
        System.out.println("  destination <elevator_id> <floor>  - Set destination inside elevator");
        System.out.println("  step  - Advance simulation one step");
        System.out.println("  auto <steps>  - Run automatic simulation for N steps");
        System.out.println("  status  - Show elevator statuses");
        System.out.println("  demo  - Run demo scenario");
        System.out.println("  quit  - Exit");
        System.out.println();
        
        boolean running = true;
        while (running) {
            System.out.print("> ");
            String input = scanner.nextLine().trim();
            
            if (input.isEmpty()) {
                continue;
            }
            
            String[] parts = input.split("\\s+");
            String command = parts[0].toLowerCase();
            
            try {
                switch (command) {
                    case "pickup":
                        if (parts.length != 3) {
                            System.out.println("Usage: pickup <floor> <UP|DOWN>");
                            break;
                        }
                        int pickupFloor = Integer.parseInt(parts[1]);
                        Direction direction = Direction.valueOf(parts[2].toUpperCase());
                        controller.requestPickup(pickupFloor, direction);
                        System.out.println("Pickup requested: Floor " + pickupFloor + ", Direction: " + direction);
                        break;
                        
                    case "destination":
                        if (parts.length != 3) {
                            System.out.println("Usage: destination <elevator_id> <floor>");
                            break;
                        }
                        int elevatorId = Integer.parseInt(parts[1]);
                        int destFloor = Integer.parseInt(parts[2]);
                        controller.getElevator(elevatorId).addDestination(destFloor);
                        System.out.println("Destination set: Elevator " + elevatorId + " -> Floor " + destFloor);
                        break;
                        
                    case "step":
                        controller.stepAll();
                        printStatus();
                        break;
                        
                    case "auto":
                        if (parts.length != 2) {
                            System.out.println("Usage: auto <number_of_steps>");
                            break;
                        }
                        int steps = Integer.parseInt(parts[1]);
                        runAutoSimulation(steps);
                        break;
                        
                    case "status":
                        printStatus();
                        break;
                        
                    case "demo":
                        runDemo();
                        break;
                        
                    case "quit":
                    case "exit":
                        running = false;
                        break;
                        
                    default:
                        System.out.println("Unknown command: " + command);
                        break;
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
        
        scanner.close();
        System.out.println("Simulation ended.");
    }
    
    private void printStatus() {
        System.out.println(controller.getStatus());
    }
    
    private void runAutoSimulation(int steps) {
        System.out.println("Running " + steps + " steps...");
        for (int i = 0; i < steps; i++) {
            controller.stepAll();
            printStatus();
            System.out.println("--- Step " + (i + 1) + " completed ---\n");
            
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
    
    private void runDemo() {
        System.out.println("\n=== Running Demo Scenario ===\n");
        
        System.out.println("Initial state:");
        printStatus();
        System.out.println();
        
        System.out.println("1. Requesting pickup from floor 2 going UP");
        controller.requestPickup(2, Direction.UP);
        printStatus();
        System.out.println();
        
        System.out.println("2. Running 10 steps...");
        for (int i = 0; i < 10; i++) {
            controller.stepAll();
            System.out.println("Step " + (i + 1) + ":");
            printStatus();
            System.out.println();
        }
        
        System.out.println("3. Setting destination: Elevator 1 -> Floor 5");
        controller.getElevator(1).addDestination(5);
        printStatus();
        System.out.println();
        
        System.out.println("4. Running 15 more steps...");
        for (int i = 0; i < 15; i++) {
            controller.stepAll();
            System.out.println("Step " + (i + 1) + ":");
            printStatus();
            System.out.println();
        }
        
        System.out.println("5. Requesting pickup from floor 3 going DOWN");
        controller.requestPickup(3, Direction.DOWN);
        printStatus();
        System.out.println();
        
        System.out.println("6. Running 10 more steps...");
        for (int i = 0; i < 10; i++) {
            controller.stepAll();
            System.out.println("Step " + (i + 1) + ":");
            printStatus();
            System.out.println();
        }
        
        System.out.println("=== Demo Complete ===\n");
    }
    
    public static void main(String[] args) {
        int numElevators = 1;
        int minFloor = 0;
        int maxFloor = 10;
        
        if (args.length >= 1) {
            numElevators = Integer.parseInt(args[0]);
        }
        if (args.length >= 2) {
            minFloor = Integer.parseInt(args[1]);
        }
        if (args.length >= 3) {
            maxFloor = Integer.parseInt(args[2]);
        }
        
        ElevatorSimulation simulation = new ElevatorSimulation(numElevators, minFloor, maxFloor);
        simulation.run();
    }
}

