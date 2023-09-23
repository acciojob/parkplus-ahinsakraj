package com.driver.services.impl;

import com.driver.model.ParkingLot;
import com.driver.model.Spot;
import com.driver.model.SpotType;
import com.driver.repository.ParkingLotRepository;
import com.driver.repository.SpotRepository;
import com.driver.services.ParkingLotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ParkingLotServiceImpl implements ParkingLotService {
    @Autowired
    ParkingLotRepository parkingLotRepository1;
    @Autowired
    SpotRepository spotRepository1;
    @Override
    public ParkingLot addParkingLot(String name, String address) {
        ParkingLot parkingLot = new ParkingLot(name, address);
        ParkingLot savedParkingLot =  parkingLotRepository1.save(parkingLot);
        return savedParkingLot;

    }

    @Override
    public Spot addSpot(int parkingLotId, Integer numberOfWheels, Integer pricePerHour) {
        // Find the proper SpotType
        SpotType spotType = SpotType.TWO_WHEELER;
        if(numberOfWheels > 2 && numberOfWheels <= 4) {
            spotType = SpotType.FOUR_WHEELER;
        } else if (numberOfWheels > 4) {
            spotType = SpotType.OTHERS;
        }

        // Get the parking Lot
        Optional<ParkingLot> optionalParkingLot = parkingLotRepository1.findById(parkingLotId);
        if(!optionalParkingLot.isPresent()) {
            return new Spot();
        }

        ParkingLot parkingLot = optionalParkingLot.get();

        // Prepare the Spot
        Spot spot = new Spot();
        spot.setSpotType(spotType);
        spot.setPricePerHour(pricePerHour);
        spot.setOccupied(Boolean.FALSE);
        spot.setParkingLot(parkingLot);

        // set the spot to the parking lot
        parkingLot.getSpotList().add(spot);
        parkingLotRepository1.save(parkingLot);

        Spot savedSpot = spotRepository1.save(spot);
        return savedSpot;
    }

    @Override
    public void deleteSpot(int spotId) {
        spotRepository1.deleteById(spotId);
    }

    @Override
    public Spot updateSpot(int parkingLotId, int spotId, int pricePerHour) {
        Optional<ParkingLot> optionalParkingLot = parkingLotRepository1.findById(parkingLotId);
        if(!optionalParkingLot.isPresent()) return new Spot();
        Optional<Spot> optionalSpot = spotRepository1.findById(spotId);
        if(!optionalSpot.isPresent()) return new Spot();
        ParkingLot parkingLot = optionalParkingLot.get();
        Spot spot = optionalSpot.get();
        spot.setPricePerHour(pricePerHour);
        spotRepository1.save(spot);
        parkingLotRepository1.save(parkingLot);
        return spot;
    }

    @Override
    public void deleteParkingLot(int parkingLotId) {
        parkingLotRepository1.deleteById(parkingLotId);
    }
}
