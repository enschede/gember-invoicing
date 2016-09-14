package app.domain.invoice.internal.vatCalculationDelegate.impl;

import app.domain.invoice.internal.*;

public class VatCalculationForB2CCustomersDelegate extends VatCalculationIncludingVatDelegate {

    public VatCalculationForB2CCustomersDelegate(InvoiceImpl invoice) {
        super(invoice);
    }

    @Override
    public String getVatDeclarationCountryIso(final String originCountryIso, final String destinationCountryIso) {

        if (invoice.getCompany().getVatRegistrations().containsKey(destinationCountryIso)) {
            return destinationCountryIso;
        }

        return originCountryIso;
    }


}
