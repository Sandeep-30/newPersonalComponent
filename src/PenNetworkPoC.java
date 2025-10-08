import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;

/**
 * Proof-of-Concept: Pen Distribution Model (single-file MVP)
 *
 * Goal: - Demonstrate a minimal working implementation that a client can run. -
 * Show core behaviors: define shops, add inventory, transfer inventory, and
 * query status.
 *
 * Notes: - This is intentionally NOT the full OSU discipline hierarchy. - The
 * later course steps can refactor this into interfaces/abstract classes.
 */
public final class PenNetworkPoC {

    // --- Simple data models ---------------------------------------------------

    /** Simple value object for a Pen SKU (brand + name + tip + color). */
    public static final class PenSKU {
        public final String brand;
        public final String name;
        public final String tip; // e.g., "0.5mm Gel"
        public final String color; // e.g., "Blue"

        public PenSKU(String brand, String name, String tip, String color) {
            this.brand = Objects.requireNonNull(brand);
            this.name = Objects.requireNonNull(name);
            this.tip = Objects.requireNonNull(tip);
            this.color = Objects.requireNonNull(color);
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof PenSKU))
                return false;
            PenSKU other = (PenSKU) o;
            return this.brand.equals(other.brand)
                    && this.name.equals(other.name)
                    && this.tip.equals(other.tip)
                    && this.color.equals(other.color);
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.brand, this.name, this.tip, this.color);
        }

        @Override
        public String toString() {
            return this.brand + " " + this.name + " " + this.tip + " ("
                    + this.color + ")";
        }
    }

    /** Represents a shop with an inventory map of SKU -> quantity. */
    public static final class Shop {
        public final String id;
        private final Map<PenSKU, Integer> inventory = new HashMap<>();

        public Shop(String id) {
            this.id = Objects.requireNonNull(id);
        }

        /** Increment stock for a SKU by qty (qty >= 0). */
        public void addStock(PenSKU sku, int qty) {
            if (qty < 0) {
                throw new IllegalArgumentException("qty cannot be negative");
            }
            this.inventory.merge(sku, qty, Integer::sum);
        }

        /** Get available quantity for a SKU. */
        public int getQty(PenSKU sku) {
            return this.inventory.getOrDefault(sku, 0);
        }

        /** Reduce stock if possible; returns true if fulfilled. */
        public boolean takeStock(PenSKU sku, int qty) {
            int have = this.getQty(sku);
            if (qty <= 0 || have < qty)
                return false;
            if (qty == have) {
                this.inventory.remove(sku);
            } else {
                this.inventory.put(sku, have - qty);
            }
            return true;
        }

        /** Read-only snapshot of inventory for display. */
        public Map<PenSKU, Integer> snapshot() {
            return Collections.unmodifiableMap(new HashMap<>(this.inventory));
        }

        @Override
        public String toString() {
            return "Shop(" + this.id + ")";
        }
    }

    // --- Network with core behaviors -----------------------------------------

    public static final class PenNetwork {
        private final Map<String, Shop> shops = new HashMap<>();

        /** Add a shop to the network if it doesn't exist yet. */
        public Shop addShop(String id) {
            return this.shops.computeIfAbsent(id, Shop::new);
        }

        /** Get an existing shop or throw if missing. */
        public Shop getShopOrThrow(String id) {
            Shop s = this.shops.get(id);
            if (s == null) {
                throw new NoSuchElementException("No shop: " + id);
            }
            return s;
        }

        /**
         * Transfer stock between shops. Validates source availability; if
         * insufficient, returns false.
         */
        public boolean requestTransfer(String fromId, String toId, PenSKU sku,
                int qty) {
            if (qty <= 0)
                return false;
            Shop from = this.getShopOrThrow(fromId);
            Shop to = this.getShopOrThrow(toId);
            if (!from.takeStock(sku, qty))
                return false;
            to.addStock(sku, qty);
            return true;
        }

        /**
         * Simple "restock plan": if a shop is below a threshold for some SKU,
         * suggest pulling from the shop with the highest surplus.
         */
        public Optional<TransferPlan> computeRestockPlan(String needyShopId,
                PenSKU sku, int minTarget) {
            Shop needy = this.getShopOrThrow(needyShopId);
            int have = needy.getQty(sku);
            if (have >= minTarget)
                return Optional.empty();

            String donorId = null;
            int donorQty = 0;
            for (Shop s : this.shops.values()) {
                if (s == needy)
                    continue;
                int q = s.getQty(sku);
                if (q > donorQty) {
                    donorQty = q;
                    donorId = s.id;
                }
            }
            if (donorId == null || donorQty == 0)
                return Optional.empty();

            int needed = minTarget - have;
            int transferQty = Math.min(needed, donorQty);
            return Optional.of(
                    new TransferPlan(donorId, needyShopId, sku, transferQty));
        }

        /**
         * Find the shop with the most of a given SKU (cheap “supplier”
         * heuristic).
         */
        public Optional<String> findTopSupplier(PenSKU sku) {
            String best = null;
            int bestQty = -1;
            for (Shop s : this.shops.values()) {
                int q = s.getQty(sku);
                if (q > bestQty) {
                    bestQty = q;
                    best = s.id;
                }
            }
            return best == null ? Optional.empty() : Optional.of(best);
        }

        /** Compact printable inventory view for all shops. */
        public String inventoryReport() {
            StringBuilder sb = new StringBuilder("=== Inventory Report ===\n");
            for (Shop s : this.shops.values()) {
                sb.append(s).append("\n");
                Map<PenSKU, Integer> snap = s.snapshot();
                if (snap.isEmpty()) {
                    sb.append("  (empty)\n");
                } else {
                    for (Map.Entry<PenSKU, Integer> e : snap.entrySet()) {
                        sb.append("  - ").append(e.getKey()).append(": ")
                                .append(e.getValue()).append("\n");
                    }
                }
            }
            return sb.toString();
        }
    }

    /** Simple DTO describing a suggested transfer. */
    public static final class TransferPlan {
        public final String fromId;
        public final String toId;
        public final PenSKU sku;
        public final int qty;

        public TransferPlan(String fromId, String toId, PenSKU sku, int qty) {
            this.fromId = fromId;
            this.toId = toId;
            this.sku = sku;
            this.qty = qty;
        }

        @Override
        public String toString() {
            return "TransferPlan{from=" + this.fromId + ", to=" + this.toId
                    + ", sku=" + this.sku + ", qty=" + this.qty + "}";
        }
    }

    // --- Demo client usage ----------------------------------------------------

    public static void main(String[] args) {
        PenNetwork network = new PenNetwork();

        // 1) Define shops
        Shop campus = network.addShop("Campus-Store");
        Shop downtown = network.addShop("Downtown");
        Shop airport = network.addShop("Airport-Kiosk");

        // 2) Define a few SKUs
        PenSKU blueGel05 = new PenSKU("Uni-ball", "Signo", "0.5mm Gel", "Blue");
        PenSKU blackBall07 = new PenSKU("Pilot", "G2", "0.7mm Ball", "Black");
        PenSKU redGel05 = new PenSKU("Uni-ball", "Signo", "0.5mm Gel", "Red");

        // 3) Seed inventories
        campus.addStock(blueGel05, 20);
        campus.addStock(blackBall07, 5);

        downtown.addStock(blueGel05, 4);
        downtown.addStock(blackBall07, 12);
        downtown.addStock(redGel05, 7);

        airport.addStock(blueGel05, 0);
        airport.addStock(blackBall07, 2);

        System.out.println(network.inventoryReport());

        // 4) A client order at Airport needs Blue Gel 0.5 (qty 6). Try transfer.
        System.out.println(
                "Requesting transfer: Downtown -> Airport, BlueGel0.5 x6");
        boolean ok = network.requestTransfer("Downtown", "Airport", blueGel05,
                6);
        System.out.println("Transfer success? " + ok);
        System.out.println(network.inventoryReport());

        // 5) Compute a restock plan to ensure Airport has at least 10 BlueGel0.5
        System.out.println(
                "Computing restock plan for Airport-Kiosk to reach 10 BlueGel0.5...");
        Optional<TransferPlan> plan = network
                .computeRestockPlan("Airport-Kiosk", blueGel05, 10);
        System.out.println(plan.isPresent() ? plan.get() : "No plan available");

        // 6) Execute plan if present
        plan.ifPresent(p -> {
            boolean done = network.requestTransfer(p.fromId, p.toId, p.sku,
                    p.qty);
            System.out.println("Executed plan? " + done);
        });
        System.out.println(network.inventoryReport());

        // 7) Find top supplier for Red Gel 0.5
        System.out.println("Top supplier for Red Gel 0.5: "
                + network.findTopSupplier(redGel05).orElse("None"));

        // 8) Edge case: attempt a transfer with insufficient stock
        System.out.println(
                "Attempting excessive transfer Campus -> Downtown, BlackBall0.7 x 999");
        boolean ok2 = network.requestTransfer("Campus-Store", "Downtown",
                blackBall07, 999);
        System.out.println("Transfer success? " + ok2);
    }
}
