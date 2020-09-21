package com.space.service;

import com.space.controller.ShipOrder;
import com.space.model.Ship;
import com.space.model.ShipType;
import com.space.repository.ShipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;

@Service
@Transactional
public class ShipServiceImpl implements ShipService {

    @Autowired
    private ShipRepository shipRepository;

    @Autowired
    private ShipValidator shipValidator;

    @Override
    public List<Ship> getShips(String name,
                               String planet,
                               ShipType shipType,
                               Long after,
                               Long before,
                               Boolean isUsed,
                               Double minSpeed,
                               Double maxSpeed,
                               Integer minCrewSize,
                               Integer maxCrewSize,
                               Double minRating,
                               Double maxRating) {
        Date afterDate = after == null ? null : new Date(after);
        Date beforeDate = before == null ? null : new Date(before);
        List<Ship> list = new ArrayList<>();
        shipRepository.findAll().forEach((ship) -> {
            if (name != null && !ship.getName().contains(name)) return;
            if (planet != null && !ship.getPlanet().contains(planet)) return;
            if (shipType != null && ship.getShipType() != shipType) return;
            if (afterDate != null && ship.getProdDate().before(afterDate)) return;
            if (beforeDate != null && ship.getProdDate().after(beforeDate)) return;
            if (isUsed != null && ship.getUsed().booleanValue() != isUsed.booleanValue()) return;
            if (minSpeed != null && ship.getSpeed().compareTo(minSpeed) < 0) return;
            if (maxSpeed != null && ship.getSpeed().compareTo(maxSpeed) > 0) return;
            if (minCrewSize != null && ship.getCrewSize().compareTo(minCrewSize) < 0) return;
            if (maxCrewSize != null && ship.getCrewSize().compareTo(maxCrewSize) > 0) return;
            if (minRating != null && ship.getRating().compareTo(minRating) < 0) return;
            if (maxRating != null && ship.getRating().compareTo(maxRating) > 0) return;
            list.add(ship);
        });
        return list;
    }

    @Override
    public Ship getShip(Long id) {
        return shipRepository.findById(id).orElse(null);
    }

    @Override
    public Ship saveShip(Ship ship) {
        return shipRepository.save(ship);
    }

    @Override
    public Ship updateShip(Ship updatedShip, Ship existingShip) throws IllegalArgumentException {
        boolean isNeedToChangeRating = false;

        String name = existingShip.getName();
        if (name != null) {
            if (shipValidator.isStringValid(name)) {
                updatedShip.setName(name);
            }
            else {
                throw new IllegalArgumentException();
            }
        }

        String planet = existingShip.getPlanet();
        if (planet != null) {
            if (shipValidator.isStringValid(planet)) {
                updatedShip.setPlanet(planet);
            }
            else {
                throw new IllegalArgumentException();
            }
        }

        if (existingShip.getShipType() != null) {
            updatedShip.setShipType(existingShip.getShipType());
        }

        Date prodDate = existingShip.getProdDate();
        if (prodDate != null) {
            if (shipValidator.isProdDateValid(prodDate)) {
                updatedShip.setProdDate(prodDate);
                isNeedToChangeRating = true;
            } else {
                throw new IllegalArgumentException();
            }
        }

        Boolean isUsed = existingShip.getUsed();
        if (isUsed != null) {
            updatedShip.setUsed(isUsed);
            isNeedToChangeRating = true;
        }

        Double speed = existingShip.getSpeed();
        if (speed != null) {
            if (shipValidator.isSpeedValid(speed)) {
                updatedShip.setSpeed(speed);
                isNeedToChangeRating = true;
            } else {
                throw new IllegalArgumentException();
            }
        }

        Integer crewSize = existingShip.getCrewSize();
        if (crewSize != null) {
            if (shipValidator.isCrewSizeValid(crewSize)) {
                updatedShip.setCrewSize(crewSize);
            }
            else {
                throw new IllegalArgumentException();
            }
        }

        if (isNeedToChangeRating) {
            double rating = computeRating(updatedShip.getSpeed(), updatedShip.getUsed(), updatedShip.getProdDate());
            updatedShip.setRating(rating);
        }
        shipRepository.save(updatedShip);
        return updatedShip;
    }

    @Override
    public void deleteShip(Ship ship) {
        shipRepository.delete(ship);
    }

    @Override
    public double computeRating(double speed, boolean isUsed, Date prodDate) {
        int currentYear = 3019;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(prodDate);
        int prodYear = calendar.get(Calendar.YEAR);

        double coefficient = isUsed ? 0.5 : 1;
        double rating = (80 * speed * coefficient) / (currentYear - prodYear + 1);
        return Math.round(rating * 100) / 100D;
    }

    @Override
    public List<Ship> sortShips(List<Ship> ships, ShipOrder order) {
        if (order != null) {
            Comparator<Ship> comparator = (firstShip, secondShip) -> {
                switch (order) {
                    case ID: return firstShip.getId().compareTo(secondShip.getId());
                    case SPEED: return firstShip.getSpeed().compareTo(secondShip.getSpeed());
                    case DATE: return firstShip.getProdDate().compareTo(secondShip.getProdDate());
                    case RATING: return firstShip.getRating().compareTo(secondShip.getRating());
                    default: return 0;
                }
            };
            ships.sort(comparator);
        }
        return ships;
    }

    @Override
    public List<Ship> getPage(List<Ship> ships, Integer pageNumber, Integer pageSize) {
        Integer page = pageNumber == null ? 0 : pageNumber;
        Integer size = pageSize == null ? 3 : pageSize;
        int from = page * size;
        int to = from + size;
        if (to > ships.size()) to = ships.size();
        return ships.subList(from, to);
    }
}
