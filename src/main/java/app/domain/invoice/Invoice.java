package app.domain.invoice;

import app.domain.invoice.internal.*;

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

    public BigDecimal getInvoiceTotalInclVat() throws OriginIsNotEuCountryException, ProductCategoryNotSetException;

    public BigDecimal getInvoiceTotalExclVat() throws OriginIsNotEuCountryException, ProductCategoryNotSetException;

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
}
