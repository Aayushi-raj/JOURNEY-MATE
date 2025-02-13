public class trainbackend {
    
    public static void main(String[] args) {
        // This is the entry point of your application
        trainbackend backend = new trainbackend();
        
        // Example usage:
        String trainNumber = "12345"; // Replace with actual train number
        TrainStatus status = backend.getLiveTrainStatus(trainNumber);
        
        // Print the status (for demonstration)
        System.out.println("Train Number: " + status.getTrainNumber());
        System.out.println("Status: " + status.getStatus());
        System.out.println("Current Station: " + status.getCurrentStation());
        System.out.println("Destination: " + status.getDestination());
        System.out.println("Expected Arrival: " + status.getExpectedArrival());
    }

    public TrainStatus getLiveTrainStatus(String trainNumber) {
        // TODO: Implement actual API call or database query to fetch live train status
        // For now, we'll return a mock TrainStatus object
        return new TrainStatus(trainNumber, "On Time", "Delhi", "Mumbai", "14:30");
    }
}

// Add this class at the end of the file or in a separate file
class TrainStatus {
    private String trainNumber;
    private String status;
    private String currentStation;
    private String destination;
    private String expectedArrival;

    public TrainStatus(String trainNumber, String status, String currentStation, String destination, String expectedArrival) {
        this.trainNumber = trainNumber;
        this.status = status;
        this.currentStation = currentStation;
        this.destination = destination;
        this.expectedArrival = expectedArrival;
    }

    // Getters for all fields
    public String getTrainNumber() {
        return trainNumber;
    }

    public String getStatus() {
        return status;
    }

    public String getCurrentStation() {
        return currentStation;
    }

    public String getDestination() {
        return destination;
    }

    public String getExpectedArrival() {
        return expectedArrival;
    }
}
