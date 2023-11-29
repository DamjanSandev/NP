package Napredno.Lab1.Lab1_3;

import java.util.Objects;
import java.util.*;
import java.util.stream.Collectors;

public class FlatAmountProvisionTransaction extends Transaction{
    private String flatProvision;
    public FlatAmountProvisionTransaction(long fromId, long toId, String amount, String flatProvision) {
        super(fromId,toId,"FlatAmount",amount);
        this.flatProvision=flatProvision;
    }

    public String getFlatProvision() {
        return flatProvision;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        FlatAmountProvisionTransaction that = (FlatAmountProvisionTransaction) o;
        return Objects.equals(flatProvision, that.flatProvision);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), flatProvision);
    }

    @Override
    double getProvision() {
        String []parts=flatProvision.split("\\$");
        return Double.parseDouble(parts[0]);
    }
}
