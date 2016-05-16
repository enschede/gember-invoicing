package app.domain.invoice;

import app.domain.invoice.internal.InvoiceVatRegimeDelegate;
import app.domain.invoice.internal.IsoCountryCode;
import app.domain.invoice.internal.VatAmountSummary;
import app.domain.invoice.internal.VatPercentage;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Created by marc on 06/05/16.
 */
public interface Invoice {
    List<InvoiceLine> getInvoiceLines();

    void setInvoiceLines(List<InvoiceLine> invoiceLines);

    public BigDecimal getInvoiceTotalInclVat();

    public BigDecimal getInvoiceTotalExclVat();

    public BigDecimal getInvoiceTotalVat();

    public Map<VatPercentage, VatAmountSummary> getVatPerVatTariff();

    InvoiceType getInvoiceType();

    void setInvoiceType(InvoiceType invoiceType);

    Optional<IsoCountryCode> getProductOriginCountry();

    void setProductOriginCountry(Optional<IsoCountryCode> productOrigin);

    Optional<IsoCountryCode> getProductDestinationCountry();

    void setProductDestinationCountry(Optional<IsoCountryCode> productDestination);

    boolean isVatShifted();

    void setVatShifted(boolean vatShifted);

    void setCompany(Company company);

    void setCustomer(Customer customer);

    Company getCompany();

    Customer getCustomer();

    InvoiceVatRegimeDelegate.InternationalTaxRuleType getInternationalTaxRuleType();
}
