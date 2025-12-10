import java.util.Objects;

/**
 * Immutable record of a suggested stock transfer between shops.
 *
 * @author Sandeep
 */
public final class TransferPlan {

    public final String fromId;
    public final String toId;
    public final PenSKU sku;
    public final int qty;

    /**
     * Constructs a transfer plan object.
     *
     * @param fromId
     *            the shop giving inventory
     * @param toId
     *            the shop receiving inventory
     * @param sku
     *            the pen product
     * @param qty
     *            the quantity to transfer (must be > 0)
     */
    public TransferPlan(String fromId, String toId, PenSKU sku, int qty) {
        this.fromId = Objects.requireNonNull(fromId);
        this.toId = Objects.requireNonNull(toId);
        this.sku = Objects.requireNonNull(sku);
        if (qty <= 0) {
            throw new IllegalArgumentException("qty must be > 0");
        }
        this.qty = qty;
    }

    @Override
    public String toString() {
        return "TransferPlan{from=" + this.fromId + ", to=" + this.toId
                + ", sku=" + this.sku + ", qty=" + this.qty + "}";
    }
}