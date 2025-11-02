package elevator;

/**
 * Represents a request to use the elevator.
 * Can be either a pickup request (from a floor) or a destination request (inside the elevator).
 */
public class Request {
    private final int floor;
    private final Direction direction;
    private final RequestType type;
    
    /**
     * Type of request - pickup from floor or destination inside elevator
     */
    public enum RequestType {
        PICKUP,
        DESTINATION
    }
    
    public Request(int floor, Direction direction, RequestType type) {
        if (floor < 0) {
            throw new IllegalArgumentException("Floor cannot be negative");
        }
        this.floor = floor;
        this.direction = direction;
        this.type = type;
    }
    
    public int getFloor() {
        return floor;
    }
    
    public Direction getDirection() {
        return direction;
    }
    
    public RequestType getType() {
        return type;
    }
    
    @Override
    public String toString() {
        return String.format("Request[floor=%d, direction=%s, type=%s]", 
                floor, direction, type);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Request request = (Request) obj;
        return floor == request.floor && 
               direction == request.direction && 
               type == request.type;
    }
    
    @Override
    public int hashCode() {
        return floor * 31 + direction.hashCode() * 17 + type.hashCode();
    }
}
