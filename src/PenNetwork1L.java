import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Kernel implementation of the PenNetwork component.
 *
 * @convention rep != null AND for every shop in rep.keySet(): shop != null AND
 *             shop != "" AND rep.get(shop) != null AND for every pen in
 *             rep.get(shop).keySet(): pen != null AND pen != "" AND
 *             rep.get(shop).get(pen) >= 0
 *
 * @correspondence The abstract PenNetwork is the finite mapping: shopID ↦
 *                 (penID ↦ quantity) where: getStock(shop, pen) =
 *                 rep.get(shop).getOrDefault(pen, 0)
 */
public final class PenNetwork1L extends PenNetworkSecondary {

    /** Representation: shopID -> (penID -> qty). */
    private Map<String, Map<String, Integer>> rep;

    // --------------------------------------------------------------
    // Standard Methods
    // --------------------------------------------------------------

    /**
     * Default constructor initializes empty network.
     */
    public PenNetwork1L() {
        this.rep = new HashMap<>();
    }

    // --------------------------------------------------------------
    // Kernel Methods (from PenNetworkKernel)
    // --------------------------------------------------------------

    @Override
    public void add(String shop, String pen, int quantity) {
        Objects.requireNonNull(shop);
        Objects.requireNonNull(pen);

        if (quantity <= 0) {
            throw new IllegalArgumentException("quantity must be > 0");
        }

        Map<String, Integer> shopMap = this.rep.computeIfAbsent(shop,
                s -> new HashMap<>());

        shopMap.merge(pen, quantity, Integer::sum);
    }

    @Override
    public void transfer(String fromShop, String toShop, String pen,
            int quantity) {

        Objects.requireNonNull(fromShop);
        Objects.requireNonNull(toShop);
        Objects.requireNonNull(pen);

        if (quantity <= 0) {
            throw new IllegalArgumentException("quantity must be > 0");
        }

        // get source shop + quantities
        Map<String, Integer> fromMap = this.rep.get(fromShop);
        if (fromMap == null) {
            throw new IllegalArgumentException("fromShop does not exist");
        }

        int available = fromMap.getOrDefault(pen, 0);
        if (available < quantity) {
            throw new IllegalArgumentException("insufficient stock");
        }

        // deduct from source
        int remaining = available - quantity;
        if (remaining == 0) {
            fromMap.remove(pen);
        } else {
            fromMap.put(pen, remaining);
        }

        // add to destination shop
        Map<String, Integer> toMap = this.rep.computeIfAbsent(toShop,
                s -> new HashMap<>());
        toMap.merge(pen, quantity, Integer::sum);
    }

    @Override
    public int getStock(String shop, String pen) {
        Objects.requireNonNull(shop);
        Objects.requireNonNull(pen);

        Map<String, Integer> shopMap = this.rep.get(shop);
        if (shopMap == null) {
            throw new IllegalArgumentException("unknown shop: " + shop);
        }

        return shopMap.getOrDefault(pen, 0);
    }

    @Override
    public int getShopCount() {
        return this.rep.size();
    }
}
