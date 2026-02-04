# Java Hello World Backend Service Template

This project is a template for creating Java gRPC based microservices using Spring. It includes a basic structure with common configurations and dependencies to help you get started quickly.

[![Continuous Integration (CI)](https://github.com/maze-technology/java-hello-world-backend-template/actions/workflows/publish.yaml/badge.svg?branch=main)](https://github.com/maze-technology/java-hello-world-backend-template/actions/workflows/publish.yaml)

## Quality

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=maze-technology_java-hello-world-backend-template&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=maze-technology_java-hello-world-backend-template)
[![Lines of Code](https://sonarcloud.io/api/project_badges/measure?project=maze-technology_java-hello-world-backend-template&metric=ncloc)](https://sonarcloud.io/summary/new_code?id=maze-technology_java-hello-world-backend-template)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=maze-technology_java-hello-world-backend-template&metric=reliability_rating)](https://sonarcloud.io/summary/new_code?id=maze-technology_java-hello-world-backend-template)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=maze-technology_java-hello-world-backend-template&metric=sqale_rating)](https://sonarcloud.io/summary/new_code?id=maze-technology_java-hello-world-backend-template)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=maze-technology_java-hello-world-backend-template&metric=security_rating)](https://sonarcloud.io/summary/new_code?id=maze-technology_java-hello-world-backend-template)
[![Technical Debt](https://sonarcloud.io/api/project_badges/measure?project=maze-technology_java-hello-world-backend-template&metric=sqale_index)](https://sonarcloud.io/summary/new_code?id=maze-technology_java-hello-world-backend-template)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=maze-technology_java-hello-world-backend-template&metric=coverage)](https://sonarcloud.io/summary/new_code?id=maze-technology_java-hello-world-backend-template)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=maze-technology_java-hello-world-backend-template&metric=bugs)](https://sonarcloud.io/summary/new_code?id=maze-technology_java-hello-world-backend-template)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=maze-technology_java-hello-world-backend-template&metric=vulnerabilities)](https://sonarcloud.io/summary/new_code?id=maze-technology_java-hello-world-backend-template)
[![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=maze-technology_java-hello-world-backend-template&metric=code_smells)](https://sonarcloud.io/summary/new_code?id=maze-technology_java-hello-world-backend-template)

## Commands

Check [Makefile](Makefile)

## Configuration

Configuration files are located in the `src/main/resources` directory. Modify `application-local.yml` to suit your needs.

### Configure the GitHub Repository

1. Go to your GitHub repository settings.
2. Navigate to the "Secrets and variables" section and add the following:

   **Secrets:**

   - `DOCKER_PASSWORD` (Your Docker Hub password)

   **Variables:**

   - `DOCKER_USERNAME` (Your Docker Hub username)
   - `DOCKER_REPOSITORY` (The Docker Hub repository where the built image should be pushed by the workflow)
