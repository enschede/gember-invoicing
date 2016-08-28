package app.domain.invoice.internal.countries;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class CountryRepository {

    private Set<Country> countries = new HashSet<>();

    public CountryRepository() {

        countries = Arrays.stream(EuCountry.values())
                .map(euCountry -> new Country(euCountry.name(), euCountry.getEnglishName(), true))
                .collect(Collectors.toSet());
    }

    public Optional<Country> getByIsoCode(String isoCode) {
        return countries
                .stream()
                .filter(country -> country.getIsoCode().equals(isoCode))
                .findFirst();
    }

}
