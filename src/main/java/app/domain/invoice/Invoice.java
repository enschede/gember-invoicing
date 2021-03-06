package app.domain.invoice;

import app.domain.invoice.internal.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface Invoice {
    List<InvoiceLine> getInvoiceLines();

    void setInvoiceLines(List<InvoiceLine> invoiceLines);

    public BigDecimal getInvoiceSubTotalInclVat();

    public BigDecimal getInvoiceSubTotalExclVat();

    BigDecimal getTotalInvoiceAmountInclVat();

    BigDecimal getTotalInvoiceAmountExclVat();

    public BigDecimal getInvoiceTotalVat();

    public Map<VatPercentage, VatAmountSummary> getVatPerVatTariff();

    InvoiceType getInvoiceType();

    void setInvoiceType(InvoiceType invoiceType);

    Optional<String> getProductOriginCountry();

    void setProductOriginCountry(Optional<String> productOrigin);

    Optional<String> getProductDestinationCountry();

    void setProductDestinationCountry(Optional<String> productDestination);

    void setCompany(Company company);

    void setCustomer(Customer customer);

    Company getCompany();

    Customer getCustomer();

    VatCalculationRegime getInternationalTaxRuleType();

    void setVatShifted(Boolean aBoolean);

    void setProductCategory(Optional<ProductCategory> productCategory);

    Boolean isShiftedVat();
}
