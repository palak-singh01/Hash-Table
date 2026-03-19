public class parking_lot {

    // Spot Status Enum
    enum Status {
        EMPTY, OCCUPIED, DELETED
    }

    // Parking Spot Class
    static class ParkingSpot {
        String licensePlate;
        long entryTime;
        Status status;

        ParkingSpot() {
            this.status = Status.EMPTY;
        }
    }

    private ParkingSpot[] table;
    private int capacity;
    private int size;
    private int totalProbes;
    private int totalOperations;

    public parking_lot(int capacity) {
        this.capacity = capacity;
        this.table = new ParkingSpot[capacity];
        for (int i = 0; i < capacity; i++) {
            table[i] = new ParkingSpot();
        }
        this.size = 0;
    }

    // Hash Function
    private int hash(String key) {
        return Math.abs(key.hashCode()) % capacity;
    }

    // Park Vehicle
    public String parkVehicle(String plate) {
        int index = hash(plate);
        int probes = 0;

        for (int i = 0; i < capacity; i++) {
            int newIndex = (index + i) % capacity;
            probes++;

            if (table[newIndex].status == Status.EMPTY ||
                table[newIndex].status == Status.DELETED) {

                table[newIndex].licensePlate = plate;
                table[newIndex].entryTime = System.currentTimeMillis();
                table[newIndex].status = Status.OCCUPIED;

                size++;
                totalProbes += probes;
                totalOperations++;

                return "Assigned spot #" + newIndex + " (" + (probes - 1) + " probes)";
            }
        }

        return "Parking Full!";
    }

    // Exit Vehicle
    public String exitVehicle(String plate) {
        int index = hash(plate);

        for (int i = 0; i < capacity; i++) {
            int newIndex = (index + i) % capacity;

            if (table[newIndex].status == Status.EMPTY) {
                return "Vehicle not found!";
            }

            if (table[newIndex].status == Status.OCCUPIED &&
                table[newIndex].licensePlate.equals(plate)) {

                long exitTime = System.currentTimeMillis();
                long durationMillis = exitTime - table[newIndex].entryTime;

                double hours = durationMillis / (1000.0 * 60 * 60);
                double fee = hours * 5; // $5 per hour

                table[newIndex].status = Status.DELETED;
                size--;

                return "Spot #" + newIndex + " freed, Duration: " +
                        String.format("%.2f", hours) +
                        " hrs, Fee: $" + String.format("%.2f", fee);
            }
        }

        return "Vehicle not found!";
    }

    // Find nearest available spot (from entrance = index 0)
    public int findNearestSpot() {
        for (int i = 0; i < capacity; i++) {
            if (table[i].status == Status.EMPTY ||
                table[i].status == Status.DELETED) {
                return i;
            }
        }
        return -1;
    }

    // Get Statistics
    public String getStatistics() {
        double occupancy = (size * 100.0) / capacity;
        double avgProbes = totalOperations == 0 ? 0 :
                (double) totalProbes / totalOperations;

        return "Occupancy: " + String.format("%.2f", occupancy) + "%" +
                ", Avg Probes: " + String.format("%.2f", avgProbes);
    }

    // MAIN METHOD
    public static void main(String[] args) {
        parking_lot lot = new parking_lot(10);

        System.out.println(lot.parkVehicle("ABC-1234"));
        System.out.println(lot.parkVehicle("ABC-1235"));
        System.out.println(lot.parkVehicle("XYZ-9999"));

        System.out.println(lot.exitVehicle("ABC-1234"));

        System.out.println("Nearest Spot: #" + lot.findNearestSpot());

        System.out.println(lot.getStatistics());
    }
}