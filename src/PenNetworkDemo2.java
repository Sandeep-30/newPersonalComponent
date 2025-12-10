import java.util.Optional;

public final class PenNetworkDemo2 {

    public static void main(String[] args) {
        PenNetwork net = new PenNetwork1L();

        PenSKU red = new PenSKU("Pilot", "G2", "0.7mm Ball", "Red");

        net.add("Shop0", red.toString(), 2);
        net.add("Shop1", red.toString(), 12);

        Optional<TransferPlan> plan = net.computeRestockPlan("Shop0", red, 8);

        if (plan.isPresent()) {
            System.out.println("Suggested restock: " + plan.get());
        } else {
            System.out.println("No restock needed.");
        }
    }
}
