package app.domain.invoice.internal;

import app.domain.invoice.InvoiceLine;
import app.domain.invoice.InvoiceLineVatType;

import java.math.BigDecimal;

/**
 * Created by marc on 14/06/16.
 */
public class LineVatCalculatorImpl implements LineVatCalculator {

    final VatRepository vatRepository;
    final IsoCountryCode destinationCountry;

    public LineVatCalculatorImpl(VatRepository vatRepository, IsoCountryCode destinationCountry) {
        this.vatRepository = vatRepository;
        this.destinationCountry = destinationCountry;
    }

    @Override
    public BigDecimal getLineAmountInclVat(InvoiceLine invoiceLine) {
        if(invoiceLine.getInvoiceLineVatType()== InvoiceLineVatType.INCLUDING_VAT)
            return invoiceLine.getLineAmount();

        return invoiceLine.getLineAmount().add(getVatAmount(invoiceLine));
    }

    @Override
    public BigDecimal getLineAmountExclVat(InvoiceLine invoiceLine) {
        if(invoiceLine.getInvoiceLineVatType()==InvoiceLineVatType.EXCLUDING_VAT)
            return invoiceLine.getLineAmount();

        return invoiceLine.getLineAmount().subtract(getVatAmount(invoiceLine));
    }

    @Override
    public BigDecimal getVatAmount(InvoiceLine invoiceLine) {

        VatPercentage vatPercentage =
                vatRepository.findByTariffAndDate(
                        destinationCountry,
                        invoiceLine.getVatTariff(),
                        invoiceLine.getVatReferenceDate());

        if(invoiceLine.getInvoiceLineVatType()==InvoiceLineVatType.INCLUDING_VAT) {
            return VatPercentage.getVatAmountFromAmountInclVat(
                    invoiceLine.getLineAmount(),
                    vatPercentage.getPercentage());
        } else {
            return VatPercentage.getVatAmountFromAmountExclVat(
                    invoiceLine.getLineAmount(),
                    vatPercentage.getPercentage());
        }

    }

}
