package com.space.controller;

import com.space.model.Ship;
import com.space.model.ShipType;
import com.space.service.ShipService;
import com.space.service.ShipValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ShipController {

    @Autowired
    private ShipService shipService;

    @Autowired
    private ShipValidator shipValidator;

    @RequestMapping(path = "/rest/ships", method = RequestMethod.GET)
    public List<Ship> getAllShips(
        @RequestParam(value = "name", required = false) String name,
        @RequestParam(value = "planet", required = false) String planet,
        @RequestParam(value = "shipType", required = false) ShipType shipType,
        @RequestParam(value = "after", required = false) Long after,
        @RequestParam(value = "before", required = false) Long before,
        @RequestParam(value = "isUsed", required = false) Boolean isUsed,
        @RequestParam(value = "minSpeed", required = false) Double minSpeed,
        @RequestParam(value = "maxSpeed", required = false) Double maxSpeed,
        @RequestParam(value = "minCrewSize", required = false) Integer minCrewSize,
        @RequestParam(value = "maxCrewSize", required = false) Integer maxCrewSize,
        @RequestParam(value = "minRating", required = false) Double minRating,
        @RequestParam(value = "maxRating", required = false) Double maxRating,
        @RequestParam(value = "order", required = false) ShipOrder order,
        @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
        @RequestParam(value = "pageSize", required = false) Integer pageSize
    ) {
        List<Ship> ships = shipService.getShips(name, planet, shipType, after, before, isUsed, minSpeed, maxSpeed,
                minCrewSize, maxCrewSize, minRating, maxRating);

        final List<Ship> sortedShips = shipService.sortShips(ships, order);

        return shipService.getPage(sortedShips, pageNumber, pageSize);
    }

    @RequestMapping(path = "/rest/ships/{id}", method = RequestMethod.GET)
    public ResponseEntity<Ship> getShip(@PathVariable(value = "id") Long shipId) {
        if (shipId == null || shipId <= 0) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        final Ship ship = shipService.getShip(shipId);
        if (ship == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(ship, HttpStatus.OK);
    }

    @RequestMapping(path = "/rest/ships", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Ship> createShip(@RequestBody Ship ship) {
        if (!shipValidator.isShipValid(ship)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if (ship.getUsed() == null) {
            ship.setUsed(false);
        }
        ship.setRating(shipService.computeRating(ship.getSpeed(), ship.getUsed(), ship.getProdDate()));
        Ship savedShip = shipService.saveShip(ship);

        return new ResponseEntity<>(savedShip, HttpStatus.OK);
    }

    @RequestMapping(path = "/rest/ships/{id}", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Ship> updateShip(
            @PathVariable(value = "id") Long shipId,
            @RequestBody Ship ship) {
        if (shipId == null || shipId <= 0) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        ResponseEntity<Ship> entity = getShip(shipId);
        Ship existingShip = entity.getBody();
        if (ship == null || existingShip == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Ship updatedShip;
        try {
            updatedShip = shipService.updateShip(existingShip, ship);
        }
        catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(updatedShip, HttpStatus.OK);
    }


//    @RequestMapping(path = "/rest/ships/{id}", method = RequestMethod.POST)
//    @ResponseBody
//    public ResponseEntity<Ship> updateShip(
//            @PathVariable(value = "id") Long shipId,
//            @RequestBody Ship ship
//    ) {
//        ResponseEntity<Ship> entity = getShip(shipId);
//        Ship savedShip = entity.getBody();
//        if (savedShip == null) {
//            return entity;
//        }
//        Ship result;
//        try {
//            result = shipService.updateShip(savedShip, ship);
//        } catch (IllegalArgumentException e) {
//            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
//        }
//        return new ResponseEntity<>(result, HttpStatus.OK);
//    }




    @RequestMapping(path = "/rest/ships/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Ship> deleteShip(@PathVariable(value = "id") Long shipId) {
        ResponseEntity<Ship> entity = getShip(shipId);
        Ship savedShip = entity.getBody();
        if (savedShip == null) {
            return entity;
        }
        shipService.deleteShip(savedShip);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(path = "/rest/ships/count", method = RequestMethod.GET)
    public Integer getShipsCount(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "planet", required = false) String planet,
            @RequestParam(value = "shipType", required = false) ShipType shipType,
            @RequestParam(value = "after", required = false) Long after,
            @RequestParam(value = "before", required = false) Long before,
            @RequestParam(value = "isUsed", required = false) Boolean isUsed,
            @RequestParam(value = "minSpeed", required = false) Double minSpeed,
            @RequestParam(value = "maxSpeed", required = false) Double maxSpeed,
            @RequestParam(value = "minCrewSize", required = false) Integer minCrewSize,
            @RequestParam(value = "maxCrewSize", required = false) Integer maxCrewSize,
            @RequestParam(value = "minRating", required = false) Double minRating,
            @RequestParam(value = "maxRating", required = false) Double maxRating
    ) {
        return shipService.getShips(name, planet, shipType, after, before, isUsed, minSpeed, maxSpeed,
                minCrewSize, maxCrewSize, minRating, maxRating).size();
    }
}
