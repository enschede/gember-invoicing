Feature: An invoice is created for a private customer

  Background:
    Given A company that resides "NL"

  Scenario: A company sends an invoice to a private customer in the same country
    Given An invoiceline worth "121.00" euro incl VAT with "High" vat level and referencedate is "2016-01-01"
    And The customer is private and resident in "NL"
    And The delivery is from "NL" to "NL"
    When An invoice is created at "2016-01-01"
    Then The total amount including VAT is "121.00"
    And The total amount excluding VAT is "100.00"
    And The total amount VAT is "21.00"
    And The VAT amount for percentage "21.00" is "21.00"

  Scenario: A company sends an invoice to a private customer in another EU country
    Given An invoiceline worth "121.00" euro incl VAT with "High" vat level and referencedate is "2016-01-01"
    And The customer is private and resident in "DE"
    And The delivery is from "NL" to "NL"
    When An invoice is created at "2016-01-01"
    Then The total amount including VAT is "121.00"
    And The total amount excluding VAT is "100.00"
    And The total amount VAT is "21.00"
    And The VAT amount for percentage "21.00" is "21.00"

  Scenario: A small company sends an invoice to a private customer in another EU country
    Given An invoiceline worth "121.00" euro incl VAT with "High" vat level and referencedate is "2016-01-01"
    And The customer is private and resident in "DE"
    And The delivery is from "NL" to "NL"
    When An invoice is created at "2016-01-01"
    Then The total amount including VAT is "121.00"
    And The total amount excluding VAT is "100.00"
    And The total amount VAT is "21.00"
    And The VAT amount for percentage "21.00" is "21.00"

  Scenario: A company sends an invoice to a private customer outside the EU zone
    Given An invoiceline worth "121.00" euro incl VAT with "High" vat level and referencedate is "2016-01-01"
    And The customer is private and resident in "TR"
    And The delivery is from "NL" to "NL"
    When An invoice is created at "2016-01-01"
    Then The total amount including VAT is "121.00"
    And The total amount excluding VAT is "100.00"
    And The total amount VAT is "21.00"
    And The VAT amount for percentage "21.00" is "21.00"

  Scenario: A company sends an invoice to a private customer in the same country
    Given An invoiceline worth "121.00" euro incl VAT with "High" vat level and referencedate is "2016-01-01"
    And An invoiceline worth "106.00" euro incl VAT with "Low1" vat level and referencedate is "2016-01-01"
#    And An invoiceline worth "100.00" euro incl VAT with "Zero" vat level and referencedate is "2016-01-01"
    And The customer is private and resident in "NL"
    And The delivery is from "NL" to "NL"
    When An invoice is created at "2016-01-01"
    Then The total amount including VAT is "227.00"
    And The total amount excluding VAT is "200.00"
    And The total amount VAT is "27.00"
    And The VAT amount for percentage "21.00" is "21.00"
    And The VAT amount for percentage "6.00" is "6.00"

