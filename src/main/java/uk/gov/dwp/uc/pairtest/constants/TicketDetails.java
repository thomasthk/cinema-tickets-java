package uk.gov.dwp.uc.pairtest.constants;

public enum TicketDetails {
    MAX_TICKET_COUNT(20),
    ADULT_TICKET_PRICE(20),
    CHILD_TICKET_PRICE(10),
    INFANT_TICKET_PRICE(0);


    private final int ticketDetails;

    TicketDetails(final int ticketDetails) {
        this.ticketDetails = ticketDetails;
    }

    public int getTicketDetails() {
        return ticketDetails;
    }
}
