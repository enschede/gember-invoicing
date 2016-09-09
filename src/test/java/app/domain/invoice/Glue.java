package app.domain.invoice;

import app.domain.invoice.internal.InvoiceImpl;
import app.domain.invoice.internal.NoRegistrationInOriginCountryException;
import app.domain.invoice.internal.ProductCategory;
import app.domain.invoice.internal.VatTariff;
import app.domain.invoice.internal.countries.CountryRepository;
import cucumber.api.PendingException;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.hamcrest.Matchers;
import org.junit.Assert;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.BooleanSupplier;

public class Glue {

    private CountryRepository countryRepository;

    private final List<InvoiceLine> invoiceLines = new ArrayList<InvoiceLine>();
    private Optional<String> productOrigin = Optional.empty();
    private Optional<String> productDestination = Optional.empty();
    private Optional<ProductCategory> productCategory = Optional.empty();
    private Optional<Boolean> vatShifted = Optional.empty();

    private Company company;
    private Customer customer;
    private Invoice invoice;

    @Given("^A company in \"([^\"]*)\" with vat calculation policy is \"([^\"]*)\"$")
    public void a_company_with_VAT_id_in_and_vat_calculation_policy_is(final String primaryCountry,
                                                                       final String vatPolicy) throws Throwable {

        company = new Company() {
            private Map<String, String> vatRegistrations = new HashMap<>();

            @Override
            public VatCalculationPolicy getVatCalculationPolicy() {
                return VatCalculationPolicy.valueOf(vatPolicy);
            }

            @Override
            public String getPrimaryCountryIso() {
                return primaryCountry;
            }

            @Override
            public Map<String, String> getVatRegistrations() {
                return vatRegistrations;
            }

            @Override
            public boolean hasVatRegistrationFor(String isoOfcountryOfDestination) {
                return vatRegistrations.containsKey(isoOfcountryOfDestination);
            }
        };

    }

    @Given("^the company has VAT id \"([^\"]*)\" in \"([^\"]*)\"$")
    public void the_company_has_VAT_id_in(String vatId, String companyCountry) throws Throwable {

        company.getVatRegistrations().put(companyCountry, vatId);
    }

    @Given("^A customer without a validated VAT id$")
    public void a_customer_without_a_validated_VAT_id() throws Throwable {

        customer = new Customer() {
            @Override
            public Optional<String> getDefaultCountry() {
                return Optional.empty();
            }

            @Override
            public Optional<String> getEuTaxId() {
                return Optional.empty();
            }
        };

    }

    @Given("^A customer has VAT id \"([^\"]*)\" in \"([^\"]*)\"$")
    public void a_customer_has_VAT_id_in(String vatId, String countryCode) throws Throwable {

        customer = new Customer() {
            @Override
            public Optional<String> getDefaultCountry() {
                return Optional.ofNullable(vatId);
            }

            @Override
            public Optional<String> getEuTaxId() {
                return Optional.of(countryCode);
            }
        };

    }

    @Given("^An invoiceline worth \"([^\"]*)\" euro \"([^\"]*)\" VAT with \"([^\"]*)\" vat level and referencedate is \"([^\"]*)\"$")
    public void an_invoiceline_worth_euro_VAT_with_vat_level_and_referencedate_is(final String lineAmount,
                                                                                  final String inclExclVat,
                                                                                  final String vatTariff,
                                                                                  final String referenceDate) throws Throwable {

        InvoiceLine invoiceLine = new InvoiceLine() {
            @Override
            public BigDecimal getLineAmount() {
                return new BigDecimal(lineAmount);
            }

            @Override
            public InvoiceLineVatType getInvoiceLineVatType() {

                switch (inclExclVat.toUpperCase()) {
                    case "INCL":
                        return InvoiceLineVatType.INCLUDING_VAT;
                    case "EXCL":
                        return InvoiceLineVatType.EXCLUDING_VAT;
                }

                return null;
            }

            @Override
            public LocalDate getVatReferenceDate() {
                return LocalDate.parse(referenceDate, DateTimeFormatter.ISO_DATE);
            }

            @Override
            public VatTariff getVatTariff() {
                return VatTariff.valueOf(vatTariff.toUpperCase());
            }
        };

        invoiceLines.add(invoiceLine);

    }

    @Given("^Country of origin is \"([^\"]*)\"$")
    public void country_of_origin_is(String countryCode) throws Throwable {

        productOrigin = Optional.of(countryCode);
    }

    @Given("^Country of destination is \"([^\"]*)\"$")
    public void country_of_destination_is(String countryCode) throws Throwable {

        productDestination = Optional.of(countryCode);
    }

    @Given("^The product category is \"([^\"]*)\"$")
    public void the_product_category_is(String productCategory) throws Throwable {

        this.productCategory = Optional.of(ProductCategory.valueOf(productCategory));
    }

    @Given("^Vat is shifted$")
    public void vat_is_shifted() throws Throwable {

        this.vatShifted = Optional.of(true);
    }

    @Given("^Vat is not shifted$")
    public void vat_is_not_shifted() throws Throwable {

        this.vatShifted = Optional.of(false);
    }

    @When("^A \"([^\"]*)\" invoice is created at \"([^\"]*)\"$")
    public void a_invoice_is_created_at(String invoiceTypeVal, String invoiceDate) throws Throwable {
        InvoiceType invoiceType = InvoiceType.valueOf(invoiceTypeVal.toUpperCase());

        Invoice invoice = new InvoiceImpl();
        invoice.setCompany(company);
        invoice.setCustomer(customer);
        invoice.setInvoiceType(invoiceType);
        invoice.setProductOriginCountry(productOrigin);
        invoice.setProductDestinationCountry(productDestination);
        invoice.setProductCategory(productCategory);
        invoice.setVatShifted(vatShifted.orElse(false));
        invoice.setInvoiceLines(invoiceLines);

        invoiceLines.forEach(invoiceLine -> invoiceLine.setInvoiceImpl((InvoiceImpl)invoice));

        this.invoice = invoice;
    }

    @Then("^The total amount including VAT is \"([^\"]*)\"$")
    public void the_total_amount_including_VAT_is(String expectedTotalAmountIncludingVat) throws Throwable {
        assert invoice != null;

        BigDecimal invoiceTotalInclVat = invoice.getTotalInvoiceAmountInclVat();

        Assert.assertThat(invoiceTotalInclVat, Matchers.is(new BigDecimal(expectedTotalAmountIncludingVat)));
    }

    @Then("^The total amount excluding VAT is \"([^\"]*)\"$")
    public void the_total_amount_excluding_VAT_is(String expectedTotalAmountExclVat) throws Throwable {
        assert invoice != null;

        Assert.assertThat(invoice.getTotalInvoiceAmountExclVat(), Matchers.is(new BigDecimal(expectedTotalAmountExclVat)));
    }

    @Then("^The total amount VAT is \"([^\"]*)\"$")
    public void the_total_amount_VAT_is(String expectedTotalAmountVat) throws Throwable {
        assert invoice != null;

        BigDecimal invoiceTotalVat = invoice.getInvoiceTotalVat();

        Assert.assertThat(invoiceTotalVat, Matchers.is(new BigDecimal(expectedTotalAmountVat)));
    }

    @Then("^The VAT amount for percentage \"([^\"]*)\" is \"([^\"]*)\"$")
    public void the_VAT_amount_for_percentage_is(String percentage, String expectedAmount) throws Throwable {
        assert invoice != null;

        Optional<BigDecimal> actualAmount =
                invoice.getVatPerVatTariff().entrySet().stream()
                        .filter(entry -> entry.getKey().getPercentage().equals(new BigDecimal(percentage)))
                        .map(entry -> entry.getValue().getAmountVat())
                        .findFirst();

        Assert.assertThat(actualAmount.isPresent(), Matchers.is(true));
        Assert.assertThat(actualAmount.get(), Matchers.is(new BigDecimal(expectedAmount)));
    }

    @Then("^The total amount including VAT request throws an no registration in origin country exception$")
    public void the_total_amount_including_VAT_request_throws_an_no_registration_in_origin_country_exception() throws Throwable {

        try {
            invoice.getTotalInvoiceAmountInclVat();
        } catch (NoRegistrationInOriginCountryException nre) {
            return;
        }

        Assert.fail();
    }

    @Then("^The total amount excluding VAT request throws an no registration in origin country exception$")
    public void the_total_amount_excluding_VAT_request_throws_an_no_registration_in_origin_country_exception() throws Throwable {

        try {
            invoice.getInvoiceSubTotalExclVat();
        } catch (NoRegistrationInOriginCountryException nre) {
            return;
        }

        Assert.fail();
    }

    @Then("^The total amount VAT request throws an no registration in origin country exception$")
    public void the_total_amount_VAT_request_throws_an_no_registration_in_origin_country_exception() throws Throwable {

        try {
            invoice.getInvoiceTotalVat();
        } catch (NoRegistrationInOriginCountryException nre) {
            return;
        }

        Assert.fail();
    }

    @Then("^The total amount including VAT request throws an product category not set exception$")
    public void the_total_amount_including_VAT_request_throws_an_product_category_not_set_exception() throws Throwable {

        try {
            invoice.getTotalInvoiceAmountInclVat();
        } catch (ProductCategoryNotSetException nre) {
            return;
        }

        Assert.fail();
    }

    @Then("^The total amount excluding VAT request throws an product category not set exception$")
    public void the_total_amount_excluding_VAT_request_throws_an_product_category_not_set_exception() throws Throwable {

        try {
            invoice.getTotalInvoiceAmountExclVat();
        } catch (ProductCategoryNotSetException nre) {
            return;
        }

        Assert.fail();
    }

    @Then("^The total amount VAT request throws an product category not set exception$")
    public void the_total_amount_VAT_request_throws_an_product_category_not_set_exception() throws Throwable {
        try {
            invoice.getInvoiceTotalVat();
        } catch (ProductCategoryNotSetException nre) {
            return;
        }

        Assert.fail();
    }
    @Then("^The total amount including VAT request throws an origin is not EU country exception$")
    public void the_total_amount_including_VAT_request_throws_an_origin_is_not_EU_country_exception() throws Throwable {

        try {
            invoice.getTotalInvoiceAmountInclVat();
        } catch (OriginIsNotEuCountryException oe) {
            return;
        }

        Assert.fail();
    }

    @Then("^The total amount excluding VAT request throws an origin is not EU country exception$")
    public void the_total_amount_excluding_VAT_request_throws_an_origin_is_not_EU_country_exception() throws Throwable {

        try {
            invoice.getInvoiceSubTotalExclVat();
        } catch (OriginIsNotEuCountryException oe) {
            return;
        }

        Assert.fail();
    }

    @Then("^The total amount VAT request throws an origin is not EU country exception$")
    public void the_total_amount_VAT_request_throws_an_origin_is_not_EU_country_exception() throws Throwable {

        try {
            invoice.getInvoiceTotalVat();
        } catch (OriginIsNotEuCountryException oe) {
            return;
        }

        Assert.fail();
    }

    @Then("^The vat shifted indicator is \"([^\"]*)\"$")
    public void the_vat_shifted_indicator_is(String shifted_indicator) throws Throwable {

        Assert.assertThat(invoice.isShiftedVat(), Matchers.is(Boolean.valueOf(shifted_indicator)));
    }

    @Then("^The VAT amount for percentage \"([^\"]*)\" is not available$")
    public void the_VAT_amount_for_percentage_is_not_available(String percentage) throws Throwable {

        assert invoice != null;

        Optional<BigDecimal> actualAmount =
                invoice.getVatPerVatTariff().entrySet().stream()
                        .filter(entry -> entry.getKey().getPercentage().equals(new BigDecimal(percentage)))
                        .map(entry -> entry.getValue().getAmountVat())
                        .findFirst();

        Assert.assertThat(actualAmount.isPresent(), Matchers.is(false));
    }

}
