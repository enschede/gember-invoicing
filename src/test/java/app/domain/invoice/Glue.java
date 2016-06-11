package app.domain.invoice;

import app.domain.invoice.internal.InvoiceImpl;
import app.domain.invoice.internal.IsoCountryCode;
import app.domain.invoice.internal.VatTariff;
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

public class Glue {

    private final List<InvoiceLine> invoiceLines = new ArrayList<InvoiceLine>();
    private Optional<IsoCountryCode> productOrigin = Optional.empty();
    private Optional<IsoCountryCode> productDestination = Optional.empty();

    private Company company;
    private Customer customer;
    private Invoice invoice;

    @Given("^A company in \"([^\"]*)\" with vat calculation policy is \"([^\"]*)\"$")
    public void a_company_with_VAT_id_in_and_vat_calculation_policy_is(final String companyCountry,
                                                                       final String vatPolicy) throws Throwable {

        company = new Company() {
            private Map<IsoCountryCode, String> vatRegistrations = new HashMap<>();

            @Override
            public VatCalculationPolicy getVatCalculationPolicy() {
                return VatCalculationPolicy.valueOf(vatPolicy);
            }

            @Override
            public IsoCountryCode getDefaultVatCountry() {
                return new IsoCountryCode(companyCountry);
            }

            @Override
            public Map<IsoCountryCode, String> getVatRegistrations() {
                return vatRegistrations;
            }

            @Override
            public boolean hasVatRegistrationFor(IsoCountryCode countryOfDestination) {
                return vatRegistrations.containsKey(countryOfDestination);
            }
        };

    }

    @Given("^the company has VAT id \"([^\"]*)\" in \"([^\"]*)\"$")
    public void the_company_has_VAT_id_in(String vatId, String companyCountry) throws Throwable {

        if(company==null)
            throw new PendingException();

        company.getVatRegistrations().put(new IsoCountryCode(companyCountry), vatId);
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

        productOrigin = Optional.of(new IsoCountryCode(countryCode));
    }

    @Given("^Country of destination is \"([^\"]*)\"$")
    public void country_of_destination_is(String countryCode) throws Throwable {

        productDestination = Optional.of(new IsoCountryCode(countryCode));
    }

    @When("^A \"([^\"]*)\" invoice with vat shifted equals \"([^\"]*)\" is created at \"([^\"]*)\"$")
    public void a_invoice_with_vat_shifted_equals_is_created_at(String invoiceTypeVal, String vatShifted, String invoiceDate) throws Throwable {

        InvoiceType invoiceType = InvoiceType.valueOf(invoiceTypeVal.toUpperCase());

        Invoice invoice = new InvoiceImpl();
        invoice.setCompany(company);
        invoice.setCustomer(customer);
        invoice.setInvoiceType(invoiceType);
        invoice.setProductOriginCountry(productOrigin);
        invoice.setProductDestinationCountry(productDestination);
        invoice.setVatShifted(Boolean.valueOf(vatShifted.toUpperCase()));
        invoice.setInvoiceLines(invoiceLines);

        invoiceLines.forEach(invoiceLine -> invoiceLine.setInvoiceImpl((InvoiceImpl)invoice));

        this.invoice = invoice;
    }

    @Then("^The total amount including VAT is \"([^\"]*)\"$")
    public void the_total_amount_including_VAT_is(String expectedTotalAmountIncludingVat) throws Throwable {
        assert invoice != null;

        Assert.assertThat(invoice.getInvoiceTotalInclVat(), Matchers.is(new BigDecimal(expectedTotalAmountIncludingVat)));
    }

    @Then("^The total amount excluding VAT is \"([^\"]*)\"$")
    public void the_total_amount_excluding_VAT_is(String expectedTotalAmountExclVat) throws Throwable {
        assert invoice != null;

        Assert.assertThat(invoice.getInvoiceTotalExclVat(), Matchers.is(new BigDecimal(expectedTotalAmountExclVat)));
    }

    @Then("^The total amount VAT is \"([^\"]*)\"$")
    public void the_total_amount_VAT_is(String expectedTotalAmountVat) throws Throwable {
        assert invoice != null;

        Assert.assertThat(invoice.getInvoiceTotalVat(), Matchers.is(new BigDecimal(expectedTotalAmountVat)));
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

    @Then("^The total amount including VAT request throws an invoice calculation exception$")
    public void the_total_amount_including_VAT_request_throws_an_invoice_calculation_exception() throws Throwable {

        try {
            invoice.getInvoiceTotalInclVat();
        } catch (InvoiceCalculationException e) {
            return;
        }

        Assert.fail();
    }

    @Then("^The total amount excluding VAT request throws an invoice calculation exception$")
    public void the_total_amount_excluding_VAT_request_throws_an_invoice_calculation_exception() throws Throwable {
        try {
            invoice.getInvoiceTotalInclVat();
        } catch (InvoiceCalculationException e) {
            return;
        }

        Assert.fail();
    }

    @Then("^The total amount VAT request throws an invoice calculation exception$")
    public void the_total_amount_VAT_request_throws_an_invoice_calculation_exception() throws Throwable {
        try {
            invoice.getInvoiceTotalInclVat();
        } catch (InvoiceCalculationException e) {
            return;
        }

        Assert.fail();
    }

}
