package com.Yargin.reservation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/reservation")
public class ReservationController {
    private final ReservationService reservationService;

    private static final Logger log = LoggerFactory.getLogger(ReservationController.class);

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Reservation> getReservationById(@PathVariable Long id) {
        log.info("Вызвался метод getReservationById id=" + id);
        try {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(reservationService.getReservationById(id));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(404).build();
        }
    }

    @GetMapping()
    public ResponseEntity<List<Reservation>> getAllReservation() {
        log.info("Вызвался метод getAllReservation");
        return ResponseEntity.ok(reservationService.findAllReservation());
      //  return reservationService.findAllReservation();

    }

    @PostMapping()
    public ResponseEntity<Reservation> createReservation(
           @RequestBody Reservation reservationToCreate
    ) {
        log.info("Вызвался метод createReservation");
        return ResponseEntity.status(HttpStatus.CREATED)
                .header("test-header", "value-header")
                .body(reservationService.createReservation(reservationToCreate));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Reservation> updateReservation(
            @PathVariable Long id,
            @RequestBody Reservation reservationToUpdate
    ) {
        log.info("Вызвался метод updateReservation id={}, reservationToUpdate={}",id, reservationToUpdate);
        var updated = reservationService.updateReservation(id, reservationToUpdate);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReservation(
            @PathVariable("id") Long id
    ) {
        log.info("Вызвался метод deleteReservation id={}", id);
        try{
            reservationService.deleteReservation(id);
            return ResponseEntity.ok()
                    .build();
        } catch (NoSuchElementException e){
            return ResponseEntity.status(404).build();
        }
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<Reservation> approveReservation(
            @PathVariable("id") Long id
    ) {
        log.info("Вызвался метод approveReservation id={}", id);
        var reservation = reservationService.approveReservation(id);
        return ResponseEntity.ok(reservation);
    }

}
