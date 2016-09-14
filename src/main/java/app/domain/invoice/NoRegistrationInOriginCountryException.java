package app.domain.invoice;

public class NoRegistrationInOriginCountryException extends RuntimeException {

    public NoRegistrationInOriginCountryException(String originCountryOfDefault) {
        super(originCountryOfDefault);
    }
}
