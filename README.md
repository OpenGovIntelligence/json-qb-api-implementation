# JSON-qb REST API

Implements the JSON-qb API specification. It aims to provide an easy to use API for developers that reuse statistical data stored in the form of RDF Data cubes. The API implementation can be installed on top of any RDF repository and offer basic and advanced operations on RDF Data Cubes.

## API Description

### GET cubes

Parameter: none

Example request:

`GET http://wapps.islab.uom.gr:8084/JSON-QB-REST-API/cubes`

Example result

```
{
  "cubes": [
    {
      "@id": "http://id.vlaanderen.be/statistieken/dq/kubus-gemiddelde-prijs#id",
      "label": "Cube average price real estate"
    },
    {
      "@id": "http://id.vlaanderen.be/statistieken/dq/kubus-gemiddelde-prijs-1993#id",
      "label": "Cube average price real estate (1993-2013)"
    },
    {
      "@id": "http://id.vlaanderen.be/statistieken/dq/kubus-bouwvergunningen#id",
      "label": "Cube building permits"
    },...
  ]
}
```

### GET dimensions

Parameter: dataset (required)

Example request:

`GET http://wapps.islab.uom.gr:8084/JSON-QB-REST-API/dimensions?dataset=http://statistics.gov.scot/data/economic-activity-benefits-and-tax-credits/employment`

Example result

```
{
  "dimensions": [
    {
      "@id": "http://id.vlaanderen.be/statistieken/def#leeftijdsgroep",
      "label": "Age group"
    },
    {
      "@id": "http://purl.org/linked-data/sdmx/2009/dimension#sex",
      "label": "Geslacht"
    },
    {
      "@id": "http://id.vlaanderen.be/statistieken/def#timePeriod",
      "label": "Period of time"
    },
    {
      "@id": "http://id.vlaanderen.be/statistieken/def#refArea",
      "label": "Reference Area"
    }
  ]
}
```

### GET measures

Parameter: dataset (required)

Example request:

`GET http://wapps.islab.uom.gr:8084/JSON-QB-REST-API/measures?dataset=http://statistics.gov.scot/data/economic-activity-benefits-and-tax-credits/employment`

Example result

```
{
  "measures": [
    {
      "@id": "http://id.vlaanderen.be/statistieken/def#werkzaamheidsgraad",
      "label": "aantal werkzaamheidsgraad"
    }
  ]
}
```

### GET dimension-values

Parameter: dataset (required), dimension (required)

Example request:

`GEThttp://wapps.islab.uom.gr:8084/JSON-QB-REST-API/dimension-values?dataset=http://statistics.gov.scot/data/economic-activity-benefits-and-tax-credits/employment&dimension=http://id.vlaanderen.be/statistieken/def%23timePeriod`

Example result

```
{
  "values": [
    {
      "@id": "http://id.vlaanderen.be/statistieken/concept/jaar_2008#id",
      "label": "2008"
    },
    {
      "@id": "http://id.vlaanderen.be/statistieken/concept/jaar_2009#id",
      "label": "2009"
    },
    {
      "@id": "http://id.vlaanderen.be/statistieken/concept/jaar_2010#id",
      "label": "2010"
    },
    {
      "@id": "http://id.vlaanderen.be/statistieken/concept/jaar_2011#id",
      "label": "2011"
    }, ...
  ],
  "dimension": {
    "@id": "http://id.vlaanderen.be/statistieken/def#timePeriod",
    "label": "Period of time"
  }
}
```
### GET slice

Parameter: dataset (required), measure (required), 0 or more fixed dimension identifiers (optional)


Example request

`GET http://wapps.islab.uom.gr:8084/JSON-QB-REST-API/slice?dataset=http://statistics.gov.scot/data/economic-activity-benefits-and-tax-credits/employment&measure=http://id.vlaanderen.be/statistieken/def%23werkzaamheidsgraad&http://purl.org/linked-data/sdmx/2009/dimension%23sex=http://purl.org/linked-data/sdmx/2009/code%23sex-F&http://id.vlaanderen.be/statistieken/def%23leeftijdsgroep=http://id.vlaanderen.be/statistieken/concept/leeftijdsgroep_35-49%23id`

Example result
```
{
  "observations": [
    {
      "http://id.vlaanderen.be/statistieken/def#refArea": "http://statistics.gov.scot/id/statistical-geography/S12000005",
      "http://id.vlaanderen.be/statistieken/def#timePeriod": "http://id.vlaanderen.be/statistieken/concept/jaar_2004#id",
      "http://id.vlaanderen.be/statistieken/def#werkzaamheidsgraad": "79.1",
      "@id": "http://statistics.gov.scot/data/economic-activity-benefits-and-tax-credits/employment/year/2004/S12000005/gender/females/age/35-49/population-group/all/percentage-of-population/ratio"
    },
    {
      "http://id.vlaanderen.be/statistieken/def#refArea": "http://statistics.gov.scot/id/statistical-geography/S12000006",
      "http://id.vlaanderen.be/statistieken/def#timePeriod": "http://id.vlaanderen.be/statistieken/concept/jaar_2004#id",
      "http://id.vlaanderen.be/statistieken/def#werkzaamheidsgraad": "84.8",
      "@id": "http://statistics.gov.scot/data/economic-activity-benefits-and-tax-credits/employment/year/2004/S12000006/gender/females/age/35-49/population-group/all/percentage-of-population/ratio"
    },
    {
      "http://id.vlaanderen.be/statistieken/def#refArea": "http://statistics.gov.scot/id/statistical-geography/S12000008",
      "http://id.vlaanderen.be/statistieken/def#timePeriod": "http://id.vlaanderen.be/statistieken/concept/jaar_2004#id",
      "http://id.vlaanderen.be/statistieken/def#werkzaamheidsgraad": "78.2",
      "@id": "http://statistics.gov.scot/data/economic-activity-benefits-and-tax-credits/employment/year/2004/S12000008/gender/females/age/35-49/population-group/all/percentage-of-population/ratio"
    },
    {
      "http://id.vlaanderen.be/statistieken/def#refArea": "http://statistics.gov.scot/id/statistical-geography/S12000010",
      "http://id.vlaanderen.be/statistieken/def#timePeriod": "http://id.vlaanderen.be/statistieken/concept/jaar_2004#id",
      "http://id.vlaanderen.be/statistieken/def#werkzaamheidsgraad": "77.9",
      "@id": "http://statistics.gov.scot/data/economic-activity-benefits-and-tax-credits/employment/year/2004/S12000010/gender/females/age/35-49/population-group/all/percentage-of-population/ratio"
    },...
  ]
}
```

### GET table

Parameter: dataset(required), row (required), col (required), measure (required), 0 or more fixed dimension identifiers (optional)


Example request

`GET http://wapps.islab.uom.gr:8084/JSON-QB-REST-API/table?dataset=http://statistics.gov.scot/data/economic-activity-benefits-and-tax-credits/employment&measure=http://id.vlaanderen.be/statistieken/def%23werkzaamheidsgraad&col=http://id.vlaanderen.be/statistieken/def%23refArea&row=http://id.vlaanderen.be/statistieken/def%23timePeriod&http://purl.org/linked-data/sdmx/2009/dimension%23sex=http://purl.org/linked-data/sdmx/2009/code%23sex-F&http://id.vlaanderen.be/statistieken/def%23leeftijdsgroep=http://id.vlaanderen.be/statistieken/concept/leeftijdsgroep_35-49%23id`

Example result
```
{
  "structure": {
    "free_dimensions": {
      "timePeriod": {
        "@id": "http://id.vlaanderen.be/statistieken/def#timePeriod",
        "label": "Period of time"
      },
      "refArea": {
        "@id": "http://id.vlaanderen.be/statistieken/def#refArea",
        "label": "Reference Area"
      }
    },
    "locked_dimensions": {
      "sex": {
        "@id": "http://purl.org/linked-data/sdmx/2009/dimension#sex",
        "label": "Geslacht",
        "locked_value": {
          "@id": "http://purl.org/linked-data/sdmx/2009/code#sex-F",
          "label": "sex-F"
        }
      },
      "leeftijdsgroep": {
        "@id": "http://id.vlaanderen.be/statistieken/def#leeftijdsgroep",
        "label": "Age group",
        "locked_value": {
          "@id": "http://id.vlaanderen.be/statistieken/concept/leeftijdsgroep_35-49#id",
          "label": "35-49"
        }
      }
    },
    "dimension_values": {
      "refArea": {
        "S12000033": {
          "@id": "http://statistics.gov.scot/id/statistical-geography/S12000033",
          "label": "Aberdeen City"
        },
        "S12000034": {
          "@id": "http://statistics.gov.scot/id/statistical-geography/S12000034",
          "label": "Aberdeenshire"
        },...       
      },
      "timePeriod": {
        "jaar_2004": {
          "@id": "http://id.vlaanderen.be/statistieken/concept/jaar_2004#id",
          "label": "2004"
        },
        "jaar_2005": {
          "@id": "http://id.vlaanderen.be/statistieken/concept/jaar_2005#id",
          "label": "2005"
        },...     
      }
    }
  },
  "headers": {
    "columns": {
      "refArea": ["S12000033", "S12000034", "S12000041","S12000035","S12000036","S12000005","S12000013",... ]
    },
    "rows": {"timePeriod": ["jaar_2004", "jaar_2005","jaar_2006","jaar_2007", "jaar_2008","jaar_2009", "jaar_2010","jaar_2011"]
    }
  },
  "data": [[73.4,79.6, 77.6, 78.3, 74.4, 79.1, 82.8, ...], [76.6,78.8, 74.5, 84.3, 80.0, 76.4, 89.3,...],...]
 }

```
