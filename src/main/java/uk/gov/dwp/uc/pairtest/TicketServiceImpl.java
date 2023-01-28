package uk.gov.dwp.uc.pairtest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.seatbooking.SeatReservationService;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;
import uk.gov.dwp.uc.pairtest.utils.TicketCalculateUtils;
import uk.gov.dwp.uc.pairtest.utils.TicketCheckUtils;

public class TicketServiceImpl implements TicketService {
    /**
     * Should only have private methods other than the one below.
     */

    private static final Logger LOG = LoggerFactory.getLogger(TicketServiceImpl.class.getName());

    private final TicketPaymentService ticketPaymentService;
    private final SeatReservationService seatReservationService;

    public TicketServiceImpl(final TicketPaymentService ticketPaymentService,
                             final SeatReservationService seatReservationService) {
        this.ticketPaymentService = ticketPaymentService;
        this.seatReservationService = seatReservationService;
    }

    /**
     * Purchasing ticket(s) with the parameters below.
     * @param accountId - Account ID of whom purchasing the ticket(s)
     * @param ticketTypeRequests ticket(s) requested {@link TicketTypeRequest}
     * @throws InvalidPurchaseException when ticket check failed {@link TicketCheckUtils}
     */
    @Override
    public void purchaseTickets(final Long accountId, final TicketTypeRequest... ticketTypeRequests)
            throws InvalidPurchaseException {
        String error = TicketCheckUtils.check(accountId, ticketTypeRequests);
        if (error.isEmpty()) {
            final int totalAmountToPay = TicketCalculateUtils.calculateTicketTotalPrice(ticketTypeRequests);
            final int totalSeatsToAllocate = TicketCalculateUtils.calculateTotalNumberOfSeats(ticketTypeRequests);
            ticketPaymentService.makePayment(accountId, totalAmountToPay);
            seatReservationService.reserveSeat(accountId, totalSeatsToAllocate);
            LOG.info("Ticket(s) purchased successfully, total price: "
                    + totalAmountToPay + " ,number of seats: " + totalSeatsToAllocate);
        } else {
            LOG.error(error);
            throw new InvalidPurchaseException(error);

        }
    }

}
