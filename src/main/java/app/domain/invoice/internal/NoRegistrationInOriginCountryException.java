package app.domain.invoice.internal;

public class NoRegistrationInOriginCountryException extends RuntimeException {

    public NoRegistrationInOriginCountryException(String originCountryOfDefault) {
        super(originCountryOfDefault);
    }
}
