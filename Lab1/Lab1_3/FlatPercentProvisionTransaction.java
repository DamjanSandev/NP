package Napredno.Lab1.Lab1_3;

import java.util.Objects;

public class FlatPercentProvisionTransaction extends Transaction{
    private int centsPerDolar;

    public FlatPercentProvisionTransaction(long fromId, long toID, String amount,int centsPerDolar) {
        super(fromId, toID,"FlatPercent", amount);
        this.centsPerDolar=centsPerDolar;
    }

    public int getCentsPerDolar() {
        return centsPerDolar;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        FlatPercentProvisionTransaction that = (FlatPercentProvisionTransaction) o;
        return centsPerDolar == that.centsPerDolar;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), centsPerDolar);
    }

    @Override
    double getProvision() {
         return (centsPerDolar/100.00) * (int) Double.parseDouble(super.getAmount().substring(0, super.getAmount().length() - 1));
    }

}
