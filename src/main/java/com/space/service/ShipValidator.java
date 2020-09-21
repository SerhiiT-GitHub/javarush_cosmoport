package com.space.service;

import com.space.model.Ship;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;

@Service
public class ShipValidator {

    public boolean isShipValid(Ship ship) {
        return ship != null && isStringValid(ship.getName()) && isStringValid(ship.getPlanet())
                && isProdDateValid(ship.getProdDate())
                && isSpeedValid(ship.getSpeed())
                && isCrewSizeValid(ship.getCrewSize());
    }

    public boolean isStringValid(String value) {
        int maxStringLength = 50;
        return value != null && !value.isEmpty() && value.length() <= maxStringLength;
    }

    public boolean isProdDateValid(Date prodDate) {
        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.YEAR, 2799);
        Date minValidYear = calendar.getTime();

        calendar.set(Calendar.YEAR, 3019);
        Date maxValidYear = calendar.getTime();

        return prodDate != null && prodDate.after(minValidYear) && prodDate.before(maxValidYear);
    }

    public boolean isSpeedValid(Double speed) {
        final double minSpeed = 0.01D;
        final double maxSpeed = 0.99D;
        return speed != null && speed.compareTo(minSpeed) >= 0 && speed.compareTo(maxSpeed) <= 0;
    }

    public boolean isCrewSizeValid(Integer crewSize) {
        final int minCrewSize = 1;
        final int maxCrewSize = 9999;
        return crewSize != null && crewSize.compareTo(minCrewSize) >= 0 && crewSize.compareTo(maxCrewSize) <= 0;
    }
}
