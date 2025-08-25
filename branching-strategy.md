# Trunk-Based Branching Strategy

Trunk-based development is a branching strategy where all changes are integrated into a single main branch (often called `main` or `trunk`).

## Workflow Example
1. Branch from `main` using the naming convention below.
2. Make changes and open a pull request (PR) to `main`.
3. Ensure code review and CI checks pass.
4. Merge to `main` and delete the branch.

# Branch Naming Convention for Microservices Monorepo

Consistent branch naming improves collaboration, traceability, and automation in a microservices monorepo that includes code, infrastructure, cloud, local development, observability, and monitoring.

## Format


`<type>/<area>-<service>-<short-description>[-<issue-number>]`

- **type**: `feature`, `fix`, `chore`, `hotfix`, `docs`
- **area**: `code`, `infra`, `cloud`, `local`, `observability`, `monitoring`, `docs`
- **service**: Name of the microservice, module, or documentation section
- **short-description**: Brief summary of the change
- **issue-number** (optional): Reference to ticket or issue (e.g., `-123`)

## Examples

- `feature/code-auth-login`
- `fix/infra-dns-issue-456`
- `chore/local-dev-env`
- `hotfix/monitoring-critical-alert-789`
- `feature/observability-logs-improvement`
- `docs/code-api-docs-update`
- `chore/docs-readme-typo`

## Best Practices

- Use lowercase and hyphens for readability
- Keep branch names concise but descriptive
- Always include the area for clarity (including `docs` for documentation changes)
- Reference issue/ticket numbers when available
- Avoid special characters and spaces

---
Following these conventions helps teams quickly identify the purpose and scope of each branch, streamlines automation, and keeps your monorepo organized.
