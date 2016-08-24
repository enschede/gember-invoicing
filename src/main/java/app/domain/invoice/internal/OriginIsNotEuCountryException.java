package app.domain.invoice.internal;

public class OriginIsNotEuCountryException extends Throwable {
    public OriginIsNotEuCountryException(String originCountryIso) {
        super(originCountryIso);
    }
}
