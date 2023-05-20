package com.example.similebank;

public class BankAccount {

    private double balance;

    public BankAccount(double initialBalance){

        if(initialBalance < 0)
            initialBalance = initialBalance * (-1);

        balance = initialBalance;
    }

    public void withdrow(double amount){
        balance -= amount;
        if(balance <= 0)
            balance = 0;
    }

    public void deposit(double amount){
        if(amount < 0)
            amount = amount * (-1);

        balance += amount;
    }

    public void addInterest(double n){
        balance = balance + (balance*n)/100;
    }

    public double getBalance(){
        return balance;
    }
}
