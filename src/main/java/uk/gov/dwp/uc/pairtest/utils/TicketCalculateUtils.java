package uk.gov.dwp.uc.pairtest.utils;

import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;

import java.util.Arrays;

public final class TicketCalculateUtils {

    /**
     * @throws UnsupportedOperationException a utility class cannot be instantiated
     */
    private TicketCalculateUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /**
     * @param ticketTypeRequests ticket(s) requested {@link TicketTypeRequest}
     * @return total price of ticket(s)
     */
    public static int calculateTicketTotalPrice(final TicketTypeRequest... ticketTypeRequests) {
        return Arrays.stream(ticketTypeRequests)
                .map(ticketTypeRequest ->
                        ticketTypeRequest.getTicketType().getTicketPrice() * ticketTypeRequest.getNoOfTickets())
                .mapToInt(value -> value).sum();
    }

    /**
     * @param ticketTypeRequests ticket(s) requested {@link TicketTypeRequest}
     * @return total number of seats of requested
     * (Infants do not pay for a ticket and are not allocated a seat. They will be sitting on an Adult's lap.)
     */
    public static int calculateTotalNumberOfSeats(final TicketTypeRequest... ticketTypeRequests) {
        return Arrays.stream(ticketTypeRequests)
                .filter(type -> type.getTicketType().equals(TicketTypeRequest.Type.CHILD)
                        || type.getTicketType().equals(TicketTypeRequest.Type.ADULT))
                .map(TicketTypeRequest::getNoOfTickets)
                .mapToInt(value -> value).sum();

    }
}
