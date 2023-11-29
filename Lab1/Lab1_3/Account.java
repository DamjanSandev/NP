package Napredno.Lab1.Lab1_3;

import java.util.Objects;
import java.util.Random;

public class Account {
    private String name;
    private Long id;
    private String balance;

    public Account(String name, String balance) {
        this.name = name;
        this.balance = balance;
        this.id= new Random().nextLong();
    }

    public Account(Account acc) {
        this.name = acc.name;
        this.balance = acc.balance;
        this.id= acc.id;
    }

    public String getName() {
        return name;
    }

    public Long getId() {
        return id;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    @Override
    public String toString() {
       return "Name: " + name + "\nBalance: " + balance + "\n";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return Objects.equals(name, account.name) && Objects.equals(id, account.id) && Objects.equals(balance, account.balance);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, id, balance);
    }

}