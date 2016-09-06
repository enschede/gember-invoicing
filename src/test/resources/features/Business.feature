Feature: Delivering to a business customer in the EU
  - A company with registration in the main country delivers goods/services within the same country
  - A company with registration in the main country delivers goods/services within the same country under tax shifted rules
  - A company with registration in the main country delivers goods to a business partner in EU with a tax id
  - A company with registration in the main country delivers goods to a business partner in EU without a tax id
  - A company with registration in the main country delivers goods from a EU country to a business partner in another EU with a tax id
  - A company with registration in the main country delivers goods from a EU country to a business partner in another EU without a tax id
  - A company with registration in the main country delivers services to a business partner in EU without a tax id

  Background:
    Given A company in "NL" with vat calculation policy is "VAT_CALCULATION_ON_TOTAL"
    And the company has VAT id "NL0123456789B01" in "NL"
    Given An invoiceline worth "100.00" euro "excl" VAT with "High" vat level and referencedate is "2016-01-01"
    And An invoiceline worth "100.00" euro "excl" VAT with "Low1" vat level and referencedate is "2016-01-01"
    And An invoiceline worth "100.00" euro "excl" VAT with "Zero" vat level and referencedate is "2016-01-01"

  # B1.1.1
  # Intern in primaire land
  Scenario Outline: A company with registration in the main country delivers goods/services within the same country
    Delivering goods without passing an internal border always requires to calculate VAT

    Given A customer has VAT id "NL56789" in "NL"
    And Country of origin is "<origin>"
    And Country of destination is "<destination>"
    When A "business" invoice is created at "2016-01-01"
    Then The total amount including VAT is "<totalAmountInclVat>"
    And The total amount excluding VAT is "<totalAmountExVat>"
    And The total amount VAT is "<totalAmountVat>"
    And The VAT amount for percentage "<vatPercentage>" is "<amountVat>"
    And The vat shifted indicator is "false"

    Examples:
      | origin | destination | totalAmountInclVat | totalAmountExVat | totalAmountVat | vatPercentage | amountVat |
      | NL     | NL          | 327.00             | 300.00           | 27.00          | 21.00         | 21.00     |
      | NL     | NL          | 327.00             | 300.00           | 27.00          | 6.00          | 6.00      |
      | NL     | NL          | 327.00             | 300.00           | 27.00          | 0.00          | 0.00      |

  #B1.1.2
  # Intern in primaire land, met BTW verlegd
  Scenario Outline: A company with registration in the main country delivers goods/services within the same country under tax shifted rules
    Delivering goods without passing an internal border always requires to calculate VAT

    Given A customer has VAT id "NL56789" in "NL"
    And Country of origin is "<origin>"
    And Country of destination is "<destination>"
    And Vat is shifted
    When A "business" invoice is created at "2016-01-01"
    Then The total amount including VAT is "<totalAmountInclVat>"
    And The total amount excluding VAT is "<totalAmountExVat>"
    And The total amount VAT is "<totalAmountVat>"
    And The VAT amount for percentage "21.00" is not available
    And The VAT amount for percentage "6.00" is not available
    And The VAT amount for percentage "0.00" is not available
    And The vat shifted indicator is "true"

    Examples:
      | origin | destination | totalAmountInclVat | totalAmountExVat | totalAmountVat |
      | NL     | NL          | 300.00             | 300.00           | 0.00           |

  #B1.1.3
  # Intern in primaire land, met marge artikelen


  # B2.1
  # Goederen vanuit primaire of secundare land, naar 3e EU land (BTW 0%)
  Scenario Outline: A company with registration in the main country delivers goods to a business partner in EU with a tax id
    Given the company has VAT id "BE12345" in "BE"
    And A customer has VAT id "DE56789" in "DE"
    And Country of origin is "<origin>"
    And Country of destination is "<destination>"
    And The product category is "Goods"
    When A "business" invoice is created at "2016-01-01"
    Then The total amount including VAT is "<totalAmountInclVat>"
    And The total amount excluding VAT is "<totalAmountExVat>"
    And The total amount VAT is "<totalAmountVat>"
    And The VAT amount for percentage "<vatPercentage>" is "<amountVat>"
    And The vat shifted indicator is "false"

    Examples:
      | origin | destination | totalAmountInclVat | totalAmountExVat | totalAmountVat | vatPercentage | amountVat |
      | NL     | DE          | 300.00             | 300.00           | 0.00           | 0.00          | 0.00      |
      | BE     | DE          | 300.00             | 300.00           | 0.00           | 0.00          | 0.00      |

  # B2.2.2
  Scenario Outline: A company with registration in the main country delivers services to a business partner in EU with a tax id
    Given the company has VAT id "BE12345" in "BE"
    And A customer has VAT id "DE56789" in "DE"
    And Country of origin is "<origin>"
    And Country of destination is "<destination>"
    And The product category is "Services"
    And Vat is shifted
    When A "business" invoice is created at "2016-01-01"
    Then The total amount including VAT is "<totalAmountInclVat>"
    And The total amount excluding VAT is "<totalAmountExVat>"
    And The total amount VAT is "0.00"
    And The VAT amount for percentage "<0.00>" is not available
    And The vat shifted indicator is "true"

    Examples:
      | origin | destination | totalAmountInclVat | totalAmountExVat |
      | NL     | DE          | 300.00             | 300.00           |
      | BE     | DE          | 300.00             | 300.00           |

  # B2.2.2
  #  Waar BTW heffen als geen BTW verlegd toegepast mag worden?
  Scenario Outline: A company with registration in the main country delivers services to a business partner in EU with a tax id
    Given the company has VAT id "BE12345" in "BE"
    And A customer has VAT id "DE56789" in "DE"
    And Country of origin is "<origin>"
    And Country of destination is "<destination>"
    And The product category is "Services"
    And Vat is not shifted
    When A "business" invoice is created at "2016-01-01"
    Then The total amount including VAT is "<totalAmountInclVat>"
    And The total amount excluding VAT is "<totalAmountExVat>"
    And The total amount VAT is "0.00"
    And The VAT amount for percentage "<0.00>" is not available
    And The vat shifted indicator is "true"

    Examples:
      | origin | destination | totalAmountInclVat | totalAmountExVat |
      | NL     | DE          | 300.00             | 300.00           |
      | BE     | DE          | 300.00             | 300.00           |


  # B2.3
    # Wat is bijzonder aan deze regeling?
#  Scenario Outline: A company with registration in the main country delivers goods to a business partner in EU with a tax id
#    Given the company has VAT id "BE12345" in "BE"
#    And A customer has VAT id "DE56789" in "DE"
#    And Country of origin is "<origin>"
#    And Country of destination is "<destination>"
#    And The product category is "EServices"
#    When A "business" invoice is created at "2016-01-01"
#    Then The total amount including VAT is "<totalAmountInclVat>"
#    And The total amount excluding VAT is "<totalAmountExVat>"
#    And The total amount VAT is "<totalAmountVat>"
#    And The VAT amount for percentage "<vatPercentage>" is "<amountVat>"
#    And The vat shifted indicator is "false"
#
#    Examples:
#      | origin | destination | totalAmountInclVat | totalAmountExVat | totalAmountVat | vatPercentage | amountVat |
#      | NL     | DE          | 300.00             | 319.00           | 19.00          | 19.00         | 19.00     |
#      | BE     | DE          | 300.00             | 319.00           | 19.00          | 19.00         | 19.00     |


  # B3.1
  # Goederen vanuit secundare EU land (zonder reg.), naar 3e EU land (exception)
  Scenario: 3.1 A company with registration in the main country delivers goods to a business partner in EU with a tax id
    Given Country of origin is "BE"
    And Country of destination is "DE"
    And A customer has VAT id "DE56789" in "DE"
    And The product category is "Goods"
    When A "business" invoice is created at "2016-01-01"
    Then The total amount including VAT request throws an invoice calculation exception
    Then The total amount excluding VAT request throws an invoice calculation exception
    Then The total amount VAT request throws an invoice calculation exception

  # B3.2
  # Diensten vanuit secundare EU land (zonder reg.), naar 3e EU land (exception)
  Scenario: A company with registration in the main country delivers goods to a business partner in EU with a tax id
    Given Country of origin is "BE"
    And Country of destination is "DE"
    And The product category is "Services"
    And A customer has VAT id "DE56789" in "DE"
    When A "business" invoice is created at "2016-01-01"
    Then The total amount including VAT request throws an invoice calculation exception
    Then The total amount excluding VAT request throws an invoice calculation exception
    Then The total amount VAT request throws an invoice calculation exception

  # B3.3
  # Electronische diensten vanuit secundare EU land (zonder reg.), naar 3e EU land (exception)
  Scenario: A company with registration in the main country delivers goods to a business partner in EU with a tax id
    Given Country of origin is "BE"
    And Country of destination is "DE"
    And The product category is "EServices"
    And A customer has VAT id "DE56789" in "DE"
    When A "business" invoice is created at "2016-01-01"
    Then The total amount including VAT request throws an invoice calculation exception
    Then The total amount excluding VAT request throws an invoice calculation exception
    Then The total amount VAT request throws an invoice calculation exception

  # B4.1
  # Goederen vanuit secundare EU land (met reg.), naar niet-EU land (BTW land A)

  # B4.2
  # Goederen vanuit secundare EU land (zonder reg.), naar niet-EU land (BTW land A)
  Scenario: A company with registration in the main country delivers goods to a business partner in EU with a tax id
    Given Country of origin is "BE"
    And Country of destination is "DE"
    And The product category is "EServices"
    And A customer has VAT id "DE56789" in "DE"
    When A "business" invoice is created at "2016-01-01"
    Then The total amount including VAT request throws an invoice calculation exception
    Then The total amount excluding VAT request throws an invoice calculation exception
    Then The total amount VAT request throws an invoice calculation exception

