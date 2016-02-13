package mybatis.test.model;

/**
 *
 *
 * @author qiancheng.xq
 * @version $Id: Order.java, v 0.1 2/13/16 5:08 PM qiancheng.xq Exp $
 */
public class Order {
    private int    id;

    private String instrument;

    private String orderType;

    private int    quantity;

    @Override
    public String toString() {
        return "Order{" + "id=" + id + ", instrument='" + instrument + '\'' + ", orderType='"
               + orderType + '\'' + ", quantity=" + quantity + '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getInstrument() {
        return instrument;
    }

    public void setInstrument(String instrument) {
        this.instrument = instrument;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
