# Notes on Super-structured Data: Rethinking the Schema, by Brim

[The article](https://www.brimdata.io/blog/super-structured-data/)

* The problem: dealing with real world data. Dealing with transformations, document-style, table style.
* The Authoritarian's way. Cleansing data, putting it in DWH, relational schemas. _Must conform_. Hard!
* The Anarchist's way. Schema-less. leads to mess in real world deployments.
* Pushing schemas upstream: Avro, Parquet [seems to suggest this is driven by the data people, which I don't think is true.]
* Super structured:
> Instead of pre-defining schemas to which all values must conform, data should instead be self-describing and organized around a deep type system, allowing each value to freely express its structure through its explicit type.
