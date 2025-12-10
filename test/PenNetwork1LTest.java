import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * JUnit tests for the PenNetwork1L kernel implementation.
 *
 * Tests only the kernel operations: - add - transfer - getStock - getShopCount
 *
 * All tests satisfy the @requires clauses (no precondition-violation tests).
 *
 * @author Sandeep
 */
public class PenNetwork1LTest {

    /**
     * Helper to create an empty network.
     */
    private PenNetwork1L createEmpty() {
        return new PenNetwork1L();
    }

    /**
     * Constructor: empty network has 0 shops.
     */
    @Test
    public void testConstructorEmptyHasZeroShops() {
        PenNetwork1L net = this.createEmpty();

        assertEquals(0, net.getShopCount());
    }

    /**
     * add: single shop + single pen.
     */
    @Test
    public void testAddSingleShopSinglePen() {
        PenNetwork1L net = this.createEmpty();

        net.add("ShopA", "PEN-001", 10);

        assertEquals(1, net.getShopCount());
        assertEquals(10, net.getStock("ShopA", "PEN-001"));
    }

    /**
     * add: adding same pen twice accumulates quantity.
     */
    @Test
    public void testAddSamePenAccumulates() {
        PenNetwork1L net = this.createEmpty();

        net.add("ShopA", "PEN-001", 5);
        net.add("ShopA", "PEN-001", 7);

        assertEquals(1, net.getShopCount());
        assertEquals(12, net.getStock("ShopA", "PEN-001"));
    }

    /**
     * add: adding different pens to the same shop.
     */
    @Test
    public void testAddDifferentPensSameShop() {
        PenNetwork1L net = this.createEmpty();

        net.add("ShopA", "PEN-001", 10);
        net.add("ShopA", "PEN-002", 4);

        assertEquals(1, net.getShopCount());
        assertEquals(10, net.getStock("ShopA", "PEN-001"));
        assertEquals(4, net.getStock("ShopA", "PEN-002"));
    }

    /**
     * add: adding pens to multiple shops.
     */
    @Test
    public void testAddMultipleShops() {
        PenNetwork1L net = this.createEmpty();

        net.add("ShopA", "PEN-001", 10);
        net.add("ShopB", "PEN-001", 3);

        assertEquals(2, net.getShopCount());
        assertEquals(10, net.getStock("ShopA", "PEN-001"));
        assertEquals(3, net.getStock("ShopB", "PEN-001"));
    }

    /**
     * transfer: basic transfer from existing shop A to existing shop B.
     */
    @Test
    public void testTransferBasic() {
        PenNetwork1L net = this.createEmpty();

        net.add("ShopA", "PEN-001", 10);
        net.add("ShopB", "PEN-001", 5);

        // pre: stock(ShopA, PEN-001) >= 4
        net.transfer("ShopA", "ShopB", "PEN-001", 4);

        assertEquals(6, net.getStock("ShopA", "PEN-001"));
        assertEquals(9, net.getStock("ShopB", "PEN-001"));
        assertEquals(2, net.getShopCount());
    }

    /**
     * transfer: destination shop does not exist beforehand; created by
     * transfer.
     */
    @Test
    public void testTransferCreatesDestinationShop() {
        PenNetwork1L net = this.createEmpty();

        // Only fromShop exists initially.
        net.add("ShopA", "PEN-001", 8);

        // toShop ("ShopB") does not exist yet, but transfer should create it.
        net.transfer("ShopA", "ShopB", "PEN-001", 3);
        assertEquals(2, net.getShopCount());
        assertEquals(5, net.getStock("ShopA", "PEN-001"));
        assertEquals(3, net.getStock("ShopB", "PEN-001"));
    }

    /**
     * transfer: moving all stock of a pen from one shop to another. From the
     * outside, getStock(from, pen) becomes 0 and getStock(to, pen) increases.
     */
    @Test
    public void testTransferAllStock() {
        PenNetwork1L net = this.createEmpty();

        net.add("ShopA", "PEN-001", 6);
        net.add("ShopB", "PEN-001", 2);

        // Move all 6 units from A to B.
        net.transfer("ShopA", "ShopB", "PEN-001", 6);

        assertEquals(0, net.getStock("ShopA", "PEN-001"));
        assertEquals(8, net.getStock("ShopB", "PEN-001"));
        assertEquals(2, net.getShopCount());
    }
}
