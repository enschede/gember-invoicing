package app.domain.invoice;

import org.hamcrest.CoreMatchers;
import org.junit.Test;

import java.time.LocalDate;

import static org.junit.Assert.assertThat;

public class VatPercentageTest {

    @Test
    public void shouldFindRightPercentage() {
        VatRepository repository = new VatRepository();

        assertThat(
                repository.findByTariffAndDate(VatTariff.HIGH, LocalDate.of(1985, 12, 31)).isPresent(),
                CoreMatchers.equalTo(false));
        assertThat(
                repository.findByTariffAndDate(VatTariff.HIGH, LocalDate.of(1986, 10,  1)).get().startDate,
                CoreMatchers.equalTo(LocalDate.of(1986, 10, 1)));
        assertThat(
                repository.findByTariffAndDate(VatTariff.HIGH, LocalDate.of(1988, 12, 31)).get().startDate,
                CoreMatchers.equalTo(LocalDate.of(1986, 10, 1)));
        assertThat(
                repository.findByTariffAndDate(VatTariff.HIGH, LocalDate.of(1989,  1,  1)).get().startDate,
                CoreMatchers.equalTo(LocalDate.of(1989,  1, 1)));
        assertThat(
                repository.findByTariffAndDate(VatTariff.HIGH, LocalDate.of(1991,  9, 30)).get().startDate,
                CoreMatchers.equalTo(LocalDate.of(1989,  1, 1)));
        assertThat(
                repository.findByTariffAndDate(VatTariff.HIGH, LocalDate.of(1992, 10,  1)).get().startDate,
                CoreMatchers.equalTo(LocalDate.of(1992, 10, 1)));
        assertThat(
                repository.findByTariffAndDate(VatTariff.HIGH, LocalDate.of(2000, 12, 31)).get().startDate,
                CoreMatchers.equalTo(LocalDate.of(1992, 10, 1)));
        assertThat(
                repository.findByTariffAndDate(VatTariff.HIGH, LocalDate.of(2001,  1,  1)).get().startDate,
                CoreMatchers.equalTo(LocalDate.of(2001,  1, 1)));
        assertThat(
                repository.findByTariffAndDate(VatTariff.HIGH, LocalDate.of(2012,  9, 30)).get().startDate,
                CoreMatchers.equalTo(LocalDate.of(2001,  1, 1)));
        assertThat(
                repository.findByTariffAndDate(VatTariff.HIGH, LocalDate.of(2012, 10,  1)).get().startDate,
                CoreMatchers.equalTo(LocalDate.of(2012, 10, 1)));
        assertThat(
                repository.findByTariffAndDate(VatTariff.HIGH, LocalDate.of(2013,  1,  1)).get().startDate,
                CoreMatchers.equalTo(LocalDate.of(2012, 10, 1)));
    }

}