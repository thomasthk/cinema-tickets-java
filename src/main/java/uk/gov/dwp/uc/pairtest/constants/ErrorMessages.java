package uk.gov.dwp.uc.pairtest.constants;

public enum ErrorMessages {

    ACCOUNT_ID_INVALID("Invalid Account ID, it must be greater than 0."),
    TICKET_COUNT_LESS_THAN_MIN("Number of tickets less than the minimal limit."),
    TICKET_COUNT_EXCEED_MAX("Number of tickets exceeded the maximum limit."),
    TICKET_WITHOUT_ADULT("Ticket purchase without adult.");


    private final String errorMessage;

    ErrorMessages(final String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
