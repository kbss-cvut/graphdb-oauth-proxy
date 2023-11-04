# GraphDB OAuth Proxy

This project proxies the [GraphDB REST API](https://graphdb.ontotext.com/documentation/10.2/clients-and-apis.html) for 
applications that want to use OAuth authentication while retaining RDF4J-based access to the GraphDB instance.

While GraphDB supports OAuth authentication, enabling it disables Basic authentication which is used by the RDF4J API.
If one has a system consisting of multiple applications secured with OAuth and some of them need to access GraphDB via
RDF4J API, GraphDB itself cannot be secured with OAuth.

This project proxies the GraphDB API in the following way:

1. It validates requests against the configured OAuth service so that it is ensured only authenticated requests
   are processed.
2. It forwards the requests to GraphDB, but instead of using the OAuth access token, it replaces it with Basic
   authentication using the configured credentials. This way, GraphDB can use its internal user database and Java
   applications using RDF4J API can connect to it.

## Configuration

The following environment variables should be used to configure the application.

| Parameter                                             | Description                                                                                                   |
|:------------------------------------------------------|:--------------------------------------------------------------------------------------------------------------|
| `PROXY_URL`                                           | URL of the proxy itself. Used to rewrite location headers when request URLs do not match reality.             |
| `PROXY_GRAPHDB_URL`                                   | URL of the GraphDB server. Without specifying any particular repository (that is taken from the request URL). |
| `PROXY_GRAPHDB_USERNAME`                              | Username to authenticate GraphDB access with.                                                                 |
| `PROXY_GRAPHDB_PASSWORD`                              | Password to authenticate GraphDB access with.                                                                 |
| `SPRING_SECURITY_OAUTH2_RESOURCESERVER_JWT_ISSUERURI` | URL of the OAuth token issuer.                                                                                |

## License

MIT
