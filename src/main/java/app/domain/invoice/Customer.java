package app.domain.invoice;

import java.util.Optional;

public interface Customer {
    Optional<String> getDefaultCountry();

    Optional<String> getEuTaxId();
}
