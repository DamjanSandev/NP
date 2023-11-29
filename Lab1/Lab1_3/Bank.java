package Napredno.Lab1.Lab1_3;

import java.util.Arrays;
import java.util.Objects;

public class Bank {
     private String name;
     private Account []accounts;
     private double totaltransfers;
     private  double totalprosivion;

    public Bank(String name, Account[] accounts) {
        this.totalprosivion=0;
        this.totaltransfers=0;
        this.name = name;
        this.accounts = accounts;
    }

    public Account[] getAccounts() {
        return accounts;
    }

    public boolean makeTransaction(Transaction t){
         long fromid=t.getFromId(),toid=t.getToID();
         int fromi=-1,toi=-1;
        for (int i=0;i<accounts.length;i++) {
            if(accounts[i].getId()==fromid){
                fromi=i;
            }
            if(accounts[i].getId()==toid){
                    toi=i;
                }
            }

        if(fromi==-1 || toi==-1){
            return false;
        }
        double transactionAmount= StringToDouble.StringtoDouble(t.getAmount());

        double fromAmount= StringToDouble.StringtoDouble(accounts[fromi].getBalance());

        double ToAmount=StringToDouble.StringtoDouble(accounts[toi].getBalance());

        if(fromAmount < transactionAmount){
            return false;
        }
        double provision=t.getProvision();
        totaltransfers+=transactionAmount;
        totalprosivion+=provision;
        if(accounts[fromi].equals(accounts[toi])){
            fromAmount-=provision;
            accounts[fromi].setBalance(String.format("%.2f", fromAmount) + "$");
        }
        else {
            fromAmount = fromAmount - (transactionAmount + provision);
            ToAmount+=
            transactionAmount;
            accounts[fromi].setBalance(String.format("%.2f", fromAmount) + "$");
            accounts[toi].setBalance(String.format("%.2f", ToAmount) + "$");
        }


        return true;
    }

    public String totalTransfers() {
        return String.format("%.2f", totaltransfers) + "$";
    }

    public String totalProvision() {
        return String.format("%.2f", totalprosivion) + "$";
    }

    @Override
    public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("Name: ");
            sb.append(name);
            sb.append("\n\n");
            for (Account account : accounts)
                sb.append(account);
            return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Bank bank = (Bank) o;
        return Double.compare(totaltransfers, bank.totaltransfers) == 0 && Double.compare(totalprosivion, bank.totalprosivion) == 0 && Objects.equals(name, bank.name) && Arrays.equals(accounts, bank.accounts);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(name, totaltransfers, totalprosivion);
        result = 31 * result + Arrays.hashCode(accounts);
        return result;
    }
}
