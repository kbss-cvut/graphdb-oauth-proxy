# OntoGrapher Backend

This project proxies a [GraphDB REST API](https://graphdb.ontotext.com/documentation/10.2/clients-and-apis.html) for
OntoGrapher in case GraphDB is secured.

OntoGrapher supports OAuth-based authentication. However, when GraphDB is configured to use OAuth as well, it is not
possible to connect to it from Java. This severely limits its use from a system with Java applications (
like [TermIt](https://github.com/kbss-cvut/termit)).

This project proxies the GraphDB API in the following way:

1. It validates requests against the OAuth service used by OntoGrapher so that it is ensured only authenticated requests
   are processed.
2. It forwards the requests to GraphDB, but instead of using the OAuth access token, it replaces it with Basic
   authentication using the configured credentials. This way, GraphDB can use its internal user database and Java
   applications using RDF4J API can connect to it.

## License

MIT
