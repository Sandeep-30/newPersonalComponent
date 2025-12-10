import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Optional;

import org.junit.Test;

/**
 * JUnit tests for the higher-level PenNetwork operations (implemented by the
 * abstract class PenNetworkSecondary and exposed via the PenNetwork interface).
 *
 * Methods tested: - requestTransfer - findTopSupplier - computeRestockPlan -
 * inventoryReport
 *
 * All calls satisfy the documented @requires clauses.
 *
 * @author Sandeep
 */
public class PenNetworkTest {

    /**
     * Helper to create an empty network with dynamic type PenNetwork1L.
     */
    private PenNetwork createEmptyNetwork() {
        return new PenNetwork1L();
    }

    /**
     * Helper that adds stock for a PenSKU using the same key convention as the
     * secondary operations (typically sku.toString()).
     */
    private void addStockForSku(PenNetwork net, String shop, PenSKU sku,
            int qty) {
        net.add(shop, sku.toString(), qty);
    }

    /**
     * requestTransfer: successful transfer between two existing shops.
     */
    @Test
    public void testRequestTransferSuccess() {
        PenNetwork net = this.createEmptyNetwork();

        PenSKU blueGel = new PenSKU("Uni-ball", "Signo", "0.5mm Gel", "Blue");

        // Ensure both shops exist and have stock as required.
        this.addStockForSku(net, "ShopA", blueGel, 10);
        this.addStockForSku(net, "ShopB", blueGel, 2);

        boolean result = net.requestTransfer("ShopA", "ShopB", blueGel, 4);

        assertTrue(result);
        assertEquals(6, net.getStock("ShopA", blueGel.toString()));
        assertEquals(6, net.getStock("ShopB", blueGel.toString()));
        assertEquals(2, net.getShopCount());
    }

    /**
     * requestTransfer: insufficient stock -> no transfer and state unchanged.
     */
    @Test
    public void testRequestTransferInsufficientStock() {
        PenNetwork net = this.createEmptyNetwork();

        PenSKU blueGel = new PenSKU("Uni-ball", "Signo", "0.5mm Gel", "Blue");

        this.addStockForSku(net, "ShopA", blueGel, 3); // not enough for qty=5
        this.addStockForSku(net, "ShopB", blueGel, 1);

        boolean result = net.requestTransfer("ShopA", "ShopB", blueGel, 5);

        assertFalse(result);
        // Quantities should be unchanged
        assertEquals(3, net.getStock("ShopA", blueGel.toString()));
        assertEquals(1, net.getStock("ShopB", blueGel.toString()));
    }

    /**
     * findTopSupplier: returns the shop with the highest quantity of a SKU.
     */
    @Test
    public void testFindTopSupplierBasic() {
        PenNetwork net = this.createEmptyNetwork();

        PenSKU redGel = new PenSKU("Uni-ball", "Signo", "0.5mm Gel", "Red");

        this.addStockForSku(net, "Shop0", redGel, 5);
        this.addStockForSku(net, "Shop1", redGel, 12);
        this.addStockForSku(net, "Shop2", redGel, 7);

        Optional<String> top = net.findTopSupplier(redGel);

        assertTrue(top.isPresent());
        assertEquals("Shop1", top.get());
    }

    /**
     * findTopSupplier: if no shop carries the SKU, result is empty.
     */
    @Test
    public void testFindTopSupplierNone() {
        PenNetwork net = this.createEmptyNetwork();

        PenSKU rareSku = new PenSKU("Pilot", "G2", "0.7mm Ball", "Purple");

        // We create shops but never stock this particular SKU.
        net.add("Shop0", "OTHER-SKU", 5);
        net.add("Shop1", "ANOTHER-SKU", 10);

        Optional<String> top = net.findTopSupplier(rareSku);

        assertTrue(top.isPresent());
    }

    /**
     * computeRestockPlan: needy shop already at or above minimum -> no plan.
     */
    @Test
    public void testComputeRestockPlanAlreadySufficient() {
        PenNetwork net = this.createEmptyNetwork();

        PenSKU blueGel = new PenSKU("Uni-ball", "Signo", "0.5mm Gel", "Blue");

        this.addStockForSku(net, "Shop0", blueGel, 10);
        this.addStockForSku(net, "Shop1", blueGel, 3);

        // Shop0 already has >= 8
        Optional<TransferPlan> plan = net.computeRestockPlan("Shop0", blueGel,
                8);

        assertFalse(plan.isPresent());
    }

    /**
     * computeRestockPlan: needy shop below target, donor has surplus.
     *
     * Expect plan.fromId to be donor with max stock and qty to be min(needed,
     * donorQty) per the spec.
     */
    @Test
    public void testComputeRestockPlanBasic() {
        PenNetwork net = this.createEmptyNetwork();

        PenSKU blueGel = new PenSKU("Uni-ball", "Signo", "0.5mm Gel", "Blue");

        // Needy shop
        this.addStockForSku(net, "Shop2", blueGel, 2);

        // Donors
        this.addStockForSku(net, "Shop0", blueGel, 10);
        this.addStockForSku(net, "Shop1", blueGel, 6);

        int minTarget = 9; // Shop2 needs to reach 9 (needs 7 more)
        Optional<TransferPlan> planOpt = net.computeRestockPlan("Shop2",
                blueGel, minTarget);

        assertTrue(planOpt.isPresent());
        TransferPlan plan = planOpt.get();

        // Shop0 has the highest quantity (10), so should be chosen.
        assertEquals("Shop0", plan.fromId);
        assertEquals("Shop2", plan.toId);
        assertEquals(blueGel, plan.sku);

        int have = 2;
        int donorQty = 10;
        int needed = minTarget - have; // 7
        int expectedQty = Math.min(needed, donorQty); // 7
        assertEquals(expectedQty, plan.qty);
    }

    /**
     * inventoryReport: contains all shops and SKU quantities in a
     * human-readable format. We don't assert the entire format, only that key
     * information appears.
     */
    @Test
    public void testInventoryReportContainsShops() {
        PenNetwork net = this.createEmptyNetwork();

        PenSKU blueGel = new PenSKU("Uni-ball", "Signo", "0.5mm Gel", "Blue");
        PenSKU blackBall = new PenSKU("Pilot", "G2", "0.7mm Ball", "Black");

        this.addStockForSku(net, "Shop0", blueGel, 5);
        this.addStockForSku(net, "Shop0", blackBall, 3);
        this.addStockForSku(net, "Shop1", blueGel, 2);

        String report = net.inventoryReport();

        assertTrue(report.contains("Shop0"));
        assertTrue(report.contains("Shop1"));
    }
}
