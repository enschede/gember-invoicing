package app.domain.invoice;

import app.domain.AggregateRootBase;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by marc on 15/01/16.
 */
public class Invoice extends AggregateRootBase {

    Boolean includingVatInvoice;
    private Debtor debtor;
    private List<InvoiceLine> invoiceLines = new ArrayList<>();

    public Boolean getIncludingVatInvoice() {
        return includingVatInvoice;
    }

    public void setIncludingVatInvoice(Boolean includingVatInvoice) {
        this.includingVatInvoice = includingVatInvoice;
    }

    public Debtor getDebtor() {
        return debtor;
    }

    public void setDebtor(Debtor debtor) {
        this.debtor = debtor;
    }

    public List<InvoiceLine> getInvoiceLines() {
        return invoiceLines;
    }

    public void setInvoiceLines(List<InvoiceLine> invoiceLines) {
        this.invoiceLines = invoiceLines;
    }

    // --- Virtual data ---

    public BigDecimal getInvoiceTotalInclVat() {
        if (includingVatInvoice.booleanValue()) {
            return sumLineTotalsInclVat();
        } else {
            return sumLineTotalsExclVat().add(getInvoiceTotalVat());
        }
    }

    public BigDecimal getInvoiceTotalExclVat() {
        if (!includingVatInvoice.booleanValue()) {
            return sumLineTotalsExclVat();
        } else {
            return sumLineTotalsInclVat().subtract(getInvoiceTotalVat());
        }
    }

    private BigDecimal sumLineTotalsInclVat() {
        return invoiceLines.stream()
                .map(invoiceLine -> invoiceLine.getLineAmountInclVat())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal sumLineTotalsExclVat() {
        return invoiceLines.stream()
                .map(invoiceLine -> invoiceLine.getLineAmountExclVat())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getInvoiceTotalVat() {
        return getVatPerVatTariff().values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Map<VatTariff, BigDecimal> getVatPerVatTariff() {
        return Arrays.asList(VatTariff.values())
                .stream()
                .filter(vatTariff -> isNumberOfInvoiceLinesForVatTariffGreaterThanZero(vatTariff))
                .collect(Collectors.groupingBy(
                        vatTariff -> vatTariff,
                        Collectors.reducing(BigDecimal.ZERO, getTotalVatForVatTariff(), BigDecimal::add)
                ));
    }

    private boolean isNumberOfInvoiceLinesForVatTariffGreaterThanZero(VatTariff vatTariff) {
        return invoiceLines.stream()
                .filter(invoiceLine -> invoiceLine.getVatTariff() == vatTariff)
                .count() > 0;
    }

    private Function<VatTariff, BigDecimal> getTotalVatForVatTariff() {
        return (Function<VatTariff, BigDecimal>) vatTariff -> invoiceLines.stream()
                .filter(invoiceLine -> invoiceLine.getVatTariff() == vatTariff)
                .map(invoiceLine1 -> invoiceLine1.getLineAmountVat(includingVatInvoice))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // --- CQRS commands and event handlers ---

    public void createInvoice(CreateInvoiceCommand createInvoiceCommand) {
        this.debtor = createInvoiceCommand.getDebtor();
    }

    public void addInvoiceLines(AddInvoiceLinesCommand addInvoiceLinesCommand) {
        invoiceLines.addAll(Arrays.asList(addInvoiceLinesCommand.getInvoiceLines()));
    }
}
