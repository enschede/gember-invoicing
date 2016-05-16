package app.domain.invoice;

import java.util.Optional;

/**
 * Created by marc on 06/05/16.
 */
public interface Customer {
    Optional<String> getDefaultCountry();

    Optional<String> getEuTaxId();
}
