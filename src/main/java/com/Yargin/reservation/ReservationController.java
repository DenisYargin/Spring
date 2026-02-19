package com.Yargin.reservation;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ReservationController {
    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @GetMapping("/{id}")
    public Reservation getReservationById(@PathVariable Long id) {
        System.out.println("Контроллер вызван!");
        return reservationService.getReservationById(id);

    }

    @GetMapping()
    public List<Reservation> getAllReservation() {
        System.out.println("Контроллер вызван!");
        return reservationService.findAllReservation();

    }


}
