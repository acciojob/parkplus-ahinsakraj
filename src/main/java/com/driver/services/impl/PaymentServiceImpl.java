package com.driver.services.impl;

import com.driver.model.Payment;
import com.driver.model.PaymentMode;
import com.driver.model.Reservation;
import com.driver.model.Spot;
import com.driver.repository.PaymentRepository;
import com.driver.repository.ReservationRepository;
import com.driver.repository.SpotRepository;
import com.driver.services.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PaymentServiceImpl implements PaymentService {
    @Autowired
    ReservationRepository reservationRepository2;
    @Autowired
    PaymentRepository paymentRepository2;

    @Override
    public Payment pay(Integer reservationId, int amountSent, String mode) throws Exception {


        // Validate and find reservation
        Optional<Reservation> optionalReservation = reservationRepository2.findById(reservationId);
        if(!optionalReservation.isPresent()) {
            return new Payment();
        }
        Reservation reservation = optionalReservation.get();

        // Get the reservation member to be updated
        Payment payment = reservation.getPayment();
        Spot spot = reservation.getSpot();


        // format Mode
        mode = mode.toUpperCase();
        switch (mode) {
            case "CASH":
                payment.setPaymentMode(PaymentMode.CASH);
                break;
            case "UPI":
                payment.setPaymentMode(PaymentMode.UPI);
                break;
            case "CARD":
                payment.setPaymentMode(PaymentMode.CARD);
                break;
            default:
                throw new Exception("Payment mode not detected");
        }

        // Calculate price
        int pricePerHour = spot.getPricePerHour();
        int timeInHours = reservation.getNumberOfHours();
        int totalPrice = pricePerHour * timeInHours;

        // validate amount
        if(amountSent < totalPrice) {
            throw new Exception("Insufficient Amount");
        }


        spot.setOccupied(Boolean.TRUE);
        payment.setPaymentCompleted(Boolean.TRUE);

        // It will save the updated spot and payment details
        Reservation savedReservation = reservationRepository2.save(reservation);
        return payment;
    }
}
