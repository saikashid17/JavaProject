public class SaleRecord {
    public String dateStr;
    public String fruitName;
    public int quantity;
    public double totalPrice;
    public String customerName;

    public SaleRecord(String dateStr, String fruitName, int quantity, double totalPrice, String customerName) {
        this.dateStr = dateStr;
        this.fruitName = fruitName;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
        this.customerName = customerName;
    }
}
