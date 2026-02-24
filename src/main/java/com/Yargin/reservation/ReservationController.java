package com.Yargin.reservation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ReservationController {
    private final ReservationService reservationService;

    private static final Logger log = LoggerFactory.getLogger(ReservationController.class);

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @GetMapping("/{id}")
    public Reservation getReservationById(@PathVariable Long id) {
        log.info("Вызвался метод getReservationById id=" + id);
        return reservationService.getReservationById(id);

    }

    @GetMapping()
    public List<Reservation> getAllReservation() {
        log.info("Вызвался метод getAllReservation");
        return reservationService.findAllReservation();

    }


}
