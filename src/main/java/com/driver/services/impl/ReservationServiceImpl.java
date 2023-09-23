package com.driver.services.impl;

import com.driver.model.*;
import com.driver.repository.ParkingLotRepository;
import com.driver.repository.ReservationRepository;
import com.driver.repository.SpotRepository;
import com.driver.repository.UserRepository;
import com.driver.services.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ReservationServiceImpl implements ReservationService {
    @Autowired
    UserRepository userRepository3;
    @Autowired
    SpotRepository spotRepository3;
    @Autowired
    ReservationRepository reservationRepository3;
    @Autowired
    ParkingLotRepository parkingLotRepository3;
    @Override
    public Reservation reserveSpot(Integer userId, Integer parkingLotId, Integer timeInHours, Integer numberOfWheels) throws Exception {
        Optional<User> optionalUser = userRepository3.findById(userId);
        if(!optionalUser.isPresent()) {
            throw new Exception("Cannot make reservation");
        }

        Optional<ParkingLot> optionalParkingLot = parkingLotRepository3.findById(parkingLotId);
        if(!optionalParkingLot.isPresent()) {
            throw new Exception("Cannot make reservation");
        }

        User user = optionalUser.get();
        ParkingLot parkingLot = optionalParkingLot.get();

        List<Spot> spotList = new ArrayList<>();

        if(numberOfWheels <= 2) {
            spotList.addAll(spotRepository3.findBySpotType(SpotType.TWO_WHEELER));
        }
        if(numberOfWheels <= 4) {
            spotList.addAll(spotRepository3.findBySpotType(SpotType.FOUR_WHEELER));
        }
        spotList.addAll(spotRepository3.findBySpotType(SpotType.OTHERS));

        Spot spot = null;
        for(Spot s : spotList) {
            if(s == null || s.getPricePerHour() < spot.getPricePerHour()) {
                spot = s;
            }
        }

        if(spot == null) {
            throw new Exception("Cannot make reservation");
        }


        // Prepare Payment
        Payment payment = new Payment();
        payment.setPaymentCompleted(Boolean.FALSE);



        Reservation reservation = new Reservation();
        reservation.setNumberOfHours(timeInHours);
        reservation.setSpot(spot);
        reservation.setUser(user);
        reservation.setPayment(payment);


        // Save the Reservation
        Reservation savedReservation = reservationRepository3.save(reservation);
        return savedReservation;
    }
}
