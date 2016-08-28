Feature: As a salesman I want to send an invoice for goods or services delivered from my primary or a secondary EU country to a private customer in my primary country, a secondary EU country or a country outside the EU.

  - A private customer is defined as a customer without a valid EU VAT id.

  Background:
    Given A company in "NL" with vat calculation policy is "VAT_CALCULATION_ON_TOTAL"
    And the company has VAT id "NL0123456789B01" in "NL"
    Given An invoiceline worth "121.00" euro "incl" VAT with "High" vat level and referencedate is "2016-01-01"
    And An invoiceline worth "106.00" euro "incl" VAT with "Low1" vat level and referencedate is "2016-01-01"
    And An invoiceline worth "100.00" euro "incl" VAT with "Zero" vat level and referencedate is "2016-01-01"
    Given A customer without a validated VAT id

  # A1.1.1
  Scenario Outline: A company with registration in the main country delivers goods/services within the same country
    Given Country of origin is "<origin>"
    And Country of destination is "<destination>"
    When A "consumer" invoice is created at "2016-01-01"
    Then The total amount including VAT is "<totalAmountInclVat>"
    And The total amount excluding VAT is "<totalAmountExVat>"
    And The total amount VAT is "<totalAmountVat>"
    And The VAT amount for percentage "<vatPercentage>" is "<amountVat>"

    Examples:
      | origin | destination | totalAmountInclVat | totalAmountExVat | totalAmountVat | vatPercentage | amountVat |
      | NL     | NL          | 327.00             | 300.00           | 27.00          | 21.00         | 21.00     |
      | NL     | NL          | 327.00             | 300.00           | 27.00          | 6.00          | 6.00      |
      | NL     | NL          | 327.00             | 300.00           | 27.00          | 0.00          | 0.00      |

  # A1.2
  Scenario Outline: A company with registration in it's main country delivers goods to another EU country where it has a registration
    Given the company has VAT id "DE12345" in "DE"
    And the company has VAT id "BE12345" in "BE"
    And Country of origin is "<origin>"
    And Country of destination is "<destination>"
    When A "consumer" invoice is created at "2016-01-01"
    Then The total amount including VAT is "<totalAmountInclVat>"
    And The total amount excluding VAT is "<totalAmountExVat>"
    And The total amount VAT is "<totalAmountVat>"
    And The VAT amount for percentage "<vatPercentage>" is "<amountVat>"

    Examples:
      | origin | destination | totalAmountInclVat | totalAmountExVat | totalAmountVat | vatPercentage | amountVat |
      | NL     | DE          | 327.00             | 300.75           | 26.25          | 19.00         | 19.32     |
      | NL     | DE          | 327.00             | 300.75           | 26.25          | 7.00          | 6.93      |
      | NL     | DE          | 327.00             | 300.75           | 26.25          | 0.00          | 0.00      |

  # A1.2
  Scenario Outline: A company with registration in it's main country delivers goods from another EU country to another EU country where it has a registration
    Given the company has VAT id "DE12345" in "DE"
    And the company has VAT id "BE12345" in "BE"
    And Country of origin is "<origin>"
    And Country of destination is "<destination>"
    When A "consumer" invoice is created at "2016-01-01"
    Then The total amount including VAT is "<totalAmountInclVat>"
    And The total amount excluding VAT is "<totalAmountExVat>"
    And The total amount VAT is "<totalAmountVat>"
    And The VAT amount for percentage "<vatPercentage>" is "<amountVat>"

    Examples:
      | origin | destination | totalAmountInclVat | totalAmountExVat | totalAmountVat | vatPercentage | amountVat |
      | BE     | DE          | 327.00             | 300.75           | 26.25          | 19.00         | 19.32     |
      | BE     | DE          | 327.00             | 300.75           | 26.25          | 7.00          | 6.93      |
      | BE     | DE          | 327.00             | 300.75           | 26.25          | 0.00          | 0.00      |

  # A1.3
  Scenario Outline: A company with registration in it's main country delivers goods to another EU country where it has no registration
    Given Country of origin is "<origin>"
    And Country of destination is "<destination>"
    When A "consumer" invoice is created at "2016-01-01"
    Then The total amount including VAT is "<totalAmountInclVat>"
    And The total amount excluding VAT is "<totalAmountExVat>"
    And The total amount VAT is "<totalAmountVat>"
    And The VAT amount for percentage "<vatPercentage>" is "<amountVat>"

    Examples:
      | origin | destination | totalAmountInclVat | totalAmountExVat | totalAmountVat | vatPercentage | amountVat |
      | NL     | DE          | 327.00             | 300.00           | 27.00          | 21.00         | 21.00     |
      | NL     | DE          | 327.00             | 300.00           | 27.00          | 6.00          | 6.00      |
      | NL     | DE          | 327.00             | 300.00           | 27.00          | 0.00          | 0.00      |

  # A1.3
  Scenario Outline: A company with registration in it's main country delivers goods from another EU country to another EU country where it has no registration
    Given the company has VAT id "BE12345" in "BE"
    And Country of origin is "<origin>"
    And Country of destination is "<destination>"
    When A "consumer" invoice is created at "2016-01-01"
    Then The total amount including VAT is "<totalAmountInclVat>"
    And The total amount excluding VAT is "<totalAmountExVat>"
    And The total amount VAT is "<totalAmountVat>"
    And The VAT amount for percentage "<vatPercentage>" is "<amountVat>"

    Examples:
      | origin | destination | totalAmountInclVat | totalAmountExVat | totalAmountVat | vatPercentage | amountVat |
      | BE     | DE          | 327.00             | 301.68           | 25.32          | 19.00         | 19.32     |
      | BE     | DE          | 327.00             | 301.68           | 25.32          | 6.00          | 6.00      |
      | BE     | DE          | 327.00             | 301.68           | 25.32          | 0.00          | 0.00      |

  # A1.4
  Scenario Outline: A company with registration in it's main country export goods from another EU country
    Given Country of origin is "<origin>"
    And Country of destination is "<destination>"
    When A "consumer" invoice is created at "2016-01-01"
    Then The total amount including VAT is "<totalAmountInclVat>"
    And The total amount excluding VAT is "<totalAmountExVat>"
    And The total amount VAT is "<totalAmountVat>"
    And The VAT amount for percentage "<vatPercentage>" is "<amountVat>"

    Examples:
      | origin | destination | totalAmountInclVat | totalAmountExVat | totalAmountVat | vatPercentage | amountVat |
      | NL     | TR          | 327.00             | 300.00           | 27.00          | 21.00         | 21.00     |
      | NL     | TR          | 327.00             | 300.00           | 27.00          | 6.00          | 6.00      |
      | NL     | TR          | 327.00             | 300.00           | 27.00          | 0.00          | 0.00      |

  # A1.4
  Scenario Outline: A company with registration in it's main country export goods
    Given the company has VAT id "BE12345" in "BE"
    And Country of origin is "<origin>"
    And Country of destination is "<destination>"
    When A "consumer" invoice is created at "2016-01-01"
    Then The total amount including VAT is "<totalAmountInclVat>"
    And The total amount excluding VAT is "<totalAmountExVat>"
    And The total amount VAT is "<totalAmountVat>"
    And The VAT amount for percentage "<vatPercentage>" is "<amountVat>"

    Examples:
      | origin | destination | totalAmountInclVat | totalAmountExVat | totalAmountVat | vatPercentage | amountVat |
      | BE     | TR          | 327.00             | 301.68           | 25.32          | 19.00         | 19.32     |
      | BE     | TR          | 327.00             | 301.68           | 25.32          | 6.00          | 6.00      |
      | BE     | TR          | 327.00             | 301.68           | 25.32          | 0.00          | 0.00      |

  # A2.1
  Scenario: A company delevers good from a EU country where it has no registration to another EU country, leading to an exception
    Given Country of origin is "BE"
    And Country of destination is "DE"
    When A "consumer" invoice is created at "2016-01-01"
    Then The total amount including VAT request throws an invoice calculation exception
    Then The total amount excluding VAT request throws an invoice calculation exception
    Then The total amount VAT request throws an invoice calculation exception

  # A2.2
  Scenario: A company exports from a EU country where it has no registration, leading to an exception
    Given Country of origin is "BE"
    And Country of destination is "TR"
    When A "consumer" invoice is created at "2016-01-01"
    Then The total amount including VAT request throws an invoice calculation exception
    Then The total amount excluding VAT request throws an invoice calculation exception
    Then The total amount VAT request throws an invoice calculation exception

  # A3.1
  Scenario Outline: A company with registration in it's main country delivers goods from another EU country to another EU country where it has a registration
    Given the company has VAT id "DE12345" in "DE"
    And Country of origin is "<origin>"
    And Country of destination is "<destination>"
    When A "consumer" invoice is created at "2016-01-01"
    Then The total amount including VAT is "<totalAmountInclVat>"
    And The total amount excluding VAT is "<totalAmountExVat>"
    And The total amount VAT is "<totalAmountVat>"
    And The VAT amount for percentage "<vatPercentage>" is "<amountVat>"

    Examples:
      | origin | destination | totalAmountInclVat | totalAmountExVat | totalAmountVat | vatPercentage | amountVat |
      | DE     | DE          | 327.00             | 300.75           | 26.25          | 19.00         | 19.32     |
      | DE     | DE          | 327.00             | 300.75           | 26.25          | 7.00          | 6.93      |
      | DE     | DE          | 327.00             | 300.75           | 26.25          | 0.00          | 0.00      |

  # A4.1
  #
  Scenario Outline: A company registered in EU delivers goods from outside the EU will result in an exception
    Given the company has VAT id "DE12345" in "DE"
    And Country of origin is "TR"
    And Country of destination is "DE"
    When A "consumer" invoice is created at "2016-01-01"
    Then The total amount including VAT request throws an invoice calculation exception
    Then The total amount excluding VAT request throws an invoice calculation exception
    Then The total amount VAT request throws an invoice calculation exception

    Examples:
      | totalAmountInclVat | totalAmountExVat | totalAmountVat | vatPercentage | amountVat |
      | 327.00             | 300.75           | 26.25          | 19.00         | 19.32     |
      | 327.00             | 300.75           | 26.25          | 7.00          | 6.93      |
      | 327.00             | 300.75           | 26.25          | 0.00          | 0.00      |

