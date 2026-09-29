# OSDU Entitlements Service for Azure

[![Release](https://img.shields.io/github/v/release/Azure/osdu-spi-entitlements)](https://github.com/Azure/osdu-spi-entitlements/releases)
[![Validate](https://github.com/Azure/osdu-spi-entitlements/actions/workflows/validate.yml/badge.svg?branch=main)](https://github.com/Azure/osdu-spi-entitlements/actions/workflows/validate.yml)
[![License: Apache 2.0](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](LICENSE)

> [!NOTE]
> Shared service code comes from the [OSDU community upstream](https://community.opengroup.org/osdu/platform/security-and-compliance/entitlements); the [OSDU documentation](https://osdu.pages.opengroup.org/platform/security-and-compliance/entitlements/) covers the API.

Entitlements manages the groups that govern access in OSDU: who belongs to which group, and which groups a caller holds when another service checks authorization.

## At a glance

| | |
|---|---|
| API base path | `/api/entitlements/v2/` |
| Swagger UI | `/api/entitlements/v2/swagger` |
| Health | `:8081/actuator/health` |
| Depends on | Partition |
| Azure resources | Cosmos DB Gremlin graph (group membership), Redis |
| Deployed by | [OSDU SPI Stack](https://github.com/Azure/osdu-spi-stack) (`software/stacks/osdu/services/entitlements.yaml`) |

## Repository layout

[CONTRIBUTING.md](CONTRIBUTING.md) explains where each kind of change belongs.

| Path | Owner | Contents |
|---|---|---|
| `entitlements-v2-core/` | OSDU upstream | Shared service code |
| `provider/entitlements-v2-azure/` | This repository | Azure provider |
| `gremlin-shaded-fix/` | OSDU upstream | Shaded Gremlin driver the Azure provider builds against |
| `entitlements-v2-acceptance-test/` | OSDU upstream | End-to-end suite run against a deployed environment |
| `testing/entitlements-v2-test-azure/` | This repository | Legacy Azure integration tests |
| `.spi/service.yaml` | This repository | How CI deploys and tests the service on SPI Stack |

## Build

Requires Java 17 and Maven 3.6.3+. OSDU dependencies resolve from the public community registry through the settings file in `.mvn`:

```bash
mvn --settings .mvn/community-maven.settings.xml -P core,azure clean install
```

The runnable jar lands at `provider/entitlements-v2-azure/target/entitlements-v2-azure-*-spring-boot.jar`.

## Configuration

SPI Stack sets the service's environment from two places: the shared `osdu-config` ConfigMap and the service's own entry in [`services/entitlements.yaml`](https://github.com/Azure/osdu-spi-stack/blob/main/software/stacks/osdu/services/entitlements.yaml). Those files are the contract; the tables below list what Entitlements actually reads from them.

**Shared, from `osdu-config`:**

| Variable | Purpose |
|---|---|
| `AZURE_TENANT_ID` | Entra tenant |
| `AAD_CLIENT_ID` | Application ID that caller tokens are issued for |
| `KEYVAULT_URI` | Central Key Vault |
| `SERVER_PORT` | HTTP port (`8080`) |
| `APPINSIGHTS_KEY` | Telemetry |

**Specific to Entitlements**, from `services/entitlements.yaml`:

| Variable | Value on SPI Stack | Purpose |
|---|---|---|
| `SERVER_SERVLET_CONTEXTPATH` | `/api/entitlements/v2/` | API base path |
| `AZURE_ISTIOAUTH_ENABLED` | `true` | Trust the mesh's token validation |
| `AZURE_PAAS_WORKLOADIDENTITY_ISENABLED` | `true` | Authenticate to Azure with workload identity |
| `PARTITION_SERVICE_ENDPOINT` | `http://partition/api/partition/v1` | Per-partition resource lookup |
| `SERVICE_DOMAIN_NAME` | `dataservices.energy` | Domain in group emails, as in `users@<partition>.dataservices.energy` |
| `ROOT_DATA_GROUP_QUOTA` | `5000` | Most parents the `users.data.root` group may have |
| `REDIS_TTL_SECONDS` | `1` | Lifetime of cached group lookups |
| `REDIS_DATABASE` | `2` | Redis database index; overrides the `8` in `application.properties` |

The service authenticates to Azure with workload identity, which injects `AZURE_CLIENT_ID` and a federated token; there are no client secrets. Gremlin connections use Entra tokens from that identity. Two endpoints come from central Key Vault rather than the environment: the graph from the secret `graph-db-endpoint` (database `osdu-graph`, collection `Entitlements`), and the Redis host from `redis-hostname`, over TLS on port `6380`.

## Test

| Suite | Where | Runs in CI | Run it yourself |
|---|---|---|---|
| Unit | `entitlements-v2-core`, `provider/entitlements-v2-azure` | Pull requests (Java Build) | `mvn ... install` from [Build](#build) |
| Acceptance | [`entitlements-v2-acceptance-test`](entitlements-v2-acceptance-test/README.md) | Pull requests, against SPI Stack (Deploy and Test) | `spi test entitlements` |
| Integration | `testing/entitlements-v2-test-azure` | No | See below |

CI runs these on pull requests from this repository that change code. Documentation-only changes skip the build, and pull requests from forks build without deploying.

**Acceptance** proves a change on real infrastructure before it merges. It calls the deployed service through the gateway as a privileged test identity and as an ordinary member identity for the forbidden-access cases, and the bindings in `.spi/service.yaml` supply its host, partition, domain, and tokens. Against an environment you are connected to:

```bash
spi test entitlements                   # the image and suite the environment is running
spi test entitlements --source .        # this checkout's suite and descriptor
```

**Integration** is the older Azure suite carried from upstream. It sits outside the root Maven build and expects a client secret for a test service principal plus the object IDs of specific Entra users and groups, none of which SPI Stack issues, so it does not run against SPI Stack today. Acceptance covers the same API surface.

To call the API by hand, `spi token` mints a bearer token:

```bash
curl -H "Authorization: Bearer $(spi token)" -H "data-partition-id: <partition>" \
  https://<gateway>/api/entitlements/v2/groups
```

## Deploy

For a pull request from this repository that changes code, CI publishes the service image and its test suite image, `osdu-spi-entitlements-acceptance`, to GHCR, and the Deploy and Test lane borrows an SPI Stack environment, runs the new image there, proves it with the acceptance suite, and restores the environment's own image. When that lane runs and passes, the change is proven on real infrastructure before it merges; the Validation Summary on the pull request shows whether it ran. This repository does not own infrastructure; SPI Stack does.

To try a build by hand on an environment you are connected to, pin it by digest and release the pin when done:

```bash
spi service pin entitlements --image ghcr.io/azure/osdu-spi-entitlements@sha256:<digest>
spi service reset entitlements
```

## Service notes

**Graph model.** Membership lives in a Cosmos DB Gremlin graph, partitioned by `dataPartitionId`, which every vertex carries.

| Element | Label | Key properties |
|---|---|---|
| Group vertex | `GROUP` | `nodeId` (group email, e.g. `users@opendes.dataservices.energy`), `name`, `description`, `appId` |
| User vertex | `USER` | `nodeId` (user or service principal ID) |
| Parent edge | `parent` | Points from a member (user or group) to the group it belongs to |
| Child edge | `child` | Points from a group to each member; carries `role` |

`role` is `OWNER` or `MEMBER`. A user can be either; a group can only be a `MEMBER` of another group. Every membership is written as a matching parent and child edge pair.

## License

Copyright © Microsoft Corporation

Licensed under the [Apache License 2.0](LICENSE).
