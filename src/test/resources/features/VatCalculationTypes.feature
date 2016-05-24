Feature: Consumenten factuur

  Background:
    Given An invoiceline worth "100.00" euro "incl" VAT with "High" vat level and referencedate is "2016-01-01"
    And An invoiceline worth "100.00" euro "incl" VAT with "High" vat level and referencedate is "2016-01-01"
    And An invoiceline worth "100.00" euro "incl" VAT with "High" vat level and referencedate is "2016-01-01"
    And An invoiceline worth "100.00" euro "incl" VAT with "High" vat level and referencedate is "2016-01-01"
    And An invoiceline worth "100.00" euro "incl" VAT with "High" vat level and referencedate is "2016-01-01"
    And An invoiceline worth "100.00" euro "incl" VAT with "High" vat level and referencedate is "2016-01-01"

  Scenario: levering zonder extra registratie in DE
    Given A company with VAT id "NL0123456789B01" in "NL" and vat calculation policy is "VAT_CALCULATION_ON_TOTAL"
    And A customer without a validated VAT id
    And Country of origin is "NL"
    And Country of destination is "NL"
    When A "consumer" invoice with vat shifted equals "false" is created at "2016-01-01"
    Then The total amount including VAT is "600.00"
    And The total amount excluding VAT is "495.87"
    And The total amount VAT is "104.13"
    And The VAT amount for percentage "21.00" is "104.13"

  Scenario: levering zonder extra registratie in DE
    Given A company with VAT id "NL0123456789B01" in "NL" and vat calculation policy is "VAT_CALCULATION_PER_LINE"
    And A customer without a validated VAT id
    And Country of origin is "NL"
    And Country of destination is "NL"
    When A "consumer" invoice with vat shifted equals "false" is created at "2016-01-01"
    Then The total amount including VAT is "600.00"
    And The total amount excluding VAT is "495.84"
    And The total amount VAT is "104.16"
    And The VAT amount for percentage "21.00" is "104.16"




