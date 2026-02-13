package com.astana.entities;

// Demonstrating Inheritance: VipRoom is-a Room
public class VipRoom extends Room {
    private double luxuryTax;

    public VipRoom(int roomNumber, double price) {
        // Calls the constructor of the parent 'Room' class
        super(roomNumber, "VIP Suite", price);
        this.luxuryTax = 0.20; // 20% extra for VIP services
    }

    // Demonstrating Polymorphism: Overriding the price calculation
    @Override
    public double getPrice() {
        return super.getPrice() * (1 + luxuryTax);
    }
}
