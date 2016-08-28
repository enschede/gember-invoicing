package app.domain.invoice;

public class OriginIsNotEuCountryException extends RuntimeException {

    public OriginIsNotEuCountryException(String originCountryIso) {
        super(originCountryIso);
    }
}
