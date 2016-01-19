package app.domain.invoice;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class VatRepository {

    private List<VatPercentage> percentages;

    /**
     * This static repository contains all Dutch VAT percentages since Oct. 10th, 1986.
     */
    public VatRepository() {
        percentages = new ArrayList<>();
        percentages.add(new VatPercentage(VatTariff.ZERO, LocalDate.of(1986, 10, 1), null, new BigDecimal("0.00")));
        percentages.add(new VatPercentage(VatTariff.LOW,  LocalDate.of(1986, 10, 1), null, new BigDecimal("6.00")));
        percentages.add(new VatPercentage(VatTariff.HIGH, LocalDate.of(1986, 10, 1), LocalDate.of(1988, 12, 31), new BigDecimal("20.00")));
        percentages.add(new VatPercentage(VatTariff.HIGH, LocalDate.of(1989,  1, 1), LocalDate.of(1992,  9, 30), new BigDecimal("18.50")));
        percentages.add(new VatPercentage(VatTariff.HIGH, LocalDate.of(1992, 10, 1), LocalDate.of(2000, 12, 31), new BigDecimal("17.50")));
        percentages.add(new VatPercentage(VatTariff.HIGH, LocalDate.of(2001,  1, 1), LocalDate.of(2012,  9, 30), new BigDecimal("19.00")));
        percentages.add(new VatPercentage(VatTariff.HIGH, LocalDate.of(2012, 10, 1), null, new BigDecimal("21.00")));
    }

    public Optional<VatPercentage> findByTariffAndDate(VatTariff vatTariff, LocalDate referenceDate) {
        return percentages.stream()
                .filter(vatPercentage -> vatPercentage.vatTariff == vatTariff && vatPercentage.startDate.compareTo(referenceDate) <= 0
                        && (vatPercentage.endDate==null || vatPercentage.endDate.compareTo(referenceDate) >= 0))
                .findFirst();
    }


}
