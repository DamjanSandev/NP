package Napredno.Lab1.Lab1_3;

import java.util.Objects;

abstract public class Transaction {
     private long fromId;
     private long toID;
     private String description;
     private String amount;

     public Transaction(long fromId, long toID, String description, String amount) {
         this.fromId = fromId;
         this.toID = toID;
         this.description = description;
         this.amount = amount;
     }

    public String getDescription() {
        return description;
    }

    public long getFromId() {
         return fromId;
     }

     public long getToID() {
         return toID;
     }

     public String getAmount() {
         return amount;
     }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return fromId == that.fromId && toID == that.toID && Objects.equals(description, that.description) && Objects.equals(amount, that.amount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fromId, toID, description, amount);
    }
    abstract double getProvision();
}
