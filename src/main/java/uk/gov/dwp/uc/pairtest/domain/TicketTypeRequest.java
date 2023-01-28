package uk.gov.dwp.uc.pairtest.domain;

import uk.gov.dwp.uc.pairtest.constants.TicketDetails;

/**
 * Immutable Object.
 */

public class TicketTypeRequest {

    private final int noOfTickets;
    private final Type type;

    public TicketTypeRequest(final Type type, final int noOfTickets) {
        this.type = type;
        this.noOfTickets = noOfTickets;
    }

    public int getNoOfTickets() {
        return noOfTickets;
    }

    public Type getTicketType() {
        return type;
    }

    public enum Type {
        ADULT(TicketDetails.ADULT_TICKET_PRICE.getTicketDetails()),
        CHILD(TicketDetails.CHILD_TICKET_PRICE.getTicketDetails()),
        INFANT(TicketDetails.INFANT_TICKET_PRICE.getTicketDetails());
        final int ticketPrice;
        Type(final int ticketPrice) {
            this.ticketPrice = ticketPrice;
        }

        public int getTicketPrice() {
            return ticketPrice;
        }
    }

}
